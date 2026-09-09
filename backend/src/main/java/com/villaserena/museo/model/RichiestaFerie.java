package com.villaserena.museo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "richiesta_ferie")
public class RichiestaFerie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dipendente_id", nullable = false)
    private Dipendente dipendente;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private LocalDate dataInizio;
    private LocalDate dataFine;

    // Valorizzati solo per i permessi (un permesso vale per un solo giorno,
    // con una fascia oraria specifica); restano null per le ferie.
    private LocalTime oraInizio;
    private LocalTime oraFine;

    private String motivo;

    @Enumerated(EnumType.STRING)
    private Stato stato = Stato.IN_ATTESA;

    @ManyToOne
    @JoinColumn(name = "approvata_da")
    private Dipendente approvataDa;

    public enum Tipo { FERIE, PERMESSO }
    public enum Stato { IN_ATTESA, APPROVATA, RIFIUTATA }

    public Long getId() { return id; }
    public Dipendente getDipendente() { return dipendente; }
    public void setDipendente(Dipendente d) { this.dipendente = d; }
    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }
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
    public Stato getStato() { return stato; }
    public void setStato(Stato stato) { this.stato = stato; }
    public Dipendente getApprovataDa() { return approvataDa; }
    public void setApprovataDa(Dipendente d) { this.approvataDa = d; }
}