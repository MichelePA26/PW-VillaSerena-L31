package com.villaserena.museo.service;

import com.villaserena.museo.dto.EventoDTO;
import com.villaserena.museo.model.Evento;
import com.villaserena.museo.model.Pagamento;
import com.villaserena.museo.model.Prenotazione;
import com.villaserena.museo.repository.EventoRepository;
import com.villaserena.museo.repository.PagamentoRepository;
import com.villaserena.museo.repository.PrenotazioneRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventiService {

    private final EventoRepository eventoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final NotificaAppService notificaAppService;
    private final PagamentoRepository pagamentoRepository;
    private final PagamentiService pagamentiService;

    public EventiService(EventoRepository eventoRepository,
                        PrenotazioneRepository prenotazioneRepository,
                        NotificaAppService notificaAppService,
                        PagamentoRepository pagamentoRepository,
                        PagamentiService pagamentiService) {
        this.eventoRepository = eventoRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.notificaAppService = notificaAppService;
        this.pagamentoRepository = pagamentoRepository;
        this.pagamentiService = pagamentiService;
    }

    public List<EventoDTO> findAll() {
        return eventoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public EventoDTO create(EventoDTO dto) {
        Evento evento = new Evento();
        applica(dto, evento);
        return toDTO(eventoRepository.save(evento));
    }

    public void delete(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));

        boolean eventoConcluso = evento.getDataFine().isBefore(java.time.LocalDateTime.now());
        boolean eventoAnnullato = evento.getStato() == Evento.Stato.ANNULLATO;

        if (!eventoConcluso && !eventoAnnullato) {
            throw new RuntimeException("Non è possibile eliminare un evento futuro o ancora in corso, salvo sia già stato annullato.");
        }

        boolean haPrenotazioni = prenotazioneRepository.findAll().stream()
                .anyMatch(p -> p.getEvento().getId().equals(id));

        if (haPrenotazioni) {
            throw new RuntimeException(
                "Non è possibile eliminare questo evento: sono presenti prenotazioni o feedback collegati."
            );
        }

        eventoRepository.deleteById(id);
    }

    private void applica(EventoDTO dto, Evento evento) {
        evento.setTitolo(dto.getTitolo());
        evento.setDescrizione(dto.getDescrizione());
        evento.setTipo(dto.getTipo());
        evento.setDataInizio(dto.getDataInizio());
        evento.setDataFine(dto.getDataFine());
        evento.setCapienzaMax(dto.getCapienzaMax());
        evento.setPrezzo(dto.getPrezzo());
        if (dto.getStato() != null) {
            evento.setStato(dto.getStato());
        }
    }

    private EventoDTO toDTO(Evento e) {
        EventoDTO dto = new EventoDTO();
        dto.setId(e.getId());
        dto.setTitolo(e.getTitolo());
        dto.setDescrizione(e.getDescrizione());
        dto.setTipo(e.getTipo());
        dto.setDataInizio(e.getDataInizio());
        dto.setDataFine(e.getDataFine());
        dto.setCapienzaMax(e.getCapienzaMax());
        dto.setStato(e.getStato());
        dto.setPrezzo(e.getPrezzo());
        return dto;
    }

    public EventoDTO update(Long id, EventoDTO dto) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));
        applica(dto, evento);
        return toDTO(eventoRepository.save(evento));
    }

    public EventoDTO cambiaStato(Long id, Evento.Stato nuovoStato) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));

        evento.setStato(nuovoStato);
        Evento salvato = eventoRepository.save(evento);

        List<Prenotazione> prenotazioniAttive = prenotazioneRepository.findAll().stream()
                .filter(p -> p.getEvento().getId().equals(id))
                .filter(p -> p.getStato() == Prenotazione.Stato.CONFERMATA || p.getStato() == Prenotazione.Stato.IN_ATTESA_MIGRAZIONE)
                .toList();

        if (nuovoStato == Evento.Stato.DA_RIPROGRAMMARE) {
            for (Prenotazione p : prenotazioniAttive) {
                p.setStato(Prenotazione.Stato.IN_ATTESA_MIGRAZIONE);
                p.setDataScadenzaRisposta(java.time.LocalDateTime.now().plusDays(7));
                prenotazioneRepository.save(p);
                notificaAppService.crea(p.getUtente(),
                        "L'evento \"" + evento.getTitolo() + "\" è stato rinviato: scegli se accettare la nuova data o richiedere il rimborso",
                        "/le-mie-prenotazioni");
            }
        } else if (nuovoStato == Evento.Stato.ANNULLATO) {
            for (Prenotazione p : prenotazioniAttive) {
                var pagamentoEsistente = pagamentoRepository.findByPrenotazioneId(p.getId())
                        .filter(pag -> pag.getStato() == Pagamento.Stato.COMPLETATO);

                if (pagamentoEsistente.isPresent()) {
                    pagamentiService.rimborsa(pagamentoEsistente.get());
                    p.setStato(Prenotazione.Stato.RIMBORSATA);
                } else {
                    p.setStato(Prenotazione.Stato.ANNULLATA);
                }
                prenotazioneRepository.save(p);

                notificaAppService.crea(p.getUtente(),
                        "L'evento \"" + evento.getTitolo() + "\" è stato annullato" +
                        (pagamentoEsistente.isPresent() ? ": il pagamento è stato rimborsato" : ""),
                        "/le-mie-prenotazioni");
            }
        }

        return toDTO(salvato);
    }
}