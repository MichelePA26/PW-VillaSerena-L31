package com.villaserena.museo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prenotazione")
public class Prenotazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String codiceBiglietto;

    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    private Integer numeroPosti = 1;
    private LocalDateTime dataPrenotazione = LocalDateTime.now();
    private java.time.LocalDateTime dataScadenzaRisposta;

    @Enumerated(EnumType.STRING)
    private Stato stato = Stato.CONFERMATA;

    public enum Stato { CONFERMATA, ANNULLATA, IN_ATTESA_PAGAMENTO, IN_ATTESA_MIGRAZIONE, RIMBORSATA}
    

    public Long getId() { return id; }
    public Utente getUtente() { return utente; }
    public void setUtente(Utente u) { this.utente = u; }
    public Evento getEvento() { return evento; }
    public void setEvento(Evento e) { this.evento = e; }
    public Integer getNumeroPosti() { return numeroPosti; }
    public void setNumeroPosti(Integer n) { this.numeroPosti = n; }
    public Stato getStato() { return stato; }
    public void setStato(Stato s) { this.stato = s; }
    public LocalDateTime getDataPrenotazione() { return dataPrenotazione; }
    public java.time.LocalDateTime getDataScadenzaRisposta() { return dataScadenzaRisposta; }
    public void setDataScadenzaRisposta(java.time.LocalDateTime d) { this.dataScadenzaRisposta = d; }
    public String getCodiceBiglietto() { return codiceBiglietto; }
    public void setCodiceBiglietto(String codice) { this.codiceBiglietto = codice; }
}