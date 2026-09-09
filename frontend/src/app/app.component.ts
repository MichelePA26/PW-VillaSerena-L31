import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { AuthService } from './services/auth.service';
import { filter } from 'rxjs';
import { NotificheDropdownComponent } from './components/notifiche-dropdown/notifiche-dropdown.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive,NotificheDropdownComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  mostraNavbar = true;
  menuAperto = false;

  constructor(public auth: AuthService, 
              private router: Router, ) {
    this.router.events.pipe(
      filter(evento => evento instanceof NavigationEnd)
    ).subscribe(() => {
      const rottaAttuale = this.router.url;
      this.mostraNavbar = !rottaAttuale.startsWith('/login')
        && !rottaAttuale.startsWith('/registrati')
        && !rottaAttuale.startsWith('/admin');
      this.menuAperto = false; // chiudi il menu mobile ad ogni cambio pagina
    });
  }

  get ruolo(): string | null {
    return this.auth.getRuolo();
  }

  toggleMenu(): void {
    this.menuAperto = !this.menuAperto;
  }

  logout(): void {
    this.auth.logout();
    window.location.href = '/';
  }

}