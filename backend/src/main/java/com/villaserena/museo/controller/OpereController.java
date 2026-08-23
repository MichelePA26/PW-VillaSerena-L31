package com.villaserena.museo.controller;

import com.villaserena.museo.dto.OperaDTO;
import com.villaserena.museo.service.OpereService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opere")
public class OpereController {

    private final OpereService opereService;

    public OpereController(OpereService opereService) {
        this.opereService = opereService;
    }

    @GetMapping
    public List<OperaDTO> getAll() {
        return opereService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public OperaDTO create(@RequestBody OperaDTO dto) {
        return opereService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public OperaDTO update(@PathVariable Long id, @RequestBody OperaDTO dto) {
        return opereService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public void delete(@PathVariable Long id) {
        opereService.delete(id);
    }
}