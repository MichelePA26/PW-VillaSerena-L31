package com.villaserena.museo.dto;

import com.villaserena.museo.model.Evento;
import java.time.LocalDateTime;

public class EventoDTO {
    private Long id;
    private String titolo;
    private String descrizione;
    private Evento.Tipo tipo;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private Integer capienzaMax;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public Evento.Tipo getTipo() { return tipo; }
    public void setTipo(Evento.Tipo tipo) { this.tipo = tipo; }
    public LocalDateTime getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDateTime d) { this.dataInizio = d; }
    public LocalDateTime getDataFine() { return dataFine; }
    public void setDataFine(LocalDateTime d) { this.dataFine = d; }
    public Integer getCapienzaMax() { return capienzaMax; }
    public void setCapienzaMax(Integer c) { this.capienzaMax = c; }
}