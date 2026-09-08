package com.villaserena.museo.controller;

import com.villaserena.museo.dto.AssunzioneRequest;
import com.villaserena.museo.dto.DipendenteDTO;
import com.villaserena.museo.service.PersonaleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.villaserena.museo.dto.AggiornaProfiloRequest;

import java.util.List;

@RestController
@RequestMapping("/api/personale")
@PreAuthorize("hasRole('HR')")


public class PersonaleController {

    private final PersonaleService personaleService;

    public PersonaleController(PersonaleService personaleService) {
        this.personaleService = personaleService;
    }

    @GetMapping
    public List<DipendenteDTO> getAll() {
        return personaleService.findAll();
    }

    @PostMapping
    public DipendenteDTO assumi(@RequestBody AssunzioneRequest request) {
        return personaleService.assumi(request);
    }

    @DeleteMapping("/{id}")
    public void cessa(@PathVariable Long id) {
        personaleService.cessa(id);
    }

    @GetMapping("/me")
    public DipendenteDTO mio() {
        return personaleService.mio();
    }

    @PutMapping("/me")
    @PreAuthorize("permitAll()")
    public DipendenteDTO aggiornaMioProfilo(@RequestBody AggiornaProfiloRequest request) {
        return personaleService.aggiornaMioProfilo(request.getTelefono(), request.getIndirizzo());
    }
}