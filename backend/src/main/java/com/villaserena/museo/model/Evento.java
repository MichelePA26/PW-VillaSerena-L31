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
    private java.math.BigDecimal prezzo;

    @Column(columnDefinition = "TEXT")
    private String descrizione;
    

    @Enumerated(EnumType.STRING)
    private Tipo tipo;
    
    @Enumerated(EnumType.STRING)
    private Stato stato = Stato.PROGRAMMATO;

    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private Integer capienzaMax;

    public enum Tipo { VISITA_GUIDATA, MOSTRA, LABORATORIO }
    public enum Stato { PROGRAMMATO, DA_RIPROGRAMMARE, ANNULLATO }

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
    public Stato getStato() { return stato; }
    public void setStato(Stato stato) { this.stato = stato; }
    public java.math.BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(java.math.BigDecimal prezzo) { this.prezzo = prezzo; }
}