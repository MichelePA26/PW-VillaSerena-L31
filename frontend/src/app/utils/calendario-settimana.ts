export interface GiornoSettimana {
  data: Date;
  dataIso: string;
  etichetta: string;
  numero: number;
  oggi: boolean;
}

export function calcolaSettimana(riferimento: Date): GiornoSettimana[] {
  const giorni: GiornoSettimana[] = [];
  const giornoSettimana = riferimento.getDay(); // 0 = domenica
  const offsetLunedi = giornoSettimana === 0 ? -6 : 1 - giornoSettimana;

  const lunedi = new Date(riferimento);
  lunedi.setDate(riferimento.getDate() + offsetLunedi);

  const etichette = ['Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab', 'Dom'];
  const oggi = new Date();
  oggi.setHours(0, 0, 0, 0);

  for (let i = 0; i < 7; i++) {
    const giorno = new Date(lunedi);
    giorno.setDate(lunedi.getDate() + i);
    giorno.setHours(0, 0, 0, 0);

    giorni.push({
      data: giorno,
      dataIso: formattaIso(giorno),
      etichetta: etichette[i],
      numero: giorno.getDate(),
      oggi: giorno.getTime() === oggi.getTime()
    });
  }

  return giorni;
}

export function formattaIso(data: Date): string {
  const anno = data.getFullYear();
  const mese = String(data.getMonth() + 1).padStart(2, '0');
  const giorno = String(data.getDate()).padStart(2, '0');
  return `${anno}-${mese}-${giorno}`;
}

export function etichettaIntervallo(giorni: GiornoSettimana[]): string {
  const primo = giorni[0].data;
  const ultimo = giorni[6].data;
  const opzioni: Intl.DateTimeFormatOptions = { day: 'numeric', month: 'short' };
  return `${primo.toLocaleDateString('it-IT', opzioni)} - ${ultimo.toLocaleDateString('it-IT', opzioni)}`;
}

// Palette di colori per distinguere i dipendenti nel calendario HR
const PALETTE = ['#c9a876', '#8e6b8f', '#5f8b7a', '#b06a4f', '#5b7fa6', '#a3763f', '#6f7b8f'];

export function coloreDipendente(dipendenteId: number): string {
  return PALETTE[dipendenteId % PALETTE.length];
}