package com.villaserena.museo.service;

import com.villaserena.museo.dto.FeedbackDTO;
import com.villaserena.museo.dto.FeedbackRequest;
import com.villaserena.museo.model.Feedback;
import com.villaserena.museo.model.Prenotazione;
import com.villaserena.museo.model.Utente;
import com.villaserena.museo.repository.FeedbackRepository;
import com.villaserena.museo.repository.PrenotazioneRepository;
import com.villaserena.museo.repository.UtenteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
                            PrenotazioneRepository prenotazioneRepository,
                            UtenteRepository utenteRepository) {
        this.feedbackRepository = feedbackRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
    }

    public FeedbackDTO crea(FeedbackRequest request) {
        if (request.getVoto() == null || request.getVoto() < 1 || request.getVoto() > 5) {
            throw new RuntimeException("Il voto deve essere compreso tra 1 e 5");
        }

        Prenotazione prenotazione = prenotazioneRepository.findById(request.getPrenotazioneId())
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));

        Utente utente = utenteAutenticato();

        // Un utente può lasciare feedback solo su una prenotazione propria
        if (!prenotazione.getUtente().getId().equals(utente.getId())) {
            throw new RuntimeException("Non puoi lasciare un feedback su una prenotazione non tua");
        }

        Feedback feedback = new Feedback();
        feedback.setPrenotazione(prenotazione);
        feedback.setUtente(utente);
        feedback.setVoto(request.getVoto());
        feedback.setCommento(request.getCommento());

        return FeedbackDTO.daEntita(feedbackRepository.save(feedback));
    }

    public List<FeedbackDTO> findAll() {
        return feedbackRepository.findAll().stream()
                .map(FeedbackDTO::daEntita)
                .collect(Collectors.toList());
    }

    private Utente utenteAutenticato() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente autenticato non trovato"));
    }
}