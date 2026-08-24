package com.villaserena.museo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evento")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titolo;
    private String descrizione;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private Integer capienzaMax;

    public enum Tipo { VISITA_GUIDATA, MOSTRA, LABORATORIO }

    public Long getId() { return id; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }
    public LocalDateTime getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDateTime d) { this.dataInizio = d; }
    public LocalDateTime getDataFine() { return dataFine; }
    public void setDataFine(LocalDateTime d) { this.dataFine = d; }
    public Integer getCapienzaMax() { return capienzaMax; }
    public void setCapienzaMax(Integer c) { this.capienzaMax = c; }
}