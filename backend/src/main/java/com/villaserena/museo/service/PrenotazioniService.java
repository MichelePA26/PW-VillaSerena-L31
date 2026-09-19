package com.villaserena.museo.service;

import com.villaserena.museo.dto.PrenotazioneDTO;
import com.villaserena.museo.dto.PrenotazioneRequest;
import com.villaserena.museo.model.Evento;
import com.villaserena.museo.model.Pagamento;
import com.villaserena.museo.model.Prenotazione;
import com.villaserena.museo.model.Utente;
import com.villaserena.museo.repository.EventoRepository;
import com.villaserena.museo.repository.PrenotazioneRepository;
import com.villaserena.museo.repository.UtenteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.villaserena.museo.repository.PagamentoRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrenotazioniService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final EventoRepository eventoRepository;
    private final UtenteRepository utenteRepository;
    private final PagamentoRepository pagamentoRepository;
    private final PagamentiService pagamentiService;
    private final NotificaAppService notificaAppService;

    public PrenotazioniService(PrenotazioneRepository prenotazioneRepository,
                                EventoRepository eventoRepository,
                                UtenteRepository utenteRepository,
                                PagamentoRepository pagamentoRepository,
                                PagamentiService pagamentiService,
                                NotificaAppService notificaAppService) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.eventoRepository = eventoRepository;
        this.utenteRepository = utenteRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.pagamentiService = pagamentiService;
        this.notificaAppService = notificaAppService;
    }

    public PrenotazioneDTO crea(PrenotazioneRequest request) {
        Evento evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));
        Utente utente = utenteAutenticato();

        int postiOccupati = prenotazioneRepository.findAll().stream()
            .filter(p -> p.getEvento().getId().equals(evento.getId())
                    && (p.getStato() == Prenotazione.Stato.CONFERMATA || p.getStato() == Prenotazione.Stato.IN_ATTESA_PAGAMENTO))
            .mapToInt(Prenotazione::getNumeroPosti)
            .sum();

        if (postiOccupati + request.getNumeroPosti() > evento.getCapienzaMax()) {
            throw new RuntimeException("Capienza massima superata per l'evento selezionato");
        }

        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setEvento(evento);
        prenotazione.setUtente(utente);
        prenotazione.setNumeroPosti(request.getNumeroPosti());

        boolean eventoAPagamento = evento.getPrezzo() != null && evento.getPrezzo().compareTo(java.math.BigDecimal.ZERO) > 0;
        prenotazione.setStato(eventoAPagamento ? Prenotazione.Stato.IN_ATTESA_PAGAMENTO : Prenotazione.Stato.CONFERMATA);

        // Primo salvataggio serve per ottenere l'id dal db
        Prenotazione salvata = prenotazioneRepository.save(prenotazione);

        // Ora che id esiste, componiamo il codice biglietto
        salvata.setCodiceBiglietto("VS-" + evento.getId() + "-" + salvata.getId());
        salvata = prenotazioneRepository.save(salvata);
        
        return PrenotazioneDTO.daEntita(salvata);
    }

    // Le prenotazioni dell'utente attualmente autenticato (non più "di un ID a scelta")
    public List<PrenotazioneDTO> mie() {
        Utente utente = utenteAutenticato();
        return prenotazioneRepository.findAll().stream()
                .filter(p -> p.getUtente().getId().equals(utente.getId()))
                .map(PrenotazioneDTO::daEntita)
                .collect(Collectors.toList());
    }
   
    private Utente utenteAutenticato() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente autenticato non trovato"));
    }
    
    public PrenotazioneDTO accettaNuovaData(Long prenotazioneId) {
        Prenotazione p = prenotazioneCorrenteDiProprieta(prenotazioneId);
        if (p.getStato() != Prenotazione.Stato.IN_ATTESA_MIGRAZIONE) {
            throw new RuntimeException("Questa prenotazione non è in attesa di una decisione");
        }
        p.setStato(Prenotazione.Stato.CONFERMATA);
        p.setDecisoDa(Prenotazione.DecisoDa.UTENTE);
        p.setDataScadenzaRisposta(null);
        return PrenotazioneDTO.daEntita(prenotazioneRepository.save(p));
    }


    public PrenotazioneDTO richiediRimborso(Long prenotazioneId) {
        Prenotazione p = prenotazioneCorrenteDiProprieta(prenotazioneId);
        if (p.getStato() != Prenotazione.Stato.IN_ATTESA_MIGRAZIONE) {
            throw new RuntimeException("Questa prenotazione non è in attesa di una decisione");
        }

        var pagamentoEsistente = pagamentoRepository.findByPrenotazioneId(prenotazioneId)
                .filter(pag -> pag.getStato() == Pagamento.Stato.COMPLETATO);

        if (pagamentoEsistente.isPresent()) {
            pagamentiService.rimborsa(pagamentoEsistente.get());
            p.setStato(Prenotazione.Stato.RIMBORSATA);
        } else {
            p.setStato(Prenotazione.Stato.ANNULLATA);
        }
        p.setDecisoDa(Prenotazione.DecisoDa.UTENTE);
        p.setDataScadenzaRisposta(null);
        return PrenotazioneDTO.daEntita(prenotazioneRepository.save(p));
    }

    private Prenotazione prenotazioneCorrenteDiProprieta(Long prenotazioneId) {
        Utente utente = utenteAutenticato();
        Prenotazione p = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
        if (!p.getUtente().getId().equals(utente.getId())) {
            throw new RuntimeException("Non puoi gestire una prenotazione non tua");
        }
        return p;
    }

    public void annullaNonPagata(Long prenotazioneId) {
        Prenotazione p = prenotazioneCorrenteDiProprieta(prenotazioneId);
        if (p.getStato() != Prenotazione.Stato.IN_ATTESA_PAGAMENTO) {
            throw new RuntimeException("Questa prenotazione non può essere annullata in questo stato");
        }
        p.setStato(Prenotazione.Stato.ANNULLATA);
        prenotazioneRepository.save(p);
    }

    // Vista d'insieme per Operatore/HR
    public List<PrenotazioneDTO> findAll(Long eventoId) {
        return prenotazioneRepository.findAll().stream()
                .filter(p -> eventoId == null || p.getEvento().getId().equals(eventoId))
                .sorted((a, b) -> b.getDataPrenotazione().compareTo(a.getDataPrenotazione()))
                .map(PrenotazioneDTO::daEntita)
                .collect(Collectors.toList());
    }

    // Ricerca per il check-in in biglietteria
    public PrenotazioneDTO cercaPerCodiceBiglietto(String codice) {
        Prenotazione p = prenotazioneRepository.findAll().stream()
                .filter(pr -> codice.equalsIgnoreCase(pr.getCodiceBiglietto()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nessuna prenotazione trovata con questo codice"));
        return PrenotazioneDTO.daEntita(p);
    }

    public PrenotazioneDTO effettuaCheckIn(String codice) {
        Prenotazione p = prenotazioneRepository.findAll().stream()
                .filter(pr -> codice.equalsIgnoreCase(pr.getCodiceBiglietto()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nessuna prenotazione trovata con questo codice"));

        if (p.getStato() != Prenotazione.Stato.CONFERMATA) {
            throw new RuntimeException("Questo biglietto non è valido (prenotazione non confermata)");
        }
        if (p.isCheckInEffettuato()) {
            throw new RuntimeException("Biglietto già utilizzato il " + p.getDataOraCheckin());
        }

        p.setCheckInEffettuato(true);
        p.setDataOraCheckin(java.time.LocalDateTime.now());
        return PrenotazioneDTO.daEntita(prenotazioneRepository.save(p));
    }

    public PrenotazioneDTO risolviDOfficio(Long prenotazioneId, String decisione) {
        Prenotazione p = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));

        if (p.getStato() != Prenotazione.Stato.IN_ATTESA_MIGRAZIONE) {
            throw new RuntimeException("Questa prenotazione non è in attesa di una decisione");
        }
        if (p.getDataScadenzaRisposta() == null || p.getDataScadenzaRisposta().isAfter(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Il termine per la risposta del cliente non è ancora scaduto");
        }

        if ("ACCETTA".equalsIgnoreCase(decisione)) {
            p.setStato(Prenotazione.Stato.CONFERMATA);
            p.setDecisoDa(Prenotazione.DecisoDa.OPERATORE);
            p.setDataScadenzaRisposta(null);
            prenotazioneRepository.save(p);
            notificaAppService.crea(p.getUtente(),
                    "Il nostro staff ha confermato la tua prenotazione per \"" + p.getEvento().getTitolo() + "\" alla nuova data",
                    "/le-mie-prenotazioni");

        } else if ("RIMBORSA".equalsIgnoreCase(decisione)) {
            var pagamentoEsistente = pagamentoRepository.findByPrenotazioneId(prenotazioneId)
                    .filter(pag -> pag.getStato() == Pagamento.Stato.COMPLETATO);

            if (pagamentoEsistente.isPresent()) {
                pagamentiService.rimborsa(pagamentoEsistente.get());
                p.setStato(Prenotazione.Stato.RIMBORSATA);
            } else {
                p.setStato(Prenotazione.Stato.ANNULLATA);
            }
            p.setDecisoDa(Prenotazione.DecisoDa.OPERATORE);
            p.setDataScadenzaRisposta(null);
            prenotazioneRepository.save(p);

            notificaAppService.crea(p.getUtente(),
                    "Il nostro staff ha gestito la tua prenotazione per \"" + p.getEvento().getTitolo() + "\": " +
                    (pagamentoEsistente.isPresent() ? "pagamento rimborsato" : "prenotazione annullata"),
                    "/le-mie-prenotazioni");
        } else {
            throw new RuntimeException("Decisione non valida");
        }

        return PrenotazioneDTO.daEntita(p);
    }

}