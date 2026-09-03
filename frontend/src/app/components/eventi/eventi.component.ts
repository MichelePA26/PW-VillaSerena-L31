import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Evento } from '../../models/evento.model';
import { EventiService } from '../../services/eventi.service';
import { PrenotazioniService } from '../../services/prenotazioni.service';
import { AuthService } from '../../services/auth.service';
import { RouterLink } from '@angular/router';
import { FeedbackService } from '../../services/feedback.service';
import { Feedback } from '../../models/feedback.model';

@Component({
  selector: 'app-eventi',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './eventi.component.html',
  styleUrl: './eventi.component.css'
})
export class EventiComponent implements OnInit {
  eventi: Evento[] = [];
  caricamento = true;

  eventoSelezionato: Evento | null = null;
  numeroPosti = 1;
  messaggioPrenotazione = '';
  prenotazioneRiuscita = false;
  invioInCorso = false;
  feedbackPerEvento: Map<number, Feedback[]> = new Map();
  eventoRecensioniAperto: Evento | null = null;

  constructor(
    private eventiService: EventiService,
    private prenotazioniService: PrenotazioniService,
    private feedbackService: FeedbackService,
    public auth: AuthService
  ) {}

   ngOnInit(): void {
    this.eventiService.getEventi().subscribe({
      next: eventi => {
        this.eventi = eventi;
        this.caricamento = false;
        eventi.forEach(e => this.caricaFeedback(e.id!));
      },
      error: () => this.caricamento = false
    });
  }

  private caricaFeedback(eventoId: number): void {
    this.feedbackService.getPerEvento(eventoId).subscribe(feedback => {
      this.feedbackPerEvento.set(eventoId, feedback);
    });
  }

  votoMedio(eventoId: number): number | null {
    const feedback = this.feedbackPerEvento.get(eventoId);
    if (!feedback || feedback.length === 0) return null;
    const somma = feedback.reduce((acc, f) => acc + f.voto, 0);
    return Math.round((somma / feedback.length) * 10) / 10;
  }

  numeroRecensioni(eventoId: number): number {
    return this.feedbackPerEvento.get(eventoId)?.length ?? 0;
  }

  apriRecensioni(evento: Evento): void {
    this.eventoRecensioniAperto = evento;
  }

  chiudiRecensioni(): void {
    this.eventoRecensioniAperto = null;
  }

  apriPrenotazione(evento: Evento): void {
    this.eventoSelezionato = evento;
    this.numeroPosti = 1;
    this.messaggioPrenotazione = '';
  }

  chiudiPrenotazione(): void {
    this.eventoSelezionato = null;
  }

  confermaPrenotazione(): void {
    if (!this.eventoSelezionato) return;
    this.invioInCorso = true;
    this.prenotazioniService.prenota(this.eventoSelezionato.id!, this.numeroPosti).subscribe({
      next: () => {
        this.invioInCorso = false;
        this.prenotazioneRiuscita = true;
        this.messaggioPrenotazione = 'Prenotazione confermata!';
        setTimeout(() => this.chiudiPrenotazione(), 1800);
      },
      error: err => {
        this.invioInCorso = false;
        this.prenotazioneRiuscita = false;
        this.messaggioPrenotazione = err.error?.errore || 'Prenotazione non riuscita.';
      }
    });
  }

  etichettaTipo(tipo: string): string {
    const etichette: Record<string, string> = {
      VISITA_GUIDATA: 'Visita guidata',
      MOSTRA: 'Mostra',
      LABORATORIO: 'Laboratorio'
    };
    return etichette[tipo] || tipo;
  }
}