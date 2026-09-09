export type TipoRichiesta = 'FERIE' | 'PERMESSO';
export type StatoRichiesta = 'IN_ATTESA' | 'APPROVATA' | 'RIFIUTATA';

export interface RichiestaFerie {
  id?: number;
  dipendenteNome?: string;
  tipo: TipoRichiesta;
  dataInizio: string;
  dataFine: string;
  oraInizio?: string;
  oraFine?: string;
  motivo?: string;
  stato?: StatoRichiesta;
  approvataDaNome?: string;
}