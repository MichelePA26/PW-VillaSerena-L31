import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Prenotazione } from '../../models/prenotazione.model';
import { PrenotazioniService } from '../../services/prenotazioni.service';
import { FeedbackService } from '../../services/feedback.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feedback.component.html',
  styleUrl: './feedback.component.css'
})
export class FeedbackComponent implements OnInit {
  miePrenotazioni: Prenotazione[] = [];
  caricamento = true;

  prenotazioneSelezionata: number | null = null;
  voto = 5;
  commento = '';
  messaggio = '';
  invioRiuscito = false;
  invioInCorso = false;

  constructor(
    private prenotazioniService: PrenotazioniService,
    private feedbackService: FeedbackService
  ) {}

  ngOnInit(): void {
    this.prenotazioniService.getMiePrenotazioni().subscribe({
      next: prenotazioni => {
        this.miePrenotazioni = prenotazioni;
        this.caricamento = false;
      },
      error: () => this.caricamento = false
    });
  }

  invia(): void {
    if (!this.prenotazioneSelezionata) {
      this.messaggio = 'Seleziona prima una visita o un evento.';
      this.invioRiuscito = false;
      return;
    }

    this.invioInCorso = true;
    this.feedbackService.invia(this.prenotazioneSelezionata, this.voto, this.commento).subscribe({
      next: () => {
        this.invioInCorso = false;
        this.invioRiuscito = true;
        this.messaggio = 'Grazie per il tuo feedback!';
        this.commento = '';
        this.prenotazioneSelezionata = null;
        this.voto = 5;
      },
      error: err => {
        this.invioInCorso = false;
        this.invioRiuscito = false;
        this.messaggio = err.error?.errore || 'Invio non riuscito. Riprova.';
      }
    });
  }
}