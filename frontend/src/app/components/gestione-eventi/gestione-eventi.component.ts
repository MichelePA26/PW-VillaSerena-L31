import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Evento, TipoEvento } from '../../models/evento.model';
import { EventiService } from '../../services/eventi.service';

@Component({
  selector: 'app-gestione-eventi',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestione-eventi.component.html',
  styleUrl: './gestione-eventi.component.css'
})
export class GestioneEventiComponent implements OnInit {
  eventi: Evento[] = [];

  // Ricerca e paginazione 
  ricerca = '';
  paginaCorrente = 1;
  elementiPerPagina = 10;

  // Ordinamento
  colonnaOrdinamento: 'titolo' | 'tipo' | 'dataInizio' | 'capienzaMax' | null = null;
  direzioneOrdinamento: 'asc' | 'desc' = 'asc';

  // Modale 
  modaleAperta = false;
  eventoCorrente: Evento = this.formVuoto();
  inModifica = false;
  messaggio = '';

  readonly tipi: { valore: TipoEvento; etichetta: string }[] = [
    { valore: 'VISITA_GUIDATA', etichetta: 'Visita guidata' },
    { valore: 'MOSTRA', etichetta: 'Mostra' },
    { valore: 'LABORATORIO', etichetta: 'Laboratorio' }
  ];

  constructor(private eventiService: EventiService) {}

  ngOnInit(): void {
    this.carica();
  }

  carica(): void {
    this.eventiService.getEventi().subscribe(eventi => this.eventi = eventi);
  }

  // Ricerca + ordinamento + paginazione
  get eventiFiltrati(): Evento[] {
    const termine = this.ricerca.trim().toLowerCase();
    let risultato = !termine
      ? [...this.eventi]
      : this.eventi.filter(e => e.titolo.toLowerCase().includes(termine));

    if (this.colonnaOrdinamento) {
      const direzione = this.direzioneOrdinamento === 'asc' ? 1 : -1;
      risultato = risultato.sort((a, b) => {
        const valA = this.valoreOrdinamento(a, this.colonnaOrdinamento!);
        const valB = this.valoreOrdinamento(b, this.colonnaOrdinamento!);
        if (valA < valB) return -1 * direzione;
        if (valA > valB) return 1 * direzione;
        return 0;
      });
    }

    return risultato;
  }

  private valoreOrdinamento(evento: Evento, colonna: string): string | number {
    switch (colonna) {
      case 'titolo': return evento.titolo?.toLowerCase() ?? '';
      case 'tipo': return evento.tipo ?? '';
      case 'dataInizio': return evento.dataInizio ?? '';
      case 'capienzaMax': return evento.capienzaMax ?? 0;
      default: return '';
    }
  }

  ordinaPer(colonna: 'titolo' | 'tipo' | 'dataInizio' | 'capienzaMax'): void {
    if (this.colonnaOrdinamento === colonna) {
      this.direzioneOrdinamento = this.direzioneOrdinamento === 'asc' ? 'desc' : 'asc';
    } else {
      this.colonnaOrdinamento = colonna;
      this.direzioneOrdinamento = 'asc';
    }
    this.paginaCorrente = 1;
  }

  iconaOrdinamento(colonna: string): string {
    if (this.colonnaOrdinamento !== colonna) return 'fa-solid fa-sort';
    return this.direzioneOrdinamento === 'asc' ? 'fa-solid fa-sort-up' : 'fa-solid fa-sort-down';
  }

  get totalePagine(): number {
    return Math.max(1, Math.ceil(this.eventiFiltrati.length / this.elementiPerPagina));
  }

  get eventiPaginati(): Evento[] {
    const inizio = (this.paginaCorrente - 1) * this.elementiPerPagina;
    return this.eventiFiltrati.slice(inizio, inizio + this.elementiPerPagina);
  }

  get indiceIniziale(): number {
    return this.eventiFiltrati.length === 0 ? 0 : (this.paginaCorrente - 1) * this.elementiPerPagina + 1;
  }

  get indiceFinale(): number {
    return Math.min(this.paginaCorrente * this.elementiPerPagina, this.eventiFiltrati.length);
  }

  paginaPrecedente(): void {
    if (this.paginaCorrente > 1) this.paginaCorrente--;
  }

  paginaSuccessiva(): void {
    if (this.paginaCorrente < this.totalePagine) this.paginaCorrente++;
  }

  onRicercaCambiata(): void {
    this.paginaCorrente = 1;
  }

  etichettaTipo(tipo: TipoEvento): string {
    return this.tipi.find(t => t.valore === tipo)?.etichetta ?? tipo;
  }

  // Modale
  apriModaleNuova(): void {
    this.eventoCorrente = this.formVuoto();
    this.inModifica = false;
    this.messaggio = '';
    this.modaleAperta = true;
  }

  apriModaleModifica(evento: Evento): void {
    this.eventoCorrente = { ...evento };
    this.inModifica = true;
    this.messaggio = '';
    this.modaleAperta = true;
  }

  chiudiModale(): void {
    this.modaleAperta = false;
  }

  salva(): void {
    const operazione = this.inModifica
      ? this.eventiService.aggiornaEvento(this.eventoCorrente.id!, this.eventoCorrente)
      : this.eventiService.creaEvento(this.eventoCorrente);

    operazione.subscribe({
      next: () => {
        this.carica();
        this.modaleAperta = false;
      },
      error: () => this.messaggio = 'Operazione non riuscita. Verifica i dati inseriti.'
    });
  }

  elimina(id: number): void {
    if (!confirm('Confermi la rimozione di questo evento?')) return;
    this.eventiService.eliminaEvento(id).subscribe({
      next: () => this.carica(),
      error: err => this.messaggio = err.error?.errore || 'Impossibile eliminare l\'evento.'
    });
  }

  private formVuoto(): Evento {
    return {
      titolo: '',
      descrizione: '',
      tipo: 'VISITA_GUIDATA',
      dataInizio: '',
      dataFine: '',
      capienzaMax: 20
    };
  }
}