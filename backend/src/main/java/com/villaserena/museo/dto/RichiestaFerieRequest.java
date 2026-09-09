package com.villaserena.museo.dto;

import com.villaserena.museo.model.RichiestaFerie;
import java.time.LocalDate;
import java.time.LocalTime;

public class RichiestaFerieRequest {
    private RichiestaFerie.Tipo tipo;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private String motivo;

    public RichiestaFerie.Tipo getTipo() { return tipo; }
    public void setTipo(RichiestaFerie.Tipo tipo) { this.tipo = tipo; }
    public LocalDate getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDate d) { this.dataInizio = d; }
    public LocalDate getDataFine() { return dataFine; }
    public void setDataFine(LocalDate d) { this.dataFine = d; }
    public LocalTime getOraInizio() { return oraInizio; }
    public void setOraInizio(LocalTime t) { this.oraInizio = t; }
    public LocalTime getOraFine() { return oraFine; }
    public void setOraFine(LocalTime t) { this.oraFine = t; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}