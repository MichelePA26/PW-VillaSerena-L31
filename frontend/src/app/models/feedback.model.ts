export interface Feedback {
  id?: number;
  prenotazioneId: number;
  eventoTitolo?: string;
  utenteNome?: string;
  voto: number;
  commento?: string;
  data?: string;
}