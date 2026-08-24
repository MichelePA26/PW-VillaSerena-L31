package com.villaserena.museo.dto;

public class PrenotazioneRequest {
    private Long eventoId;
    private Integer numeroPosti;

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long id) { this.eventoId = id; }
    public Integer getNumeroPosti() { return numeroPosti; }
    public void setNumeroPosti(Integer n) { this.numeroPosti = n; }
}