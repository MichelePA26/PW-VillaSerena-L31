package com.villaserena.museo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifica")
public class Notifica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String link;

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Utente destinatario;

    private String testo;
    private boolean letta = false;
    private LocalDateTime data = LocalDateTime.now();

    public Long getId() { return id; }
    public Utente getDestinatario() { return destinatario; }
    public void setDestinatario(Utente u) { this.destinatario = u; }
    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }
    public boolean isLetta() { return letta; }
    public void setLetta(boolean letta) { this.letta = letta; }
    public LocalDateTime getData() { return data; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}