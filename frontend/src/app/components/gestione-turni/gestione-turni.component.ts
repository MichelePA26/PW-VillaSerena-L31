import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Turno } from '../../models/turno.model';
import { Dipendente } from '../../models/dipendente.model';
import { TurniService } from '../../services/turni.service';
import { PersonaleService } from '../../services/personale.service';
import { calcolaSettimana, etichettaIntervallo, coloreDipendente, GiornoSettimana } from '../../utils/calendario-settimana';

@Component({
  selector: 'app-gestione-turni',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestione-turni.component.html',
  styleUrl: './gestione-turni.component.css'
})
export class GestioneTurniComponent implements OnInit {
  turni: Turno[] = [];
  dipendenti: Dipendente[] = [];

  vista: 'calendario' | 'lista' = 'calendario';
  riferimentoSettimana = new Date();
  giorni: GiornoSettimana[] = [];

  modaleAperta = false;
  turnoCorrente: Turno = this.formVuoto();
  inModifica = false;
  messaggio = '';

  constructor(
    private turniService: TurniService,
    private personaleService: PersonaleService
  ) {}

  ngOnInit(): void {
    this.aggiornaSettimana();
    this.carica();
    this.personaleService.getDipendenti().subscribe(d => this.dipendenti = d.filter(x => x.stato === 'ATTIVO'));
  }

  carica(): void {
    this.turniService.getTutti().subscribe(t => this.turni = t);
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

  colore(dipendenteId: number): string {
    return coloreDipendente(dipendenteId);
  }

  get turniOrdinatiLista(): Turno[] {
    return [...this.turni].sort((a, b) => a.data.localeCompare(b.data) || a.oraInizio.localeCompare(b.oraInizio));
  }

  apriModaleNuova(dataIso?: string): void {
    this.turnoCorrente = this.formVuoto();
    if (dataIso) this.turnoCorrente.data = dataIso;
    this.inModifica = false;
    this.messaggio = '';
    this.modaleAperta = true;
  }

  apriModaleModifica(turno: Turno): void {
    this.turnoCorrente = { ...turno };
    this.inModifica = true;
    this.messaggio = '';
    this.modaleAperta = true;
  }

  chiudiModale(): void {
    this.modaleAperta = false;
  }

  salva(): void {
    const operazione = this.inModifica
      ? this.turniService.aggiorna(this.turnoCorrente.id!, this.turnoCorrente)
      : this.turniService.crea(this.turnoCorrente);

    operazione.subscribe({
      next: () => {
        this.carica();
        this.modaleAperta = false;
      },
      error: err => this.messaggio = err.error?.errore || 'Operazione non riuscita.'
    });
  }

  elimina(turno: Turno): void {
    if (!confirm(`Confermi la rimozione del turno di ${turno.nomeDipendente} del ${turno.data}?`)) return;
    this.turniService.elimina(turno.id!).subscribe(() => this.carica());
  }

  private formVuoto(): Turno {
    return { dipendenteId: 0, data: '', oraInizio: '', oraFine: '', reparto: '' };
  }
}