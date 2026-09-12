export type TipoEvento = 'VISITA_GUIDATA' | 'MOSTRA' | 'LABORATORIO';
export type StatoEvento = 'PROGRAMMATO' | 'DA_RIPROGRAMMARE' | 'ANNULLATO';

export interface Evento {
  id?: number;
  titolo: string;
  descrizione?: string;
  tipo: TipoEvento;
  dataInizio: string;
  dataFine: string;
  capienzaMax: number;
  stato?: StatoEvento;
}