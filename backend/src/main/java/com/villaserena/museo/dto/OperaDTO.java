package com.villaserena.museo.dto;

public class OperaDTO {
    private Long id;
    private String titolo;
    private String autore;
    private Integer anno;
    private String tecnica;
    private String descrizione;
    private Long collezioneId;
    private String immagineUrl;

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
    public Long getCollezioneId() { return collezioneId; }
    public void setCollezioneId(Long id) { this.collezioneId = id; }
    public String getImmagineUrl() { return immagineUrl; }
    public void setImmagineUrl(String url) { this.immagineUrl = url; }
}