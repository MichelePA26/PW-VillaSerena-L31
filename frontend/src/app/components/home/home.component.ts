import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Opera } from '../../models/opera.model';
import { CatalogoService } from '../../services/catalogo.service';

interface Tappa {
  anno: string;
  titolo: string;
  testo: string;
}

interface Statistica {
  valore: string;
  etichetta: string;
}

interface InfoCard {
  icona: string;
  titolo: string;
  righe: string[];
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  @ViewChild('percorsoTrack') percorsoTrack?: ElementRef<HTMLDivElement>;

  opereInEvidenza: Opera[] = [];

  private readonly annoFondazione = 1962;
  readonly anniStoria = new Date().getFullYear() - this.annoFondazione;

  readonly tappeStoria: Tappa[] = [
    {
      anno: '1887',
      titolo: 'Una residenza nobiliare',
      testo: "Villa Serena nasce come dimora signorile alla fine dell'Ottocento, tra giardini all'italiana e saloni affrescati."
    },
    {
      anno: '1962',
      titolo: 'Apre il museo',
      testo: "Dopo decenni di abbandono, il Comune restaura la villa e la trasforma in sede museale dedicata all'arte moderna."
    },
    {
      anno: 'Oggi',
      titolo: 'Una collezione viva',
      testo: 'Donazioni e collaborazioni con artisti contemporanei fanno crescere il percorso, tra Novecento italiano e nuove ricerche.'
    }
  ];

  readonly statistiche: Statistica[] = [
    { valore: `${this.anniStoria}`, etichetta: 'Anni di storia' },
    { valore: '180+', etichetta: 'Opere in collezione' },
    { valore: '12', etichetta: 'Sale espositive' },
    { valore: '45+', etichetta: 'Artisti rappresentati' }
  ];

  readonly infoMuseo: InfoCard[] = [
    { icona: 'fa-regular fa-clock', titolo: 'Orari di apertura', righe: ['Martedì – Domenica', '9:00 – 19:00'] },
    { icona: 'fa-solid fa-location-dot', titolo: 'Dove siamo', righe: ['Via delle Arti, 12', 'Villa Serena'] },
    { icona: 'fa-solid fa-ticket', titolo: 'Visite guidate', righe: ['Ogni sabato alle 11:00', 'Su prenotazione'] }
  ];

  constructor(private catalogoService: CatalogoService) {}

  ngOnInit(): void {
    this.catalogoService.getOpere().subscribe(opere => {
      this.opereInEvidenza = opere.slice(0, 6);
    });
  }

  numeroTappa(indice: number): string {
    const numero = indice + 1;
    return numero < 10 ? `0${numero}` : `${numero}`;
  }

  scorriPercorso(direzione: number): void {
    const track = this.percorsoTrack?.nativeElement;
    if (!track) {
      return;
    }
    track.scrollBy({ left: track.clientWidth * 0.8 * direzione, behavior: 'smooth' });
  }
}
