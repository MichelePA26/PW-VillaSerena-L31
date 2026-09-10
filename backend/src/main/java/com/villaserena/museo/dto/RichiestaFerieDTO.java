package com.villaserena.museo.dto;

import com.villaserena.museo.model.RichiestaFerie;
import java.time.LocalDate;
import java.time.LocalTime;

public class RichiestaFerieDTO {
    private Long id;
    private String dipendenteNome;
    private RichiestaFerie.Tipo tipo;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private String motivo;
    private RichiestaFerie.Stato stato;
    private String approvataDaNome;
    private String avvisoTurniInConflitto;

    public static RichiestaFerieDTO daEntita(RichiestaFerie r) {
        RichiestaFerieDTO dto = new RichiestaFerieDTO();
        dto.id = r.getId();
        dto.dipendenteNome = r.getDipendente().getUtente().getNome() + " " + r.getDipendente().getUtente().getCognome();
        dto.tipo = r.getTipo();
        dto.dataInizio = r.getDataInizio();
        dto.dataFine = r.getDataFine();
        dto.oraInizio = r.getOraInizio();
        dto.oraFine = r.getOraFine();
        dto.motivo = r.getMotivo();
        dto.stato = r.getStato();
        if (r.getApprovataDa() != null) {
            dto.approvataDaNome = r.getApprovataDa().getUtente().getNome() + " " + r.getApprovataDa().getUtente().getCognome();
        }
        return dto;
    }

    public Long getId() { return id; }
    public String getDipendenteNome() { return dipendenteNome; }
    public RichiestaFerie.Tipo getTipo() { return tipo; }
    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public LocalTime getOraInizio() { return oraInizio; }
    public LocalTime getOraFine() { return oraFine; }
    public String getMotivo() { return motivo; }
    public RichiestaFerie.Stato getStato() { return stato; }
    public String getApprovataDaNome() { return approvataDaNome; }
    public String getAvvisoTurniInConflitto() { return avvisoTurniInConflitto; }
public void setAvvisoTurniInConflitto(String a) { this.avvisoTurniInConflitto = a; }
}