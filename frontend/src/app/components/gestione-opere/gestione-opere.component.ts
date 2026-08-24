import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Opera } from '../../models/opera.model';
import { Collezione } from '../../models/collezione.model';
import { CatalogoService } from '../../services/catalogo.service';
import { CollezioniService } from '../../services/collezioni.service';
import { UploadService } from '../../services/upload.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-gestione-opere',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestione-opere.component.html',
  styleUrl: './gestione-opere.component.css'
})
export class GestioneOpereComponent implements OnInit {
  opere: Opera[] = [];
  collezioni: Collezione[] = [];

  //  Ricerca e paginazione 
  ricerca = '';
  paginaCorrente = 1;
  elementiPerPagina = 10;

  //  Stato della modale 
  modaleAperta = false;
  operaCorrente: Opera = this.formVuoto();
  inModifica = false;
  messaggio = '';

  fileSelezionato: File | null = null;
  anteprimaUrl: string | null = null;
  caricamentoImmagine = false;

  // Lightbox
  lightboxAperto = false;
  lightboxUrl: string | null = null;

  //  Ordinamento
  colonnaOrdinamento: 'titolo' | 'autore' | 'collezione' | 'anno' | 'dataModifica' | null = null;
  direzioneOrdinamento: 'asc' | 'desc' = 'asc';

  constructor(
    private catalogoService: CatalogoService,
    private collezioniService: CollezioniService,
    private uploadService: UploadService
  ) {}

  ngOnInit(): void {
    this.carica();
    this.collezioniService.getCollezioni().subscribe(c => this.collezioni = c);
  }

  carica(): void {
    this.catalogoService.getOpere().subscribe(opere => this.opere = opere);
  }

  get opereFiltrate(): Opera[] {
    const termine = this.ricerca.trim().toLowerCase();
    let risultato = !termine
      ? [...this.opere]
      : this.opere.filter(o =>
          o.titolo.toLowerCase().includes(termine) ||
          o.autore.toLowerCase().includes(termine)
        );

    if (this.colonnaOrdinamento) {
      const direzione = this.direzioneOrdinamento === 'asc' ? 1 : -1;
      risultato = risultato.sort((a, b) => {
        const valA = this.valoreOrdinamento(a, this.colonnaOrdinamento!);
        const valB = this.valoreOrdinamento(b, this.colonnaOrdinamento!);
        if (valA < valB) return -1 * direzione;
        if (valA > valB) return 1 * direzione;
        return 0;
      });
    }

    return risultato;
  }

  private valoreOrdinamento(opera: Opera, colonna: string): string | number {
    switch (colonna) {
      case 'titolo': return opera.titolo?.toLowerCase() ?? '';
      case 'autore': return opera.autore?.toLowerCase() ?? '';
      case 'collezione': return this.nomeCollezione(opera.collezioneId).toLowerCase();
      case 'anno': return opera.anno ?? 0;
      case 'dataModifica': return opera.dataModifica ?? '';
      default: return '';
    }
  }

  get totalePagine(): number {
    return Math.max(1, Math.ceil(this.opereFiltrate.length / this.elementiPerPagina));
  }

  get operePaginate(): Opera[] {
    const inizio = (this.paginaCorrente - 1) * this.elementiPerPagina;
    return this.opereFiltrate.slice(inizio, inizio + this.elementiPerPagina);
  }

  get indiceIniziale(): number {
    return this.opereFiltrate.length === 0 ? 0 : (this.paginaCorrente - 1) * this.elementiPerPagina + 1;
  }

  get indiceFinale(): number {
    return Math.min(this.paginaCorrente * this.elementiPerPagina, this.opereFiltrate.length);
  }

  paginaPrecedente(): void {
    if (this.paginaCorrente > 1) this.paginaCorrente--;
  }

  paginaSuccessiva(): void {
    if (this.paginaCorrente < this.totalePagine) this.paginaCorrente++;
  }

  onRicercaCambiata(): void {
    this.paginaCorrente = 1; // torna alla prima pagina ad ogni nuova ricerca
  }

  nomeCollezione(collezioneId?: number): string {
    if (!collezioneId) return '—';
    return this.collezioni.find(c => c.id === collezioneId)?.nome ?? '—';
  }

  //  Modale 
  apriModaleNuova(): void {
    this.operaCorrente = this.formVuoto();
    this.anteprimaUrl = null;
    this.fileSelezionato = null;
    this.inModifica = false;
    this.messaggio = '';
    this.modaleAperta = true;
  }

  apriModaleModifica(opera: Opera): void {
    this.operaCorrente = { ...opera };
    this.anteprimaUrl = opera.immagineUrl || null;
    this.fileSelezionato = null;
    this.inModifica = true;
    this.messaggio = '';
    this.modaleAperta = true;
  }

  chiudiModale(): void {
    this.modaleAperta = false;
  }

  onFileSelezionato(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.fileSelezionato = input.files[0];
      this.anteprimaUrl = URL.createObjectURL(this.fileSelezionato);
    }
  }

  salva(): void {
    if (this.fileSelezionato) {
      this.caricamentoImmagine = true;
      this.uploadService.carica(this.fileSelezionato).subscribe({
        next: risposta => {
          this.operaCorrente.immagineUrl = `${environment.apiBaseUrl}${risposta.url}`;
          this.caricamentoImmagine = false;
          this.salvaOpera();
        },
        error: () => {
          this.caricamentoImmagine = false;
          this.messaggio = 'Caricamento immagine non riuscito.';
        }
      });
    } else {
      this.salvaOpera();
    }
  }

  private salvaOpera(): void {
    const operazione = this.inModifica
      ? this.catalogoService.aggiornaOpera(this.operaCorrente.id!, this.operaCorrente)
      : this.catalogoService.creaOpera(this.operaCorrente);

    operazione.subscribe({
      next: () => {
        this.carica();
        this.modaleAperta = false;
      },
      error: () => this.messaggio = 'Operazione non riuscita. Verifica i dati inseriti.'
    });
  }

  elimina(id: number): void {
    if (!confirm('Confermi la rimozione di questa opera dal catalogo?')) return;
    this.catalogoService.eliminaOpera(id).subscribe(() => this.carica());
  }

  private formVuoto(): Opera {
    return { titolo: '', autore: '', anno: new Date().getFullYear(), tecnica: '', descrizione: '' };
  }

  apriLightbox(url: string | null | undefined): void {
    if (!url) return;
    this.lightboxUrl = url;
    this.lightboxAperto = true;
  }

  chiudiLightbox(): void {
    this.lightboxAperto = false;
    this.lightboxUrl = null;
  }

  ordinaPer(colonna: 'titolo' | 'autore' | 'collezione' | 'anno' | 'dataModifica'): void {
    if (this.colonnaOrdinamento === colonna) {
      // stessa colonna: inverte la direzione
      this.direzioneOrdinamento = this.direzioneOrdinamento === 'asc' ? 'desc' : 'asc';
    } else {
      // nuova colonna: riparte da ascendente
      this.colonnaOrdinamento = colonna;
      this.direzioneOrdinamento = 'asc';
    }
    this.paginaCorrente = 1; // torna alla prima pagina quando cambia l'ordinamento
  }

  iconaOrdinamento(colonna: string): string {
    if (this.colonnaOrdinamento !== colonna) return 'fa-solid fa-sort';
    return this.direzioneOrdinamento === 'asc' ? 'fa-solid fa-sort-up' : 'fa-solid fa-sort-down';
  }
  
}