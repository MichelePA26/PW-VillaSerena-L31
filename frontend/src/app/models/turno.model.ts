export interface Turno {
  id?: number;
  dipendenteId: number;
  nomeDipendente?: string;
  data: string;
  oraInizio: string;
  oraFine: string;
  reparto: string;
}