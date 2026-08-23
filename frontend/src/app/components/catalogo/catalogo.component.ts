import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Opera } from '../../models/opera.model';
import { CatalogoService } from '../../services/catalogo.service';

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
}