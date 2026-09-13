package com.villaserena.museo.dto;

import com.villaserena.museo.model.Evento;
import com.villaserena.museo.model.Prenotazione;
import java.time.LocalDateTime;

public class PrenotazioneDTO {
    private Long id;
    private Long eventoId;
    private String eventoTitolo;
    private LocalDateTime eventoDataInizio;
    private Integer numeroPosti;
    private LocalDateTime dataPrenotazione;
    private Prenotazione.Stato stato;
    private Evento.Stato statoEvento;
    private java.time.LocalDateTime dataScadenzaRisposta;
    private java.math.BigDecimal prezzoEvento;
    private String codiceBiglietto;


    public static PrenotazioneDTO daEntita(Prenotazione p) {
        PrenotazioneDTO dto = new PrenotazioneDTO();
        dto.id = p.getId();
        dto.eventoId = p.getEvento().getId();
        dto.eventoTitolo = p.getEvento().getTitolo();
        dto.eventoDataInizio = p.getEvento().getDataInizio();
        dto.numeroPosti = p.getNumeroPosti();
        dto.dataPrenotazione = p.getDataPrenotazione();
        dto.stato = p.getStato();
        dto.statoEvento = p.getEvento().getStato();
        dto.dataScadenzaRisposta = p.getDataScadenzaRisposta();
        dto.prezzoEvento = p.getEvento().getPrezzo();
        dto.codiceBiglietto = p.getCodiceBiglietto();
        return dto;
    }

    public Long getId() { return id; }
    public Long getEventoId() { return eventoId; }
    public String getEventoTitolo() { return eventoTitolo; }
    public LocalDateTime getEventoDataInizio() { return eventoDataInizio; }
    public Integer getNumeroPosti() { return numeroPosti; }
    public LocalDateTime getDataPrenotazione() { return dataPrenotazione; }
    public Prenotazione.Stato getStato() { return stato; }
    public Evento.Stato getStatoEvento() { return statoEvento; }
    public java.time.LocalDateTime getDataScadenzaRisposta() { return dataScadenzaRisposta; }
    public java.math.BigDecimal getPrezzoEvento() { return prezzoEvento; }
    public String getCodiceBiglietto() { return codiceBiglietto; }
}