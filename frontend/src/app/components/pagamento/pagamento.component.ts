import { AfterViewInit, Component, ElementRef, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PagamentoService } from '../../services/pagamento.service';
import { environment } from '../../../environments/environment';
import { firstValueFrom } from 'rxjs';

declare const paypal: any;

@Component({
  selector: 'app-pagamento',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pagamento.component.html',
  styleUrl: './pagamento.component.css'
})
export class PagamentoComponent implements AfterViewInit {
  @Input() prenotazioneId!: number;
  @Input() importo!: number;
  @Output() pagamentoCompletato = new EventEmitter<void>();
  @ViewChild('paypalButtons', { static: true }) paypalButtons!: ElementRef;

  esito = '';
  errore = false;

  constructor(private pagamentoService: PagamentoService) {}

  ngAfterViewInit(): void {
    this.caricaSdkPaypal().then(() => this.renderizzaBottoni());
  }

  private caricaSdkPaypal(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (typeof paypal !== 'undefined') { resolve(); return; }
      const script = document.createElement('script');
      script.src = `https://www.paypal.com/sdk/js?client-id=${environment.paypalClientId}&currency=EUR&disable-funding=card,mybank,credit,paylater`;
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Impossibile caricare il PayPal JS SDK'));
      document.body.appendChild(script);
    });
  }

  private renderizzaBottoni(): void {
    paypal.Buttons({
        style: { layout: 'vertical', color: 'gold', shape: 'rect', label: 'pay' },

        createOrder: () =>
        firstValueFrom(this.pagamentoService.creaOrdine(this.prenotazioneId))
            .then(ordine => ordine.id),

        onApprove: (data: { orderID: string }) =>
        firstValueFrom(this.pagamentoService.catturaOrdine(data.orderID))
            .then(() => {
            this.esito = 'Pagamento completato con successo!';
            this.errore = false;
            this.pagamentoCompletato.emit();
            }),

        onError: () => {
        this.esito = 'Si è verificato un errore durante il pagamento. Riprova.';
        this.errore = true;
        }
    }).render(this.paypalButtons.nativeElement);
    }
}