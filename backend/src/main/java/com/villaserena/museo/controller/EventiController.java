package com.villaserena.museo.controller;

import com.villaserena.museo.dto.EventoDTO;
import com.villaserena.museo.service.EventiService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventi")
public class EventiController {

    private final EventiService eventiService;

    public EventiController(EventiService eventiService) {
        this.eventiService = eventiService;
    }

    @GetMapping
    public List<EventoDTO> getAll() {
        return eventiService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public EventoDTO create(@RequestBody EventoDTO dto) {
        return eventiService.create(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public void delete(@PathVariable Long id) {
        eventiService.delete(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public EventoDTO update(@PathVariable Long id, @RequestBody EventoDTO dto) {
        return eventiService.update(id, dto);
    }
}