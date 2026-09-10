package com.villaserena.museo.service;

import com.villaserena.museo.dto.RichiestaFerieDTO;
import com.villaserena.museo.dto.RichiestaFerieRequest;
import com.villaserena.museo.model.Dipendente;
import com.villaserena.museo.model.RichiestaFerie;
import com.villaserena.museo.model.Utente;
import com.villaserena.museo.repository.DipendenteRepository;
import com.villaserena.museo.repository.RichiestaFerieRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FerieService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final RichiestaFerieRepository richiestaFerieRepository;
    private final DipendenteRepository dipendenteRepository;
    private final NotificaAppService notificaAppService;
    private final TurniService turniService;

    public FerieService(RichiestaFerieRepository richiestaFerieRepository,
                        DipendenteRepository dipendenteRepository,
                        NotificaAppService notificaAppService,
                        TurniService turniService) {
        this.richiestaFerieRepository = richiestaFerieRepository;
        this.dipendenteRepository = dipendenteRepository;
        this.notificaAppService = notificaAppService;
        this.turniService = turniService;
    }

    public RichiestaFerieDTO crea(RichiestaFerieRequest request) {
        if (request.getDataFine().isBefore(request.getDataInizio())) {
            throw new RuntimeException("La data di fine non può precedere la data di inizio");
        }
        if (request.getTipo() == RichiestaFerie.Tipo.PERMESSO) {
            if (request.getMotivo() == null || request.getMotivo().isBlank()) {
                throw new RuntimeException("Il motivo è obbligatorio per un permesso");
            }
            if (request.getOraInizio() == null || request.getOraFine() == null) {
                throw new RuntimeException("Indicare l'orario di inizio e fine del permesso");
            }
            if (!request.getOraFine().isAfter(request.getOraInizio())) {
                throw new RuntimeException("L'ora di fine deve essere successiva all'ora di inizio");
            }
        }

        Dipendente dipendente = dipendenteCorrente();

        RichiestaFerie richiesta = new RichiestaFerie();
        richiesta.setDipendente(dipendente);
        richiesta.setTipo(request.getTipo());
        richiesta.setDataInizio(request.getDataInizio());
        richiesta.setDataFine(request.getDataFine());
        if (request.getTipo() == RichiestaFerie.Tipo.PERMESSO) {
            richiesta.setOraInizio(request.getOraInizio());
            richiesta.setOraFine(request.getOraFine());
        }
        richiesta.setMotivo(request.getMotivo());

        RichiestaFerie salvata = richiestaFerieRepository.save(richiesta);

        // Notifica tutti gli HR che c'è una nuova richiesta da valutare
        String nomeDipendente = dipendente.getUtente().getNome() + " " + dipendente.getUtente().getCognome();
        String tipoTesto = request.getTipo() == RichiestaFerie.Tipo.FERIE ? "ferie" : "un permesso";
        dipendenteRepository.findAll().stream()
            .filter(d -> d.getUtente().getRuolo() == Utente.Ruolo.HR)
            .forEach(hr -> notificaAppService.crea(hr.getUtente(),
                    nomeDipendente + " ha richiesto " + tipoTesto + " dal " + request.getDataInizio().format(FORMATO_DATA),
                    "/admin/ferie"));

        return RichiestaFerieDTO.daEntita(salvata);
    }

    public List<RichiestaFerieDTO> mie() {
        Dipendente dipendente = dipendenteCorrente();
        return richiestaFerieRepository.findAll().stream()
                .filter(r -> r.getDipendente().getId().equals(dipendente.getId()))
                .map(RichiestaFerieDTO::daEntita)
                .collect(Collectors.toList());
    }

    public List<RichiestaFerieDTO> findAll() {
        return richiestaFerieRepository.findAll().stream()
                .map(RichiestaFerieDTO::daEntita)
                .collect(Collectors.toList());
    }

    public RichiestaFerieDTO aggiornaStato(Long id, RichiestaFerie.Stato nuovoStato) {
        RichiestaFerie richiesta = richiestaFerieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Richiesta non trovata"));

        Dipendente responsabile = dipendenteCorrente();
        richiesta.setStato(nuovoStato);
        richiesta.setApprovataDa(responsabile);
        RichiestaFerie salvata = richiestaFerieRepository.save(richiesta);

        String esito = nuovoStato == RichiestaFerie.Stato.APPROVATA ? "approvata" : "rifiutata";
        notificaAppService.crea(richiesta.getDipendente().getUtente(),
        "La tua richiesta del " + richiesta.getDataInizio().format(FORMATO_DATA) + " è stata " + esito,
        "/area-personale");

        RichiestaFerieDTO dto = RichiestaFerieDTO.daEntita(salvata);

        // Se la richiesta è stata approvata, avvisa l'HR di eventuali turni
        // già pianificati in quel periodo, che ora andrebbero rivisti a mano.
        if (nuovoStato == RichiestaFerie.Stato.APPROVATA) {
            var turniInConflitto = turniService.turniInConflittoConPeriodo(
                    richiesta.getDipendente().getId(), richiesta.getDataInizio(), richiesta.getDataFine());
            if (!turniInConflitto.isEmpty()) {
                dto.setAvvisoTurniInConflitto(
                    "Attenzione: il dipendente ha " + turniInConflitto.size() +
                    " turno/i già pianificato/i in questo periodo, da rivedere manualmente."
                );
            }
        }

        return dto;
    }

    private Dipendente dipendenteCorrente() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return dipendenteRepository.findAll().stream()
                .filter(d -> d.getUtente().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Profilo dipendente non trovato per l'utente corrente"));
    }
}