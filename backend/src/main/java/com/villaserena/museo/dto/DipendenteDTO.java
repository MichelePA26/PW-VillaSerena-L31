package com.villaserena.museo.dto;

import com.villaserena.museo.model.Dipendente;
import java.time.LocalDate;

public class DipendenteDTO {
    private Long id;
    private Long utenteId;
    private String nomeCompleto;
    private String mansione;
    private LocalDate dataAssunzione;
    private LocalDate dataCessazione;
    private Dipendente.Stato stato;
    private Dipendente.TipoContratto tipoContratto;
    private String livelloInquadramento;
    private String telefono;
    private LocalDate dataNascita;
    private String codiceFiscaleMascherato;
    private String ibanMascherato;

    public static DipendenteDTO daEntita(Dipendente d) {
        DipendenteDTO dto = new DipendenteDTO();
        dto.id = d.getId();
        dto.utenteId = d.getUtente().getId();
        dto.nomeCompleto = d.getUtente().getNome() + " " + d.getUtente().getCognome();
        dto.mansione = d.getMansione();
        dto.dataAssunzione = d.getDataAssunzione();
        dto.dataCessazione = d.getDataCessazione();
        dto.stato = d.getStato();
        dto.tipoContratto = d.getTipoContratto();
        dto.livelloInquadramento = d.getLivelloInquadramento();
        dto.telefono = d.getTelefono();
        dto.dataNascita = d.getDataNascita();
        dto.codiceFiscaleMascherato = maschera(d.getCodiceFiscale(), 4);
        dto.ibanMascherato = maschera(d.getIban(), 4);
        return dto;
    }

    private static String maschera(String valore, int caratteriVisibili) {
        if (valore == null || valore.length() <= caratteriVisibili) return "****";
        return "*".repeat(valore.length() - caratteriVisibili) + valore.substring(valore.length() - caratteriVisibili);
    }

    public Long getId() { return id; }
    public Long getUtenteId() { return utenteId; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getMansione() { return mansione; }
    public LocalDate getDataAssunzione() { return dataAssunzione; }
    public LocalDate getDataCessazione() { return dataCessazione; }
    public Dipendente.Stato getStato() { return stato; }
    public Dipendente.TipoContratto getTipoContratto() { return tipoContratto; }
    public String getLivelloInquadramento() { return livelloInquadramento; }
    public String getTelefono() { return telefono; }
    public LocalDate getDataNascita() { return dataNascita; }
    public String getCodiceFiscaleMascherato() { return codiceFiscaleMascherato; }
    public String getIbanMascherato() { return ibanMascherato; }
}