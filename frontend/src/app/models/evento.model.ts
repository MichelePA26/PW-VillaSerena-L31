export type TipoEvento = 'VISITA_GUIDATA' | 'MOSTRA' | 'LABORATORIO';

export interface Evento {
  id?: number;
  titolo: string;
  descrizione?: string;
  tipo: TipoEvento;
  dataInizio: string;
  dataFine: string;
  capienzaMax: number;
}