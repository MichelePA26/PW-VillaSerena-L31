package com.villaserena.museo.dto;

import java.util.List;

public class DashboardDTO {
    private long numeroOpere;
    private long numeroCollezioni;
    private long numeroEventi;
    private long numeroPrenotazioni;
    private long numeroUtenti;
    private double votoMedioFeedback;
    private double occupazioneMediaEventi;
    private List<EventoProssimo> eventiProssimi;
    private List<PrenotazioniPerEvento> prenotazioniPerEvento;
    private List<FeedbackDTO> ultimiFeedback;
    private List<String> eventiCapienzaRaggiunta;

    // Solo per HR (null per Operatore)
    private Long personaleAttivo;
    private Long ferieInAttesa;
    private List<GiornoTurni> turniSettimana;
    private List<String> giorniSenzaTurni;

    public static class EventoProssimo {
        private String titolo;
        private String data;
        public EventoProssimo(String titolo, String data) { this.titolo = titolo; this.data = data; }
        public String getTitolo() { return titolo; }
        public String getData() { return data; }
    }

    public static class PrenotazioniPerEvento {
        private String titoloEvento;
        private long numeroPrenotazioni;
        public PrenotazioniPerEvento(String titoloEvento, long numeroPrenotazioni) {
            this.titoloEvento = titoloEvento; this.numeroPrenotazioni = numeroPrenotazioni;
        }
        public String getTitoloEvento() { return titoloEvento; }
        public long getNumeroPrenotazioni() { return numeroPrenotazioni; }
    }

    public static class GiornoTurni {
        private String giorno;
        private long numeroTurni;
        public GiornoTurni(String giorno, long numeroTurni) { this.giorno = giorno; this.numeroTurni = numeroTurni; }
        public String getGiorno() { return giorno; }
        public long getNumeroTurni() { return numeroTurni; }
    }

    public long getNumeroOpere() { return numeroOpere; }
    public void setNumeroOpere(long n) { this.numeroOpere = n; }
    public long getNumeroCollezioni() { return numeroCollezioni; }
    public void setNumeroCollezioni(long n) { this.numeroCollezioni = n; }
    public long getNumeroEventi() { return numeroEventi; }
    public void setNumeroEventi(long n) { this.numeroEventi = n; }
    public long getNumeroPrenotazioni() { return numeroPrenotazioni; }
    public void setNumeroPrenotazioni(long n) { this.numeroPrenotazioni = n; }
    public long getNumeroUtenti() { return numeroUtenti; }
    public void setNumeroUtenti(long n) { this.numeroUtenti = n; }
    public double getVotoMedioFeedback() { return votoMedioFeedback; }
    public void setVotoMedioFeedback(double v) { this.votoMedioFeedback = v; }
    public double getOccupazioneMediaEventi() { return occupazioneMediaEventi; }
    public void setOccupazioneMediaEventi(double o) { this.occupazioneMediaEventi = o; }
    public List<EventoProssimo> getEventiProssimi() { return eventiProssimi; }
    public void setEventiProssimi(List<EventoProssimo> e) { this.eventiProssimi = e; }
    public List<PrenotazioniPerEvento> getPrenotazioniPerEvento() { return prenotazioniPerEvento; }
    public void setPrenotazioniPerEvento(List<PrenotazioniPerEvento> p) { this.prenotazioniPerEvento = p; }
    public List<FeedbackDTO> getUltimiFeedback() { return ultimiFeedback; }
    public void setUltimiFeedback(List<FeedbackDTO> f) { this.ultimiFeedback = f; }
    public List<String> getEventiCapienzaRaggiunta() { return eventiCapienzaRaggiunta; }
    public void setEventiCapienzaRaggiunta(List<String> e) { this.eventiCapienzaRaggiunta = e; }
    public Long getPersonaleAttivo() { return personaleAttivo; }
    public void setPersonaleAttivo(Long p) { this.personaleAttivo = p; }
    public Long getFerieInAttesa() { return ferieInAttesa; }
    public void setFerieInAttesa(Long f) { this.ferieInAttesa = f; }
    public List<GiornoTurni> getTurniSettimana() { return turniSettimana; }
    public void setTurniSettimana(List<GiornoTurni> t) { this.turniSettimana = t; }
    public List<String> getGiorniSenzaTurni() { return giorniSenzaTurni; }
    public void setGiorniSenzaTurni(List<String> g) { this.giorniSenzaTurni = g; }
}