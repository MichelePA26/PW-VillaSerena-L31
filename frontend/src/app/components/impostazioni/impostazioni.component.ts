import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-impostazioni',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="impostazioni-container">
      <p class="eyebrow">Account</p>
      <h1>Impostazioni</h1>
      <p class="placeholder">Questa sezione sarà completata in un prossimo passo.</p>
    </div>
  `,
  styles: [`
    .impostazioni-container { padding: 2.5rem; font-family: "Poppins", sans-serif; }
    .eyebrow { text-transform: uppercase; letter-spacing: 0.15rem; font-size: 0.8rem; color: #c9a876; margin-bottom: 0.4rem; }
    h1 { font-family: "Playfair Display", Georgia, serif; color: #2b2320; margin-bottom: 1rem; }
    .placeholder { color: #a89a89; }
  `]
})
export class ImpostazioniComponent {}