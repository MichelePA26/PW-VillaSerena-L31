package com.villaserena.museo.controller;

import com.villaserena.museo.dto.PrenotazioneDTO;
import com.villaserena.museo.dto.PrenotazioneRequest;
import com.villaserena.museo.service.PrenotazioniService;

import org.springframework.security.access.prepost.PreAuthorize;
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

    @PutMapping("/{id}/accetta-nuova-data")
    public PrenotazioneDTO accettaNuovaData(@PathVariable Long id) {
        return prenotazioniService.accettaNuovaData(id);
    }

    @PutMapping("/{id}/richiedi-rimborso")
    public PrenotazioneDTO richiediRimborso(@PathVariable Long id) {
        return prenotazioniService.richiediRimborso(id);
    }
    
    @PutMapping("/{id}/annulla")
    public void annullaNonPagata(@PathVariable Long id) {
        prenotazioniService.annullaNonPagata(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public List<PrenotazioneDTO> getAll(@RequestParam(required = false) Long eventoId) {
        return prenotazioniService.findAll(eventoId);
    }

    @GetMapping("/cerca-biglietto/{codice}")
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public PrenotazioneDTO cercaPerCodiceBiglietto(@PathVariable String codice) {
        return prenotazioniService.cercaPerCodiceBiglietto(codice);
    }

    @PutMapping("/check-in/{codice}")
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public PrenotazioneDTO effettuaCheckIn(@PathVariable String codice) {
        return prenotazioniService.effettuaCheckIn(codice);
    }

    @PutMapping("/{id}/risolvi-ufficio")
    @PreAuthorize("hasRole('OPERATORE')")
    public PrenotazioneDTO risolviDOfficio(@PathVariable Long id, @RequestParam String decisione) {
        return prenotazioniService.risolviDOfficio(id, decisione);
    }
}