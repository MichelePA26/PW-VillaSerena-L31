import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RichiestaFerie, TipoRichiesta } from '../../models/richiesta-ferie.model';
import { Notifica } from '../../models/notifica.model';
import { FerieService } from '../../services/ferie.service';
import { NotificaService } from '../../services/notifica.service';

@Component({
  selector: 'app-area-personale',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './area-personale.component.html',
  styleUrl: './area-personale.component.css'
})
export class AreaPersonaleComponent implements OnInit {
  richieste: RichiestaFerie[] = [];
  notifiche: Notifica[] = [];

  modaleAperta = false;
  nuovaRichiesta: RichiestaFerie = this.formVuoto();
  messaggio = '';

  constructor(
    private ferieService: FerieService,
    private notificaService: NotificaService
  ) {}

  ngOnInit(): void {
    this.caricaRichieste();
    this.caricaNotifiche();
  }

  caricaRichieste(): void {
    this.ferieService.getMie().subscribe(r => this.richieste = r);
  }

  caricaNotifiche(): void {
    this.notificaService.getMie().subscribe(n => this.notifiche = n);
  }

  segnaComeLetta(notifica: Notifica): void {
    this.notificaService.segnaComeLetta(notifica.id).subscribe(() => {
      this.notifiche = this.notifiche.filter(n => n.id !== notifica.id);
    });
  }

  apriModale(): void {
    this.nuovaRichiesta = this.formVuoto();
    this.messaggio = '';
    this.modaleAperta = true;
  }

  chiudiModale(): void {
    this.modaleAperta = false;
  }

  onTipoCambiato(): void {
    // Per una ferie, la data di fine può essere diversa; per un permesso
    // forziamo la data di fine sulla stessa data di inizio.
    if (this.nuovaRichiesta.tipo === 'PERMESSO') {
      this.nuovaRichiesta.dataFine = this.nuovaRichiesta.dataInizio;
    }
  }

  invia(): void {
    if (this.nuovaRichiesta.tipo === 'PERMESSO') {
      this.nuovaRichiesta.dataFine = this.nuovaRichiesta.dataInizio;
    }

    this.ferieService.crea(this.nuovaRichiesta).subscribe({
      next: () => {
        this.caricaRichieste();
        this.modaleAperta = false;
      },
      error: err => this.messaggio = err.error?.errore || 'Richiesta non riuscita. Verifica i dati inseriti.'
    });
  }

  private formVuoto(): RichiestaFerie {
    return { tipo: 'FERIE', dataInizio: '', dataFine: '', motivo: '' };
  }
}