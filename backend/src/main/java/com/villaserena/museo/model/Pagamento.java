package com.villaserena.museo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "prenotazione_id", nullable = false)
    private Prenotazione prenotazione;

    private BigDecimal importo;
    private String valuta = "EUR";

    @Enumerated(EnumType.STRING)
    private Stato stato = Stato.IN_ATTESA;

    private String idOrdinePaypal;
    private String idCatturaPaypal; // necessario per un eventuale rimborso
    private LocalDateTime data = LocalDateTime.now();

    public enum Stato { IN_ATTESA, COMPLETATO, FALLITO, RIMBORSATO }

    public Long getId() { return id; }
    public Prenotazione getPrenotazione() { return prenotazione; }
    public void setPrenotazione(Prenotazione p) { this.prenotazione = p; }
    public BigDecimal getImporto() { return importo; }
    public void setImporto(BigDecimal importo) { this.importo = importo; }
    public String getValuta() { return valuta; }
    public void setValuta(String valuta) { this.valuta = valuta; }
    public Stato getStato() { return stato; }
    public void setStato(Stato stato) { this.stato = stato; }
    public String getIdOrdinePaypal() { return idOrdinePaypal; }
    public void setIdOrdinePaypal(String id) { this.idOrdinePaypal = id; }
    public String getIdCatturaPaypal() { return idCatturaPaypal; }
    public void setIdCatturaPaypal(String id) { this.idCatturaPaypal = id; }
    public LocalDateTime getData() { return data; }
}