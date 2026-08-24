package com.villaserena.museo.dto;

import com.villaserena.museo.model.Prenotazione;
import java.time.LocalDateTime;

public class PrenotazioneDTO {
    private Long id;
    private Long eventoId;
    private String eventoTitolo;
    private LocalDateTime eventoDataInizio;
    private Integer numeroPosti;
    private LocalDateTime dataPrenotazione;
    private Prenotazione.Stato stato;

    public static PrenotazioneDTO daEntita(Prenotazione p) {
        PrenotazioneDTO dto = new PrenotazioneDTO();
        dto.id = p.getId();
        dto.eventoId = p.getEvento().getId();
        dto.eventoTitolo = p.getEvento().getTitolo();
        dto.eventoDataInizio = p.getEvento().getDataInizio();
        dto.numeroPosti = p.getNumeroPosti();
        dto.dataPrenotazione = p.getDataPrenotazione();
        dto.stato = p.getStato();
        return dto;
    }

    public Long getId() { return id; }
    public Long getEventoId() { return eventoId; }
    public String getEventoTitolo() { return eventoTitolo; }
    public LocalDateTime getEventoDataInizio() { return eventoDataInizio; }
    public Integer getNumeroPosti() { return numeroPosti; }
    public LocalDateTime getDataPrenotazione() { return dataPrenotazione; }
    public Prenotazione.Stato getStato() { return stato; }
}