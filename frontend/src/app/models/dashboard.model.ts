export interface EventoProssimo {
  titolo: string;
  data: string;
}

export interface PrenotazioniPerEvento {
  titoloEvento: string;
  numeroPrenotazioni: number;
}

export interface FeedbackDashboard {
  utenteNome: string;
  voto: number;
  commento?: string;
}

export interface GiornoTurni {
  giorno: string;
  numeroTurni: number;
}

export interface Dashboard {
  numeroOpere: number;
  numeroCollezioni: number;
  numeroEventi: number;
  numeroPrenotazioni: number;
  numeroUtenti: number;
  votoMedioFeedback: number;
  occupazioneMediaEventi: number;
  eventiProssimi: EventoProssimo[];
  prenotazioniPerEvento: PrenotazioniPerEvento[];
  ultimiFeedback: FeedbackDashboard[];
  eventiCapienzaRaggiunta: string[];
  personaleAttivo?: number;
  ferieInAttesa?: number;
  turniSettimana?: GiornoTurni[];
  giorniSenzaTurni?: string[];
}