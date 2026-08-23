package com.villaserena.museo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "opera")
public class Opera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titolo;
    private String autore;
    private Integer anno;
    private String tecnica;
    private String descrizione;
    private String immagineUrl;

    @ManyToOne
    @JoinColumn(name = "collezione_id")
    private Collezione collezione;

    public Long getId() { return id; }
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
    public Collezione getCollezione() { return collezione; }
    public void setCollezione(Collezione c) { this.collezione = c; }
    public String getImmagineUrl() { return immagineUrl; }
    public void setImmagineUrl(String url) { this.immagineUrl = url; }
}