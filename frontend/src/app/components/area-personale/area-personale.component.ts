import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RichiestaFerie, TipoRichiesta } from '../../models/richiesta-ferie.model';
import { Notifica } from '../../models/notifica.model';
import { FerieService } from '../../services/ferie.service';
import { NotificaService } from '../../services/notifica.service';
import { Turno } from '../../models/turno.model';
import { TurniService } from '../../services/turni.service';
import { calcolaSettimana, etichettaIntervallo, GiornoSettimana } from '../../utils/calendario-settimana';
import { AuthService } from '../../services/auth.service';

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
  turni: Turno[] = [];
  vistaTurni: 'calendario' | 'lista' = 'calendario';
  riferimentoSettimana = new Date();
  giorni: GiornoSettimana[] = [];

  constructor(
    private ferieService: FerieService,
    private notificaService: NotificaService,
    private turniService: TurniService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.caricaRichieste();
    this.caricaNotifiche();
    this.aggiornaSettimana();
    this.caricaTurni();
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

  caricaTurni(): void {
    const dipendenteId = Number(localStorage.getItem('dipendenteId'));
    //console.log('dipendenteId letto:', dipendenteId);
    if (dipendenteId) {
      this.turniService.getPerDipendente(dipendenteId).subscribe(t => {
        //console.log('Turni ricevuti:', t);
        this.turni = t;
      });
    }
  }

  private aggiornaSettimana(): void {
    this.giorni = calcolaSettimana(this.riferimentoSettimana);
  }

  get etichettaSettimana(): string {
    return etichettaIntervallo(this.giorni);
  }

  settimanaPrecedente(): void {
    this.riferimentoSettimana = new Date(this.riferimentoSettimana);
    this.riferimentoSettimana.setDate(this.riferimentoSettimana.getDate() - 7);
    this.aggiornaSettimana();
  }

  settimanaSuccessiva(): void {
    this.riferimentoSettimana = new Date(this.riferimentoSettimana);
    this.riferimentoSettimana.setDate(this.riferimentoSettimana.getDate() + 7);
    this.aggiornaSettimana();
  }

  turniDelGiorno(dataIso: string): Turno[] {
    return this.turni
      .filter(t => t.data === dataIso)
      .sort((a, b) => a.oraInizio.localeCompare(b.oraInizio));
  }

  get turniOrdinatiLista(): Turno[] {
    return [...this.turni].sort((a, b) => a.data.localeCompare(b.data) || a.oraInizio.localeCompare(b.oraInizio));
  } 

}