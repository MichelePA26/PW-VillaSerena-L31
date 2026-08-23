package com.villaserena.museo.repository;

import com.villaserena.museo.model.Opera;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperaRepository extends JpaRepository<Opera, Long> {
}