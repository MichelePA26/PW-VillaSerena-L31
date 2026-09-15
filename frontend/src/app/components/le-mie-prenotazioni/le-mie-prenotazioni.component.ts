import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Prenotazione } from '../../models/prenotazione.model';
import { PrenotazioniService } from '../../services/prenotazioni.service';
import { BigliettoComponent } from '../biglietto/biglietto.component';

@Component({
  selector: 'app-le-mie-prenotazioni',
  standalone: true,
  imports: [CommonModule, RouterLink, BigliettoComponent],
  templateUrl: './le-mie-prenotazioni.component.html',
  styleUrl: './le-mie-prenotazioni.component.css'
})
export class LeMiePrenotazioniComponent implements OnInit {
  prenotazioni: Prenotazione[] = [];
  caricamento = true;

  bigliettoAperto = false;
  prenotazioneSelezionata: Prenotazione | null = null;

  dialogoRimborsoAperto = false;
  prenotazionePerRimborso: Prenotazione | null = null;
  elaborazioneInCorso = false;

  constructor(private prenotazioniService: PrenotazioniService) {}

  ngOnInit(): void {
    this.carica();
  }

  carica(): void {
    this.caricamento = true;
    this.prenotazioniService.getMie().subscribe({
      next: p => { this.prenotazioni = p; this.caricamento = false; },
      error: () => this.caricamento = false
    });
  }

  etichettaStato(p: Prenotazione): string {
    const etichette: Record<string, string> = {
      CONFERMATA: 'Confermata',
      ANNULLATA: 'Annullata',
      IN_ATTESA_PAGAMENTO: 'In attesa di pagamento',
      IN_ATTESA_MIGRAZIONE: 'In attesa di risposta',
      RIMBORSATA: 'Rimborsata'
    };
    return etichette[p.stato || 'CONFERMATA'] || p.stato || '';
  }

  isPagato(p: Prenotazione): boolean {
    return (p.prezzoEvento || 0) > 0;
  }

  importoTotale(p: Prenotazione): number {
    return (p.prezzoEvento || 0) * p.numeroPosti;
  }

  accettaNuovaData(p: Prenotazione): void {
    this.elaborazioneInCorso = true;
    this.prenotazioniService.accettaNuovaData(p.id!).subscribe({
      next: () => { this.elaborazioneInCorso = false; this.carica(); },
      error: err => {
        this.elaborazioneInCorso = false;
        alert(err.error?.errore || 'Operazione non riuscita.');
      }
    });
  }

  // Evento a pagamento: apre il dialogo di conferma con i dettagli del rimborso
  apriDialogoRimborso(p: Prenotazione): void {
    this.prenotazionePerRimborso = p;
    this.dialogoRimborsoAperto = true;
  }

  chiudiDialogoRimborso(): void {
    this.dialogoRimborsoAperto = false;
    this.prenotazionePerRimborso = null;
  }

  confermaRimborso(): void {
    if (!this.prenotazionePerRimborso) return;
    this.elaborazioneInCorso = true;
    this.prenotazioniService.richiediRimborso(this.prenotazionePerRimborso.id!).subscribe({
      next: () => {
        this.elaborazioneInCorso = false;
        this.chiudiDialogoRimborso();
        this.carica();
      },
      error: err => {
        this.elaborazioneInCorso = false;
        alert(err.error?.errore || 'Operazione non riuscita.');
      }
    });
  }

  // Evento gratuito: niente da rimborsare, solo una conferma semplice
  annullaGratuita(p: Prenotazione): void {
    if (!confirm('Confermi di voler annullare questa prenotazione?')) return;
    this.elaborazioneInCorso = true;
    this.prenotazioniService.richiediRimborso(p.id!).subscribe({
      next: () => { this.elaborazioneInCorso = false; this.carica(); },
      error: err => {
        this.elaborazioneInCorso = false;
        alert(err.error?.errore || 'Operazione non riuscita.');
      }
    });
  }

  apriBiglietto(prenotazione: Prenotazione): void {
    this.prenotazioneSelezionata = prenotazione;
    this.bigliettoAperto = true;
  }

  chiudiBiglietto(): void {
    this.bigliettoAperto = false;
  }
}