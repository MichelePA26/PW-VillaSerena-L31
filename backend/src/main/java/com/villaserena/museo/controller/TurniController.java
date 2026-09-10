package com.villaserena.museo.controller;

import com.villaserena.museo.dto.TurnoDTO;
import com.villaserena.museo.service.TurniService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turni")
public class TurniController {

    private final TurniService turniService;

    public TurniController(TurniService turniService) {
        this.turniService = turniService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public List<TurnoDTO> getAll() {
        return turniService.findAll();
    }

    @GetMapping("/dipendente/{dipendenteId}")
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public List<TurnoDTO> byDipendente(@PathVariable Long dipendenteId) {
        return turniService.byDipendente(dipendenteId);
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public TurnoDTO create(@RequestBody TurnoDTO dto) {
        return turniService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public TurnoDTO update(@PathVariable Long id, @RequestBody TurnoDTO dto) {
        return turniService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public void delete(@PathVariable Long id) {
        turniService.delete(id);
    }
}