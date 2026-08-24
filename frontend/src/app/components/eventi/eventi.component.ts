import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Evento } from '../../models/evento.model';
import { EventiService } from '../../services/eventi.service';
import { PrenotazioniService } from '../../services/prenotazioni.service';
import { AuthService } from '../../services/auth.service';
import { RouterLink } from '@angular/router';

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

  constructor(
    private eventiService: EventiService,
    private prenotazioniService: PrenotazioniService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.eventiService.getEventi().subscribe({
      next: eventi => { this.eventi = eventi; this.caricamento = false; },
      error: () => this.caricamento = false
    });
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