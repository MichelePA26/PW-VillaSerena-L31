export type TipoContratto = 'TEMPO_DETERMINATO' | 'TEMPO_INDETERMINATO' | 'PART_TIME' | 'STAGIONALE';
export type StatoDipendente = 'ATTIVO' | 'CESSATO';

// Sola lettura, riflette il DipendenteDTO, con i dati sensibili già mascherati dal backend
export interface Dipendente {
  id: number;
  utenteId: number;
  nomeCompleto: string;
  mansione: string;
  dataAssunzione: string;
  dataCessazione?: string;
  stato: StatoDipendente;
  tipoContratto?: TipoContratto;
  livelloInquadramento?: string;
  telefono?: string;
  dataNascita?: string;
  indirizzo?: string;
  codiceFiscaleMascherato?: string;
  ibanMascherato?: string;
}

// Usato solo in fase di assunzione: i dati sensibili viaggiano in chiaro
// verso il backend via HTTPS e vengono cifrati lato server prima del salvataggio
export interface AssunzioneRequest {
  utenteId: number;
  mansione: string;
  codiceFiscale: string;
  dataNascita: string;
  telefono: string;
  indirizzo: string;
  tipoContratto: TipoContratto;
  livelloInquadramento: string;
  iban: string;
}