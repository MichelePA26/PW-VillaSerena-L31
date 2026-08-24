package com.villaserena.museo.service;

import com.villaserena.museo.dto.OperaDTO;
import com.villaserena.museo.model.Collezione;
import com.villaserena.museo.model.Opera;
import com.villaserena.museo.model.Utente;
import com.villaserena.museo.repository.CollezioneRepository;
import com.villaserena.museo.repository.OperaRepository;
import com.villaserena.museo.repository.UtenteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpereService {

    private final OperaRepository operaRepository;
    private final CollezioneRepository collezioneRepository;
    private final UtenteRepository utenteRepository;

    public OpereService(OperaRepository operaRepository,
                         CollezioneRepository collezioneRepository,
                         UtenteRepository utenteRepository) {
        this.operaRepository = operaRepository;
        this.collezioneRepository = collezioneRepository;
        this.utenteRepository = utenteRepository;
    }

    public List<OperaDTO> findAll() {
        return operaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public OperaDTO create(OperaDTO dto) {
        Opera opera = new Opera();
        applica(dto, opera);

        LocalDateTime adesso = LocalDateTime.now();
        opera.setDataCreazione(adesso);
        opera.setDataModifica(adesso);
        opera.setCreatoDa(utenteAutenticato());

        return toDTO(operaRepository.save(opera));
    }

    public OperaDTO update(Long id, OperaDTO dto) {
        Opera opera = operaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opera non trovata"));
        applica(dto, opera);
        opera.setDataModifica(LocalDateTime.now());
        // dataCreazione e creatoDa non vengono mai toccati in modifica
        return toDTO(operaRepository.save(opera));
    }

    public void delete(Long id) {
        operaRepository.deleteById(id);
    }

    // Recupera l'utente autenticato dal token JWT già validato dal filtro di sicurezza
    private Utente utenteAutenticato() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente autenticato non trovato"));
    }

    private void applica(OperaDTO dto, Opera opera) {
        opera.setTitolo(dto.getTitolo());
        opera.setAutore(dto.getAutore());
        opera.setAnno(dto.getAnno());
        opera.setTecnica(dto.getTecnica());
        opera.setDescrizione(dto.getDescrizione());
        opera.setImmagineUrl(dto.getImmagineUrl());
        if (dto.getCollezioneId() != null) {
            Collezione c = collezioneRepository.findById(dto.getCollezioneId())
                    .orElseThrow(() -> new RuntimeException("Collezione non trovata"));
            opera.setCollezione(c);
        } else {
            opera.setCollezione(null);
        }
    }

    private OperaDTO toDTO(Opera o) {
        OperaDTO dto = new OperaDTO();
        dto.setId(o.getId());
        dto.setTitolo(o.getTitolo());
        dto.setAutore(o.getAutore());
        dto.setAnno(o.getAnno());
        dto.setTecnica(o.getTecnica());
        dto.setDescrizione(o.getDescrizione());
        dto.setImmagineUrl(o.getImmagineUrl());
        if (o.getCollezione() != null) dto.setCollezioneId(o.getCollezione().getId());
        dto.setDataCreazione(o.getDataCreazione());
        dto.setDataModifica(o.getDataModifica());
        if (o.getCreatoDa() != null) {
            dto.setCreatoDaNome(o.getCreatoDa().getNome() + " " + o.getCreatoDa().getCognome());
        }
        return dto;
    }
}