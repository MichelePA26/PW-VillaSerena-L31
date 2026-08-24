export interface Prenotazione {
  id?: number;
  eventoId: number;
  eventoTitolo: string;
  eventoDataInizio: string;
  numeroPosti: number;
  dataPrenotazione?: string;
  stato?: 'CONFERMATA' | 'ANNULLATA';
}