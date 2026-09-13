package com.villaserena.museo.repository;

import com.villaserena.museo.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByPrenotazioneId(Long prenotazioneId);
    Optional<Pagamento> findByIdOrdinePaypal(String idOrdinePaypal);
}