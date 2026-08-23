import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Opera } from '../../models/opera.model';
import { CatalogoService } from '../../services/catalogo.service';

@Component({
  selector: 'app-gestione-opere',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestione-opere.component.html',
  styleUrl: './gestione-opere.component.css'
})
export class GestioneOpereComponent implements OnInit {
  opere: Opera[] = [];
  operaCorrente: Opera = this.formVuoto();
  inModifica = false;
  messaggio = '';

  constructor(private catalogoService: CatalogoService) {}

  ngOnInit(): void {
    this.carica();
  }

  carica(): void {
    this.catalogoService.getOpere().subscribe(opere => this.opere = opere);
  }

  salva(): void {
    const operazione = this.inModifica
      ? this.catalogoService.aggiornaOpera(this.operaCorrente.id!, this.operaCorrente)
      : this.catalogoService.creaOpera(this.operaCorrente);

    operazione.subscribe({
      next: () => {
        this.messaggio = this.inModifica ? 'Opera aggiornata.' : 'Opera aggiunta al catalogo.';
        this.resetForm();
        this.carica();
      },
      error: () => this.messaggio = 'Operazione non riuscita. Verifica i dati inseriti.'
    });
  }

  modifica(opera: Opera): void {
    this.operaCorrente = { ...opera };
    this.inModifica = true;
  }

  elimina(id: number): void {
    if (!confirm('Confermi la rimozione di questa opera dal catalogo?')) return;
    this.catalogoService.eliminaOpera(id).subscribe(() => this.carica());
  }

  resetForm(): void {
    this.operaCorrente = this.formVuoto();
    this.inModifica = false;
  }

  private formVuoto(): Opera {
    return { titolo: '', autore: '', anno: new Date().getFullYear(), tecnica: '', descrizione: '' };
  }
}