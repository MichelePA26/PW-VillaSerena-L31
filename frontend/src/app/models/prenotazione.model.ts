import { StatoEvento } from "./evento.model";

export interface Prenotazione {
  id?: number;
  eventoId: number;
  eventoTitolo: string;
  eventoDataInizio: string;
  numeroPosti: number;
  dataPrenotazione?: string;
  stato?: 'CONFERMATA' | 'ANNULLATA';
  statoEvento?: StatoEvento;
}

export { StatoEvento };
