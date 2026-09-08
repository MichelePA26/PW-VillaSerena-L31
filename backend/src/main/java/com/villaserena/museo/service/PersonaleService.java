package com.villaserena.museo.service;

import com.villaserena.museo.dto.AssunzioneRequest;
import com.villaserena.museo.dto.DipendenteDTO;
import com.villaserena.museo.model.Dipendente;
import com.villaserena.museo.model.Utente;
import com.villaserena.museo.repository.DipendenteRepository;
import com.villaserena.museo.repository.UtenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonaleService {

    private final DipendenteRepository dipendenteRepository;
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonaleService(DipendenteRepository dipendenteRepository, UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.dipendenteRepository = dipendenteRepository;
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void resetPassword(Long dipendenteId, String nuovaPassword) {
        if (nuovaPassword == null || nuovaPassword.length() < 8) {
            throw new RuntimeException("La nuova password deve avere almeno 8 caratteri");
        }
        Dipendente dipendente = dipendenteRepository.findById(dipendenteId)
                .orElseThrow(() -> new RuntimeException("Dipendente non trovato"));
        Utente utente = dipendente.getUtente();
        utente.setPasswordHash(passwordEncoder.encode(nuovaPassword));
        utenteRepository.save(utente);
    }

    public List<DipendenteDTO> findAll() {
        return dipendenteRepository.findAll().stream()
                .map(DipendenteDTO::daEntita)
                .collect(Collectors.toList());
    }

    public DipendenteDTO assumi(AssunzioneRequest request) {
        Utente utente = utenteRepository.findById(request.getUtenteId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (dipendenteRepository.findAll().stream().anyMatch(d -> d.getUtente().getId().equals(utente.getId()))) {
            throw new RuntimeException("Questo utente ha già un profilo dipendente");
        }

        utente.setRuolo(Utente.Ruolo.OPERATORE);
        utenteRepository.save(utente);

        Dipendente dipendente = new Dipendente();
        dipendente.setUtente(utente);
        dipendente.setMansione(request.getMansione());
        dipendente.setDataAssunzione(LocalDate.now());
        dipendente.setStato(Dipendente.Stato.ATTIVO);
        dipendente.setCodiceFiscale(request.getCodiceFiscale());
        dipendente.setDataNascita(request.getDataNascita());
        dipendente.setTelefono(request.getTelefono());
        dipendente.setIndirizzo(request.getIndirizzo());
        dipendente.setTipoContratto(request.getTipoContratto());
        dipendente.setLivelloInquadramento(request.getLivelloInquadramento());
        dipendente.setIban(request.getIban());

        return DipendenteDTO.daEntita(dipendenteRepository.save(dipendente));
    }

    // Cessazione: riporta il ruolo dell'utente a VISITATORE, impedendo
    // l'accesso alle aree riservate a Operatore/HR una volta cessato.
    public void cessa(Long dipendenteId) {
        Dipendente dipendente = dipendenteRepository.findById(dipendenteId)
                .orElseThrow(() -> new RuntimeException("Dipendente non trovato"));
        dipendente.setStato(Dipendente.Stato.CESSATO);
        dipendente.setDataCessazione(LocalDate.now());
        dipendenteRepository.save(dipendente);

        Utente utente = dipendente.getUtente();
        utente.setRuolo(Utente.Ruolo.VISITATORE);
        utenteRepository.save(utente);
    }

    public DipendenteDTO aggiornaMioProfilo(String telefono, String indirizzo) {
        Dipendente dipendente = dipendenteCorrente();
        dipendente.setTelefono(telefono);
        dipendente.setIndirizzo(indirizzo);
        return DipendenteDTO.daEntita(dipendenteRepository.save(dipendente));
    }

    private Dipendente dipendenteCorrente() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return dipendenteRepository.findAll().stream()
                .filter(d -> d.getUtente().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Profilo dipendente non trovato per l'utente corrente"));
    }

    public DipendenteDTO mio() {
        return DipendenteDTO.daEntita(dipendenteCorrente());
    }

    public DipendenteDTO aggiorna(Long dipendenteId, AssunzioneRequest request) {
    Dipendente dipendente = dipendenteRepository.findById(dipendenteId)
            .orElseThrow(() -> new RuntimeException("Dipendente non trovato"));

    dipendente.setMansione(request.getMansione());
    dipendente.setDataNascita(request.getDataNascita());
    dipendente.setTelefono(request.getTelefono());
    dipendente.setIndirizzo(request.getIndirizzo());
    dipendente.setTipoContratto(request.getTipoContratto());
    dipendente.setLivelloInquadramento(request.getLivelloInquadramento());

    // aggiorna i campi sensibili solo se HR ha scritto un nuovo valore,
    // altrimenti mantiene quello già cifrato in database
    if (request.getCodiceFiscale() != null && !request.getCodiceFiscale().isBlank()) {
        dipendente.setCodiceFiscale(request.getCodiceFiscale());
    }
    if (request.getIban() != null && !request.getIban().isBlank()) {
        dipendente.setIban(request.getIban());
    }

    return DipendenteDTO.daEntita(dipendenteRepository.save(dipendente));
}
}