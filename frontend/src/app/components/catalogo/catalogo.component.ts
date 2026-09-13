import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Opera } from '../../models/opera.model';
import { CatalogoService } from '../../services/catalogo.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './catalogo.component.html',
  styleUrl: './catalogo.component.css'
})
export class CatalogoComponent implements OnInit {
  opere: Opera[] = [];
  caricamento = true;

  constructor(private catalogoService: CatalogoService) {}

   ngOnInit(): void {
      this.catalogoService.getOpere().subscribe({
        next: opere => { this.opere = opere; this.caricamento = false; },
        error: () => this.caricamento = false
      });
    }
 

 // Metodo Rete locale per test su telefono, aggiornato per correggere l'URL dell'immagini, da attivare in caso di test 
  /* ngOnInit(): void {
    this.catalogoService.getOpere().subscribe({
      next: opere => { 
        this.opere = opere.map(opera => {
          if (opera.immagineUrl) {
            let urlCorretto = opera.immagineUrl;

            // Sostituisce localhost con l'IP corretto rete locale
            if (urlCorretto.includes('localhost:8080')) {
              urlCorretto = urlCorretto.replace('http://localhost:8080', environment.apiBaseUrl);
            } 

            return { ...opera, immagineUrl: urlCorretto };
          }
          return opera;
        });
        this.caricamento = false; 
      },
      error: () => this.caricamento = false
    });
  } */
}