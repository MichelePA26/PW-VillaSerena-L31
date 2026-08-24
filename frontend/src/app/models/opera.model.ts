export interface Opera {
  id?: number;
  titolo: string;
  autore: string;
  anno: number;
  tecnica: string;
  descrizione?: string;
  immagineUrl?: string;
  collezioneId?: number;
  dataCreazione?: string;
  dataModifica?: string;
  creatoDaNome?: string;
}