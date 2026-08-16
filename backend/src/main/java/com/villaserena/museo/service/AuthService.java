package com.villaserena.museo.service;

import com.villaserena.museo.dto.AuthResponse;
import com.villaserena.museo.dto.LoginRequest;
import com.villaserena.museo.model.Utente;
import com.villaserena.museo.repository.UtenteRepository;
import com.villaserena.museo.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Utente registra(Utente nuovoUtente, String passwordChiara) {
        nuovoUtente.setPasswordHash(passwordEncoder.encode(passwordChiara));
        nuovoUtente.setRuolo(Utente.Ruolo.VISITATORE);
        return utenteRepository.save(nuovoUtente);
    }

    public AuthResponse login(LoginRequest request) {
        Utente utente = utenteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenziali non valide"));
        if (!passwordEncoder.matches(request.getPassword(), utente.getPasswordHash())) {
            throw new RuntimeException("Credenziali non valide");
        }
        String token = jwtUtil.generateToken(utente.getEmail(), utente.getRuolo().name());
        return new AuthResponse(token, utente.getRuolo().name());
    }
}
