package com.villaserena.museo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "prenotazione_id", nullable = false)
    private Prenotazione prenotazione;

    private Integer voto;
    private String commento;
    private LocalDateTime data = LocalDateTime.now();

    public Long getId() { return id; }
    public Utente getUtente() { return utente; }
    public void setUtente(Utente u) { this.utente = u; }
    public Prenotazione getPrenotazione() { return prenotazione; }
    public void setPrenotazione(Prenotazione p) { this.prenotazione = p; }
    public Integer getVoto() { return voto; }
    public void setVoto(Integer voto) { this.voto = voto; }
    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }
    public LocalDateTime getData() { return data; }
}