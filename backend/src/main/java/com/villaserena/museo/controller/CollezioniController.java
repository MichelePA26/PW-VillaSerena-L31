package com.villaserena.museo.controller;

import com.villaserena.museo.model.Collezione;
import com.villaserena.museo.service.CollezioniService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collezioni")
public class CollezioniController {

    private final CollezioniService collezioniService;

    public CollezioniController(CollezioniService collezioniService) {
        this.collezioniService = collezioniService;
    }

    @GetMapping
    public List<Collezione> getAll() {
        return collezioniService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public Collezione create(@RequestBody Collezione collezione) {
        return collezioniService.create(collezione);
    }
}