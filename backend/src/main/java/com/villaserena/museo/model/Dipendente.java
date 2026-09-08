package com.villaserena.museo.model;

import com.villaserena.museo.security.CampoCifratoConverter;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "dipendente")
public class Dipendente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "utente_id", nullable = false, unique = true)
    private Utente utente;

    private String mansione;
    private LocalDate dataAssunzione;
    private LocalDate dataCessazione;

    @Enumerated(EnumType.STRING)
    private Stato stato = Stato.ATTIVO;

    @Convert(converter = CampoCifratoConverter.class)
    @Column(name = "codice_fiscale")
    private String codiceFiscale;

    private LocalDate dataNascita;
    private String telefono;
    private String indirizzo;

    @Enumerated(EnumType.STRING)
    private TipoContratto tipoContratto;

    private String livelloInquadramento;

    @Convert(converter = CampoCifratoConverter.class)
    @Column(name = "iban")
    private String iban;

    public enum Stato { ATTIVO, CESSATO }
    public enum TipoContratto { TEMPO_DETERMINATO, TEMPO_INDETERMINATO, PART_TIME, STAGIONALE }

    public Long getId() { return id; }
    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public String getMansione() { return mansione; }
    public void setMansione(String mansione) { this.mansione = mansione; }
    public LocalDate getDataAssunzione() { return dataAssunzione; }
    public void setDataAssunzione(LocalDate d) { this.dataAssunzione = d; }
    public LocalDate getDataCessazione() { return dataCessazione; }
    public void setDataCessazione(LocalDate d) { this.dataCessazione = d; }
    public Stato getStato() { return stato; }
    public void setStato(Stato stato) { this.stato = stato; }
    public String getCodiceFiscale() { return codiceFiscale; }
    public void setCodiceFiscale(String c) { this.codiceFiscale = c; }
    public LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(LocalDate d) { this.dataNascita = d; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
    public TipoContratto getTipoContratto() { return tipoContratto; }
    public void setTipoContratto(TipoContratto t) { this.tipoContratto = t; }
    public String getLivelloInquadramento() { return livelloInquadramento; }
    public void setLivelloInquadramento(String l) { this.livelloInquadramento = l; }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
}