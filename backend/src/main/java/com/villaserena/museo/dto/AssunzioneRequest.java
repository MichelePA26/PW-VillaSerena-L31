package com.villaserena.museo.dto;

import com.villaserena.museo.model.Dipendente;
import java.time.LocalDate;

public class AssunzioneRequest {
    private Long utenteId;
    private String mansione;
    private String codiceFiscale;
    private LocalDate dataNascita;
    private String telefono;
    private String indirizzo;
    private Dipendente.TipoContratto tipoContratto;
    private String livelloInquadramento;
    private String iban;

    public Long getUtenteId() { return utenteId; }
    public void setUtenteId(Long id) { this.utenteId = id; }
    public String getMansione() { return mansione; }
    public void setMansione(String mansione) { this.mansione = mansione; }
    public String getCodiceFiscale() { return codiceFiscale; }
    public void setCodiceFiscale(String c) { this.codiceFiscale = c; }
    public LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(LocalDate d) { this.dataNascita = d; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
    public Dipendente.TipoContratto getTipoContratto() { return tipoContratto; }
    public void setTipoContratto(Dipendente.TipoContratto t) { this.tipoContratto = t; }
    public String getLivelloInquadramento() { return livelloInquadramento; }
    public void setLivelloInquadramento(String l) { this.livelloInquadramento = l; }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
}