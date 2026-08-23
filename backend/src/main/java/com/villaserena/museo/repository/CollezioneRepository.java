package com.villaserena.museo.repository;

import com.villaserena.museo.model.Collezione;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollezioneRepository extends JpaRepository<Collezione, Long> {
}