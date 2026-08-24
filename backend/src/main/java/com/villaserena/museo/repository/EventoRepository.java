package com.villaserena.museo.repository;

import com.villaserena.museo.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
}