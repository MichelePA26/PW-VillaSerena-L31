package com.villaserena.museo.dto;

public class FeedbackRequest {
    private Long prenotazioneId;
    private Integer voto;
    private String commento;

    public Long getPrenotazioneId() { return prenotazioneId; }
    public void setPrenotazioneId(Long id) { this.prenotazioneId = id; }
    public Integer getVoto() { return voto; }
    public void setVoto(Integer voto) { this.voto = voto; }
    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }
}