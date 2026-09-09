package com.villaserena.museo.controller;

import com.villaserena.museo.dto.RichiestaFerieDTO;
import com.villaserena.museo.dto.RichiestaFerieRequest;
import com.villaserena.museo.model.RichiestaFerie;
import com.villaserena.museo.service.FerieService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ferie")
public class FerieController {

    private final FerieService ferieService;

    public FerieController(FerieService ferieService) {
        this.ferieService = ferieService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public RichiestaFerieDTO crea(@RequestBody RichiestaFerieRequest request) {
        return ferieService.crea(request);
    }

    @GetMapping("/mie")
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public List<RichiestaFerieDTO> mie() {
        return ferieService.mie();
    }

    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public List<RichiestaFerieDTO> getAll() {
        return ferieService.findAll();
    }

    @PutMapping("/{id}/stato")
    @PreAuthorize("hasRole('HR')")
    public RichiestaFerieDTO aggiornaStato(@PathVariable Long id, @RequestParam RichiestaFerie.Stato stato) {
        return ferieService.aggiornaStato(id, stato);
    }
}