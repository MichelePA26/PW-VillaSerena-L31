import { StatoEvento } from "./evento.model";

export type StatoPrenotazione = 'CONFERMATA' | 'ANNULLATA' | 'IN_ATTESA_PAGAMENTO' | 'IN_ATTESA_MIGRAZIONE' | 'RIMBORSATA';
export { StatoEvento };

export interface Prenotazione {
  id?: number;
  eventoId: number;
  eventoTitolo: string;
  eventoDataInizio: string;
  numeroPosti: number;
  dataPrenotazione?: string;
  stato?: StatoPrenotazione;
  statoEvento?: StatoEvento;
  dataScadenzaRisposta?: string;
  prezzoEvento?: number;
  codiceBiglietto?: string;
}



