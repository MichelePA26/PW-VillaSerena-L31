package com.villaserena.museo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class TurnoDTO {
    private Long id;
    private Long dipendenteId;
    private String nomeDipendente;
    private LocalDate data;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private String reparto;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDipendenteId() { return dipendenteId; }
    public void setDipendenteId(Long id) { this.dipendenteId = id; }
    public String getNomeDipendente() { return nomeDipendente; }
    public void setNomeDipendente(String n) { this.nomeDipendente = n; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getOraInizio() { return oraInizio; }
    public void setOraInizio(LocalTime t) { this.oraInizio = t; }
    public LocalTime getOraFine() { return oraFine; }
    public void setOraFine(LocalTime t) { this.oraFine = t; }
    public String getReparto() { return reparto; }
    public void setReparto(String reparto) { this.reparto = reparto; }
}