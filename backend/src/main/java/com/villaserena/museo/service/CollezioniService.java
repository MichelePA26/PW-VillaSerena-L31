package com.villaserena.museo.service;

import com.villaserena.museo.model.Collezione;
import com.villaserena.museo.repository.CollezioneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollezioniService {

    private final CollezioneRepository collezioneRepository;

    public CollezioniService(CollezioneRepository collezioneRepository) {
        this.collezioneRepository = collezioneRepository;
    }

    public List<Collezione> findAll() {
        return collezioneRepository.findAll();
    }

    public Collezione create(Collezione collezione) {
        return collezioneRepository.save(collezione);
    }
}