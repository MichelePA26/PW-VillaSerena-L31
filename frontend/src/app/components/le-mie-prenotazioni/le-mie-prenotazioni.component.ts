import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Prenotazione, StatoEvento } from '../../models/prenotazione.model';
import { PrenotazioniService } from '../../services/prenotazioni.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-le-mie-prenotazioni',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './le-mie-prenotazioni.component.html',
  styleUrl: './le-mie-prenotazioni.component.css'
})
export class LeMiePrenotazioniComponent implements OnInit {
  prenotazioni: Prenotazione[] = [];
  caricamento = true;

  constructor(private prenotazioniService: PrenotazioniService) {}

  ngOnInit(): void {
    this.prenotazioniService.getMie().subscribe({
      next: p => { this.prenotazioni = p; this.caricamento = false; },
      error: () => this.caricamento = false
    });
  }

  etichettaStato(stato?: StatoEvento): string {
    const etichette: Record<string, string> = {
      PROGRAMMATO: 'Confermata',
      DA_RIPROGRAMMARE: 'In attesa di nuova data',
      ANNULLATO: 'Evento annullato'
    };
    return etichette[stato || 'PROGRAMMATO'];
  }
}