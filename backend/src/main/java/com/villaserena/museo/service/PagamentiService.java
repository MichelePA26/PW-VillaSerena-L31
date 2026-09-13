package com.villaserena.museo.service;

import com.villaserena.museo.model.Pagamento;
import com.villaserena.museo.model.Prenotazione;
import com.villaserena.museo.repository.PagamentoRepository;
import com.villaserena.museo.repository.PrenotazioneRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PagamentiService {

    private final PayPalService payPalService;
    private final PagamentoRepository pagamentoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final NotificaAppService notificaAppService;

    public PagamentiService(PayPalService payPalService, PagamentoRepository pagamentoRepository,
                             PrenotazioneRepository prenotazioneRepository, NotificaAppService notificaAppService) {
        this.payPalService = payPalService;
        this.pagamentoRepository = pagamentoRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.notificaAppService = notificaAppService;
    }

    // L'importo NON arriva dal client: si ricalcola sempre da prenotazione -> evento.prezzo
    public Map<String, Object> creaOrdine(Long prenotazioneId) {
        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));

        BigDecimal prezzoUnitario = prenotazione.getEvento().getPrezzo();
        if (prezzoUnitario == null || prezzoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Questo evento non richiede pagamento");
        }
        BigDecimal importo = prezzoUnitario.multiply(BigDecimal.valueOf(prenotazione.getNumeroPosti()));

        Map<String, Object> ordinePaypal = payPalService.creaOrdine(importo, "EUR", prenotazioneId);

        Pagamento pagamento = pagamentoRepository.findByPrenotazioneId(prenotazioneId).orElse(new Pagamento());
        pagamento.setPrenotazione(prenotazione);
        pagamento.setImporto(importo);
        pagamento.setIdOrdinePaypal((String) ordinePaypal.get("id"));
        pagamento.setStato(Pagamento.Stato.IN_ATTESA);
        pagamentoRepository.save(pagamento);

        return ordinePaypal;
    }

    @SuppressWarnings("unchecked")
    public Pagamento catturaOrdine(String orderId) {
        Map<String, Object> risposta = payPalService.catturaOrdine(orderId);
        String status = (String) risposta.get("status");

        Pagamento pagamento = pagamentoRepository.findByIdOrdinePaypal(orderId)
                .orElseThrow(() -> new RuntimeException("Pagamento non trovato per l'ordine " + orderId));

        if ("COMPLETED".equals(status)) {
            // Estrae l'id della "cattura", necessario in futuro per un eventuale rimborso
            String idCattura = estraiIdCattura(risposta);
            pagamento.setIdCatturaPaypal(idCattura);
            pagamento.setStato(Pagamento.Stato.COMPLETATO);
            pagamentoRepository.save(pagamento);

            Prenotazione prenotazione = pagamento.getPrenotazione();
            prenotazione.setStato(Prenotazione.Stato.CONFERMATA);
            prenotazioneRepository.save(prenotazione);

            notificaAppService.crea(prenotazione.getUtente(),
                    "Pagamento ricevuto per \"" + prenotazione.getEvento().getTitolo() + "\": prenotazione confermata",
                    "/le-mie-prenotazioni");
        } else {
            pagamento.setStato(Pagamento.Stato.FALLITO);
            pagamentoRepository.save(pagamento);
        }
        return pagamento;
    }

    @SuppressWarnings("unchecked")
    private String estraiIdCattura(Map<String, Object> rispostaCattura) {
        try {
            var purchaseUnits = (List<Map<String, Object>>) rispostaCattura.get("purchase_units");
            var payments = (Map<String, Object>) purchaseUnits.get(0).get("payments");
            var captures = (List<Map<String, Object>>) payments.get("captures");
            return (String) captures.get(0).get("id");
        } catch (Exception e) {
            return null;
        }
    }

    // Rimborsa un pagamento già completato (usato dal flusso di riprogrammazione/annullamento)
    public void rimborsa(Pagamento pagamento) {
        if (pagamento.getIdCatturaPaypal() == null) {
            throw new RuntimeException("Impossibile rimborsare: nessuna cattura PayPal registrata");
        }
        payPalService.rimborsaCattura(pagamento.getIdCatturaPaypal());
        pagamento.setStato(Pagamento.Stato.RIMBORSATO);
        pagamentoRepository.save(pagamento);
    }
}