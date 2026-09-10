package com.villaserena.museo.service;

import com.villaserena.museo.dto.DashboardDTO;
import com.villaserena.museo.dto.FeedbackDTO;
import com.villaserena.museo.model.*;
import com.villaserena.museo.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("d MMM", Locale.ITALIAN);

    private final OperaRepository operaRepository;
    private final CollezioneRepository collezioneRepository;
    private final EventoRepository eventoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;
    private final FeedbackRepository feedbackRepository;
    private final DipendenteRepository dipendenteRepository;
    private final RichiestaFerieRepository richiestaFerieRepository;
    private final TurnoRepository turnoRepository;

    public DashboardService(OperaRepository operaRepository, CollezioneRepository collezioneRepository,
                             EventoRepository eventoRepository, PrenotazioneRepository prenotazioneRepository,
                             UtenteRepository utenteRepository, FeedbackRepository feedbackRepository,
                             DipendenteRepository dipendenteRepository, RichiestaFerieRepository richiestaFerieRepository,
                             TurnoRepository turnoRepository) {
        this.operaRepository = operaRepository;
        this.collezioneRepository = collezioneRepository;
        this.eventoRepository = eventoRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
        this.feedbackRepository = feedbackRepository;
        this.dipendenteRepository = dipendenteRepository;
        this.richiestaFerieRepository = richiestaFerieRepository;
        this.turnoRepository = turnoRepository;
    }

    public DashboardDTO calcola() {
        DashboardDTO dto = new DashboardDTO();

        List<Evento> eventi = eventoRepository.findAll();
        List<Prenotazione> prenotazioni = prenotazioneRepository.findAll().stream()
                .filter(p -> p.getStato() == Prenotazione.Stato.CONFERMATA)
                .collect(Collectors.toList());
        List<Feedback> feedback = feedbackRepository.findAll();

        dto.setNumeroOpere(operaRepository.count());
        dto.setNumeroCollezioni(collezioneRepository.count());
        dto.setNumeroEventi(eventi.size());
        dto.setNumeroPrenotazioni(prenotazioni.size());
        dto.setNumeroUtenti(utenteRepository.count());

        dto.setVotoMedioFeedback(feedback.stream().mapToInt(Feedback::getVoto).average().orElse(0));

        // Occupazione media: media, per ogni evento, di (posti prenotati / capienza)
        double occupazioneMedia = eventi.stream()
                .filter(e -> e.getCapienzaMax() != null && e.getCapienzaMax() > 0)
                .mapToDouble(e -> {
                    int occupati = prenotazioni.stream()
                            .filter(p -> p.getEvento().getId().equals(e.getId()))
                            .mapToInt(Prenotazione::getNumeroPosti).sum();
                    return (double) occupati / e.getCapienzaMax();
                })
                .average().orElse(0);
        dto.setOccupazioneMediaEventi(Math.round(occupazioneMedia * 100));

        // Eventi nei prossimi 7 giorni
        LocalDate oggi = LocalDate.now();
        LocalDate tra7giorni = oggi.plusDays(7);
        dto.setEventiProssimi(eventi.stream()
                .filter(e -> {
                    LocalDate data = e.getDataInizio().toLocalDate();
                    return !data.isBefore(oggi) && !data.isAfter(tra7giorni);
                })
                .sorted(Comparator.comparing(Evento::getDataInizio))
                .map(e -> new DashboardDTO.EventoProssimo(e.getTitolo(), e.getDataInizio().toLocalDate().format(FORMATO_DATA)))
                .collect(Collectors.toList()));

        // Prenotazioni per evento (per il grafico)
        dto.setPrenotazioniPerEvento(eventi.stream()
                .map(e -> {
                    long numero = prenotazioni.stream()
                            .filter(p -> p.getEvento().getId().equals(e.getId()))
                            .mapToInt(Prenotazione::getNumeroPosti).sum();
                    return new DashboardDTO.PrenotazioniPerEvento(e.getTitolo(), numero);
                })
                .collect(Collectors.toList()));

        // Ultimi feedback (i 12 più recenti, il frontend li pagina 3 alla volta)
        dto.setUltimiFeedback(feedback.stream()
                .sorted((a, b) -> b.getData().compareTo(a.getData()))
                .limit(12)
                .map(FeedbackDTO::daEntita)
                .collect(Collectors.toList()));

        // Eventi che hanno raggiunto (o superato) il 90% della capienza
        dto.setEventiCapienzaRaggiunta(eventi.stream()
                .filter(e -> e.getCapienzaMax() != null && e.getCapienzaMax() > 0)
                .filter(e -> {
                    int occupati = prenotazioni.stream()
                            .filter(p -> p.getEvento().getId().equals(e.getId()))
                            .mapToInt(Prenotazione::getNumeroPosti).sum();
                    return occupati >= e.getCapienzaMax() * 0.9;
                })
                .map(Evento::getTitolo)
                .collect(Collectors.toList()));

        // Sezione riservata HR
        if (utenteHaRuoloHR()) {
            dto.setPersonaleAttivo(dipendenteRepository.findAll().stream()
                    .filter(d -> d.getStato() == Dipendente.Stato.ATTIVO).count());

            dto.setFerieInAttesa(richiestaFerieRepository.findAll().stream()
                    .filter(r -> r.getStato() == RichiestaFerie.Stato.IN_ATTESA).count());

            LocalDate lunedi = oggi.with(DayOfWeek.MONDAY);
            List<Turno> turni = turnoRepository.findAll();
            List<DashboardDTO.GiornoTurni> turniSettimana = new ArrayList<>();
            List<String> giorniSenzaTurni = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                LocalDate giorno = lunedi.plusDays(i);
                String nomeGiorno = giorno.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ITALIAN);
                long numeroTurni = turni.stream().filter(t -> t.getData().equals(giorno)).count();
                turniSettimana.add(new DashboardDTO.GiornoTurni(capitalizza(nomeGiorno), numeroTurni));
                if (numeroTurni == 0) {
                    giorniSenzaTurni.add(capitalizza(giorno.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN)));
                }
            }
            dto.setTurniSettimana(turniSettimana);
            dto.setGiorniSenzaTurni(giorniSenzaTurni);
        }

        return dto;
    }

    private boolean utenteHaRuoloHR() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utenteRepository.findByEmail(email)
                .map(u -> u.getRuolo() == Utente.Ruolo.HR)
                .orElse(false);
    }

    private String capitalizza(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}