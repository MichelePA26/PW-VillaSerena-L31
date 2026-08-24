package com.villaserena.museo.controller;

import com.villaserena.museo.dto.PrenotazioneDTO;
import com.villaserena.museo.dto.PrenotazioneRequest;
import com.villaserena.museo.service.PrenotazioniService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioniController {

    private final PrenotazioniService prenotazioniService;

    public PrenotazioniController(PrenotazioniService prenotazioniService) {
        this.prenotazioniService = prenotazioniService;
    }

    @PostMapping
    public PrenotazioneDTO crea(@RequestBody PrenotazioneRequest request) {
        return prenotazioniService.crea(request);
    }

    // Sostituisce il vecchio /utente/{id}: ora restituisce sempre e solo
    // le prenotazioni di CHI è autenticato, mai di un ID scelto dal client
    @GetMapping("/mie")
    public List<PrenotazioneDTO> mie() {
        return prenotazioniService.mie();
    }
}