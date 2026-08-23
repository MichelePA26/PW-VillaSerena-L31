package com.villaserena.museo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "collezione")
public class Collezione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descrizione;

    @OneToMany(mappedBy = "collezione")
    private List<Opera> opere;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
}