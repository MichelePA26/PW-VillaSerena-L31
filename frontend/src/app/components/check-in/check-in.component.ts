import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Prenotazione } from '../../models/prenotazione.model';
import { PrenotazioniService } from '../../services/prenotazioni.service';

@Component({
  selector: 'app-check-in',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './check-in.component.html',
  styleUrl: './check-in.component.css'
})
export class CheckInComponent {
  codice = '';
  prenotazione: Prenotazione | null = null;
  messaggio = '';
  errore = false;
  ricercaFatta = false;

  constructor(private prenotazioniService: PrenotazioniService) {}

  cerca(): void {
    if (!this.codice.trim()) return;
    this.messaggio = '';
    this.prenotazione = null;
    this.ricercaFatta = true;

    this.prenotazioniService.cercaPerCodice(this.codice.trim()).subscribe({
      next: p => this.prenotazione = p,
      error: err => {
        this.errore = true;
        this.messaggio = err.error?.errore || 'Codice non trovato.';
      }
    });
  }

  confermaIngresso(): void {
    if (!this.prenotazione?.codiceBiglietto) return;
    this.prenotazioniService.effettuaCheckIn(this.prenotazione.codiceBiglietto).subscribe({
      next: p => {
        this.prenotazione = p;
        this.errore = false;
        this.messaggio = 'Ingresso confermato.';
      },
      error: err => {
        this.errore = true;
        this.messaggio = err.error?.errore || 'Impossibile completare il check-in.';
      }
    });
  }

  nuovaRicerca(): void {
    this.codice = '';
    this.prenotazione = null;
    this.messaggio = '';
    this.ricercaFatta = false;
  }
}