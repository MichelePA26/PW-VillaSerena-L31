package com.villaserena.museo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "turno")
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dipendente_id", nullable = false)
    private Dipendente dipendente;

    private LocalDate data;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private String reparto;

    public Long getId() { return id; }
    public Dipendente getDipendente() { return dipendente; }
    public void setDipendente(Dipendente d) { this.dipendente = d; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getOraInizio() { return oraInizio; }
    public void setOraInizio(LocalTime t) { this.oraInizio = t; }
    public LocalTime getOraFine() { return oraFine; }
    public void setOraFine(LocalTime t) { this.oraFine = t; }
    public String getReparto() { return reparto; }
    public void setReparto(String reparto) { this.reparto = reparto; }
}