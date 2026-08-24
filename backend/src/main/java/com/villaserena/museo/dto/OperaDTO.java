package com.villaserena.museo.dto;

import java.time.LocalDateTime;

public class OperaDTO {
    private Long id;
    private String titolo;
    private String autore;
    private Integer anno;
    private String tecnica;
    private String descrizione;
    private String immagineUrl;
    private Long collezioneId;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataModifica;
    private String creatoDaNome;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }
    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }
    public String getTecnica() { return tecnica; }
    public void setTecnica(String tecnica) { this.tecnica = tecnica; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public String getImmagineUrl() { return immagineUrl; }
    public void setImmagineUrl(String url) { this.immagineUrl = url; }
    public Long getCollezioneId() { return collezioneId; }
    public void setCollezioneId(Long id) { this.collezioneId = id; }
    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime d) { this.dataCreazione = d; }
    public LocalDateTime getDataModifica() { return dataModifica; }
    public void setDataModifica(LocalDateTime d) { this.dataModifica = d; }
    public String getCreatoDaNome() { return creatoDaNome; }
    public void setCreatoDaNome(String nome) { this.creatoDaNome = nome; }
}