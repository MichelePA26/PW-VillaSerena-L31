import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Prenotazione } from '../../models/prenotazione.model';
import { Evento } from '../../models/evento.model';
import { PrenotazioniService } from '../../services/prenotazioni.service';
import { EventiService } from '../../services/eventi.service';

@Component({
  selector: 'app-gestione-prenotazioni',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestione-prenotazioni.component.html',
  styleUrl: './gestione-prenotazioni.component.css'
})
export class GestionePrenotazioniComponent implements OnInit {
  prenotazioni: Prenotazione[] = [];
  eventi: Evento[] = [];
  eventoFiltro: number | null = null;
  ricerca = '';

  paginaCorrente = 1;
  elementiPerPagina = 10;

  constructor(
    private prenotazioniService: PrenotazioniService,
    private eventiService: EventiService
  ) {}

  ngOnInit(): void {
    this.eventiService.getEventi().subscribe(e => this.eventi = e);
    this.carica();
  }

  carica(): void {
    this.prenotazioniService.getTutte(this.eventoFiltro || undefined).subscribe(p => {
      this.prenotazioni = p;
      this.paginaCorrente = 1;
    });
  }

  get prenotazioniFiltrate(): Prenotazione[] {
    const termine = this.ricerca.trim().toLowerCase();
    if (!termine) return this.prenotazioni;
    return this.prenotazioni.filter(p =>
      p.utenteNome?.toLowerCase().includes(termine) ||
      p.codiceBiglietto?.toLowerCase().includes(termine)
    );
  }

  get totalePagine(): number {
    return Math.max(1, Math.ceil(this.prenotazioniFiltrate.length / this.elementiPerPagina));
  }

  get prenotazioniPaginate(): Prenotazione[] {
    const inizio = (this.paginaCorrente - 1) * this.elementiPerPagina;
    return this.prenotazioniFiltrate.slice(inizio, inizio + this.elementiPerPagina);
  }

  get indiceIniziale(): number {
    return this.prenotazioniFiltrate.length === 0 ? 0 : (this.paginaCorrente - 1) * this.elementiPerPagina + 1;
  }

  get indiceFinale(): number {
    return Math.min(this.paginaCorrente * this.elementiPerPagina, this.prenotazioniFiltrate.length);
  }

  paginaPrecedente(): void { if (this.paginaCorrente > 1) this.paginaCorrente--; }
  paginaSuccessiva(): void { if (this.paginaCorrente < this.totalePagine) this.paginaCorrente++; }

  etichettaStato(stato?: string): string {
    const etichette: Record<string, string> = {
      CONFERMATA: 'Confermata',
      ANNULLATA: 'Annullata',
      IN_ATTESA_PAGAMENTO: 'In attesa pagamento',
      IN_ATTESA_MIGRAZIONE: 'In attesa risposta',
      RIMBORSATA: 'Rimborsata'
    };
    return etichette[stato || ''] || stato || '';
  }
}