package com.villaserena.museo.service;

import com.villaserena.museo.dto.AuthResponse;
import com.villaserena.museo.dto.LoginRequest;
import com.villaserena.museo.model.Dipendente;
import com.villaserena.museo.model.Utente;
import com.villaserena.museo.repository.DipendenteRepository;
import com.villaserena.museo.repository.UtenteRepository;
import com.villaserena.museo.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import com.villaserena.museo.dto.CambioPasswordRequest;

@Service
public class AuthService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final DipendenteRepository dipendenteRepository;

    public AuthService(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, DipendenteRepository dipendenteRepository) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.dipendenteRepository = dipendenteRepository;
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

        Long dipendenteId = dipendenteRepository.findAll().stream()
                .filter(d -> d.getUtente().getId().equals(utente.getId()))
                .findFirst()
                .map(Dipendente::getId)
                .orElse(null);

        return new AuthResponse(token, utente.getRuolo().name(), dipendenteId);
    }

    public void cambiaPassword(CambioPasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (!passwordEncoder.matches(request.getPasswordAttuale(), utente.getPasswordHash())) {
            throw new RuntimeException("La password attuale non è corretta");
        }

        if (request.getNuovaPassword() == null || request.getNuovaPassword().length() < 8) {
            throw new RuntimeException("La nuova password deve avere almeno 8 caratteri");
        }

        utente.setPasswordHash(passwordEncoder.encode(request.getNuovaPassword()));
        utenteRepository.save(utente);
    }
}
