package com.villaserena.museo.dto;

import com.villaserena.museo.model.Feedback;
import java.time.LocalDateTime;

public class FeedbackDTO {
    private Long id;
    private Long prenotazioneId;
    private String eventoTitolo;
    private String utenteNome;
    private Integer voto;
    private String commento;
    private LocalDateTime data;

    public static FeedbackDTO daEntita(Feedback f) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.id = f.getId();
        dto.prenotazioneId = f.getPrenotazione().getId();
        dto.eventoTitolo = f.getPrenotazione().getEvento().getTitolo();
        dto.utenteNome = f.getUtente().getNome() + " " + f.getUtente().getCognome().charAt(0) + ".";
        dto.voto = f.getVoto();
        dto.commento = f.getCommento();
        dto.data = f.getData();
        return dto;
    }

    public Long getId() { return id; }
    public Long getPrenotazioneId() { return prenotazioneId; }
    public String getEventoTitolo() { return eventoTitolo; }
    public String getUtenteNome() { return utenteNome; }
    public Integer getVoto() { return voto; }
    public String getCommento() { return commento; }
    public LocalDateTime getData() { return data; }
}