import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RichiestaFerie } from '../../models/richiesta-ferie.model';
import { Notifica } from '../../models/notifica.model';
import { FerieService } from '../../services/ferie.service';
import { NotificaService } from '../../services/notifica.service';

@Component({
  selector: 'app-gestione-ferie',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestione-ferie.component.html',
  styleUrl: './gestione-ferie.component.css'
})
export class GestioneFerieComponent implements OnInit {
  richieste: RichiestaFerie[] = [];
  notifiche: Notifica[] = [];

  constructor(
    private ferieService: FerieService,
    private notificaService: NotificaService
  ) {}

  ngOnInit(): void {
    this.carica();
    this.caricaNotifiche();
  }

  carica(): void {
    this.ferieService.getTutte().subscribe(r => this.richieste = r);
  }

  caricaNotifiche(): void {
    this.notificaService.getMie().subscribe(n => this.notifiche = n);
  }

  segnaComeLetta(notifica: Notifica): void {
    this.notificaService.segnaComeLetta(notifica.id).subscribe(() => {
      this.notifiche = this.notifiche.filter(n => n.id !== notifica.id);
    });
  }

  get inAttesa(): RichiestaFerie[] {
    return this.richieste.filter(r => r.stato === 'IN_ATTESA');
  }

  get decise(): RichiestaFerie[] {
    return this.richieste.filter(r => r.stato !== 'IN_ATTESA');
  }

  approva(richiesta: RichiestaFerie): void {
    this.ferieService.aggiornaStato(richiesta.id!, 'APPROVATA').subscribe(() => this.carica());
  }

  rifiuta(richiesta: RichiestaFerie): void {
    this.ferieService.aggiornaStato(richiesta.id!, 'RIFIUTATA').subscribe(() => this.carica());
  }
}