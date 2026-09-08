import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AssunzioneRequest, Dipendente, TipoContratto } from '../../models/dipendente.model';
import { PersonaleService } from '../../services/personale.service';

@Component({
  selector: 'app-gestione-personale',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestione-personale.component.html',
  styleUrl: './gestione-personale.component.css'
})
export class GestionePersonaleComponent implements OnInit {
  dipendenti: Dipendente[] = [];

  //  Ricerca e paginazione 
  ricerca = '';
  paginaCorrente = 1;
  elementiPerPagina = 10;

  //  Ordinamento
  colonnaOrdinamento: 'nomeCompleto' | 'mansione' | 'dataAssunzione' | 'stato' | null = null;
  direzioneOrdinamento: 'asc' | 'desc' = 'asc';

  //Modale assunzione
  modaleAperta = false;
  messaggio = '';

  readonly tipiContratto: { valore: TipoContratto; etichetta: string }[] = [
    { valore: 'TEMPO_INDETERMINATO', etichetta: 'Tempo indeterminato' },
    { valore: 'TEMPO_DETERMINATO', etichetta: 'Tempo determinato' },
    { valore: 'PART_TIME', etichetta: 'Part-time' },
    { valore: 'STAGIONALE', etichetta: 'Stagionale' }
  ];

  nuovaAssunzione: AssunzioneRequest = this.formVuoto();

  constructor(private personaleService: PersonaleService) {}

  ngOnInit(): void {
    this.carica();
  }

  carica(): void {
    this.personaleService.getDipendenti().subscribe(d => this.dipendenti = d);
  }

  // Ricerca + ordinamento + paginazione
  get dipendentiFiltrati(): Dipendente[] {
    const termine = this.ricerca.trim().toLowerCase();
    let risultato = !termine
      ? [...this.dipendenti]
      : this.dipendenti.filter(d =>
          d.nomeCompleto.toLowerCase().includes(termine) ||
          d.mansione?.toLowerCase().includes(termine)
        );

    if (this.colonnaOrdinamento) {
      const direzione = this.direzioneOrdinamento === 'asc' ? 1 : -1;
      risultato = risultato.sort((a, b) => {
        const valA = (a[this.colonnaOrdinamento!] ?? '').toString().toLowerCase();
        const valB = (b[this.colonnaOrdinamento!] ?? '').toString().toLowerCase();
        if (valA < valB) return -1 * direzione;
        if (valA > valB) return 1 * direzione;
        return 0;
      });
    }

    return risultato;
  }

  ordinaPer(colonna: 'nomeCompleto' | 'mansione' | 'dataAssunzione' | 'stato'): void {
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
    return Math.max(1, Math.ceil(this.dipendentiFiltrati.length / this.elementiPerPagina));
  }

  get dipendentiPaginati(): Dipendente[] {
    const inizio = (this.paginaCorrente - 1) * this.elementiPerPagina;
    return this.dipendentiFiltrati.slice(inizio, inizio + this.elementiPerPagina);
  }

  get indiceIniziale(): number {
    return this.dipendentiFiltrati.length === 0 ? 0 : (this.paginaCorrente - 1) * this.elementiPerPagina + 1;
  }

  get indiceFinale(): number {
    return Math.min(this.paginaCorrente * this.elementiPerPagina, this.dipendentiFiltrati.length);
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

  etichettaContratto(tipo?: TipoContratto): string {
    return this.tipiContratto.find(t => t.valore === tipo)?.etichetta ?? '—';
  }

  // Modale
  apriModaleNuova(): void {
    this.nuovaAssunzione = this.formVuoto();
    this.messaggio = '';
    this.modaleAperta = true;
  }

  chiudiModale(): void {
    this.modaleAperta = false;
  }

  assumi(): void {
    this.personaleService.assumi(this.nuovaAssunzione).subscribe({
      next: () => {
        this.carica();
        this.modaleAperta = false;
      },
      error: err => this.messaggio = err.error?.errore || 'Assunzione non riuscita. Verifica i dati inseriti.'
    });
  }

  cessa(dipendente: Dipendente): void {
    if (!confirm(`Confermi la cessazione del rapporto di lavoro con ${dipendente.nomeCompleto}?`)) return;
    this.personaleService.cessa(dipendente.id).subscribe(() => this.carica());
  }

  private formVuoto(): AssunzioneRequest {
    return {
      utenteId: 0,
      mansione: '',
      codiceFiscale: '',
      dataNascita: '',
      telefono: '',
      indirizzo: '',
      tipoContratto: 'TEMPO_INDETERMINATO',
      livelloInquadramento: '',
      iban: ''
    };
  }
}