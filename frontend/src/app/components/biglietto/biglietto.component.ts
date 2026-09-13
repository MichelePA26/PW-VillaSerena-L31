import { Component, ElementRef, Input, OnChanges, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Prenotazione } from '../../models/prenotazione.model';;
import * as QRCode from 'qrcode';

@Component({
  selector: 'app-biglietto',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './biglietto.component.html',
  styleUrl: './biglietto.component.css'
})
export class BigliettoComponent implements OnChanges {
  @Input() prenotazione: Prenotazione | null = null;
  @Input() aperto = false;
  @ViewChild('canvasQr') canvasQr?: ElementRef<HTMLCanvasElement>;

  ngOnChanges(): void {
    if (this.aperto && this.prenotazione?.codiceBiglietto) {
      setTimeout(() => this.disegnaQr(), 0);
    }
  }

  private disegnaQr(): void {
    if (!this.canvasQr || !this.prenotazione?.codiceBiglietto) return;
    QRCode.toCanvas(this.canvasQr.nativeElement, this.prenotazione.codiceBiglietto, {
      width: 160,
      margin: 1,
      color: { dark: '#2b2320', light: '#ffffff' }
    });
  }
}