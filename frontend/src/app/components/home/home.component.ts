import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

// Home minima: serve solo a verificare visivamente che login/logout
// funzionino. Le funzionalità vere (catalogo, prenotazioni, ecc.)
// arriveranno nelle prossime "fette" del progetto.
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html'
})
export class HomeComponent {
  constructor(public auth: AuthService) {}

  logout(): void {
    this.auth.logout();
    window.location.reload();
  }
}
