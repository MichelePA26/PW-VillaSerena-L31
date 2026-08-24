package com.villaserena.museo.service;

import com.villaserena.museo.dto.PrenotazioneDTO;
import com.villaserena.museo.dto.PrenotazioneRequest;
import com.villaserena.museo.model.Evento;
import com.villaserena.museo.model.Prenotazione;
import com.villaserena.museo.model.Utente;
import com.villaserena.museo.repository.EventoRepository;
import com.villaserena.museo.repository.PrenotazioneRepository;
import com.villaserena.museo.repository.UtenteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrenotazioniService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final EventoRepository eventoRepository;
    private final UtenteRepository utenteRepository;

    public PrenotazioniService(PrenotazioneRepository prenotazioneRepository,
                                EventoRepository eventoRepository,
                                UtenteRepository utenteRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.eventoRepository = eventoRepository;
        this.utenteRepository = utenteRepository;
    }

    public PrenotazioneDTO crea(PrenotazioneRequest request) {
        Evento evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));
        Utente utente = utenteAutenticato();

        int postiOccupati = prenotazioneRepository.findAll().stream()
                .filter(p -> p.getEvento().getId().equals(evento.getId())
                        && p.getStato() == Prenotazione.Stato.CONFERMATA)
                .mapToInt(Prenotazione::getNumeroPosti)
                .sum();

        if (postiOccupati + request.getNumeroPosti() > evento.getCapienzaMax()) {
            throw new RuntimeException("Capienza massima superata per l'evento selezionato");
        }

        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setEvento(evento);
        prenotazione.setUtente(utente);
        prenotazione.setNumeroPosti(request.getNumeroPosti());

        return PrenotazioneDTO.daEntita(prenotazioneRepository.save(prenotazione));
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
}