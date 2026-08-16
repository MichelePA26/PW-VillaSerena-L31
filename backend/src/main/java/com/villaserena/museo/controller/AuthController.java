package com.villaserena.museo.controller;

import com.villaserena.museo.dto.AuthResponse;
import com.villaserena.museo.dto.LoginRequest;
import com.villaserena.museo.model.Utente;
import com.villaserena.museo.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Utente register(@RequestBody Utente utente, @RequestParam String password) {
        return authService.registra(utente, password);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
