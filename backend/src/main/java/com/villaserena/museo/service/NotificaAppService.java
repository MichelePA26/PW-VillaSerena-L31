package com.villaserena.museo.service;

import com.villaserena.museo.dto.NotificaDTO;
import com.villaserena.museo.model.Notifica;
import com.villaserena.museo.model.Utente;
import com.villaserena.museo.repository.NotificaRepository;
import com.villaserena.museo.repository.UtenteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificaAppService {

    private final NotificaRepository notificaRepository;
    private final UtenteRepository utenteRepository;

    public NotificaAppService(NotificaRepository notificaRepository, UtenteRepository utenteRepository) {
        this.notificaRepository = notificaRepository;
        this.utenteRepository = utenteRepository;
    }

    public void crea(Utente destinatario, String testo , String link) {
        Notifica notifica = new Notifica();
        notifica.setDestinatario(destinatario);
        notifica.setTesto(testo);
        notifica.setLink(link);
        notificaRepository.save(notifica);
    }

    public List<NotificaDTO> mie() {
        Utente utente = utenteAutenticato();
        return notificaRepository.findAll().stream()
                .filter(n -> n.getDestinatario().getId().equals(utente.getId()))
                .filter(n -> !n.isLetta())
                .sorted((a, b) -> b.getData().compareTo(a.getData()))
                .map(NotificaDTO::daEntita)
                .collect(Collectors.toList());
    }

    public void segnaComeLetta(Long id) {
        Notifica notifica = notificaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notifica non trovata"));
        notifica.setLetta(true);
        notificaRepository.save(notifica);
    }

    private Utente utenteAutenticato() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }
}