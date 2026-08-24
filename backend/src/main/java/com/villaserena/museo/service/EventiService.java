package com.villaserena.museo.service;

import com.villaserena.museo.dto.EventoDTO;
import com.villaserena.museo.model.Evento;
import com.villaserena.museo.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventiService {

    private final EventoRepository eventoRepository;

    public EventiService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<EventoDTO> findAll() {
        return eventoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public EventoDTO create(EventoDTO dto) {
        Evento evento = new Evento();
        applica(dto, evento);
        return toDTO(eventoRepository.save(evento));
    }

    public void delete(Long id) {
        eventoRepository.deleteById(id);
    }

    private void applica(EventoDTO dto, Evento evento) {
        evento.setTitolo(dto.getTitolo());
        evento.setDescrizione(dto.getDescrizione());
        evento.setTipo(dto.getTipo());
        evento.setDataInizio(dto.getDataInizio());
        evento.setDataFine(dto.getDataFine());
        evento.setCapienzaMax(dto.getCapienzaMax());
    }

    private EventoDTO toDTO(Evento e) {
        EventoDTO dto = new EventoDTO();
        dto.setId(e.getId());
        dto.setTitolo(e.getTitolo());
        dto.setDescrizione(e.getDescrizione());
        dto.setTipo(e.getTipo());
        dto.setDataInizio(e.getDataInizio());
        dto.setDataFine(e.getDataFine());
        dto.setCapienzaMax(e.getCapienzaMax());
        return dto;
    }

    public EventoDTO update(Long id, EventoDTO dto) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));
        applica(dto, evento);
        return toDTO(eventoRepository.save(evento));
    }
}