import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PersonaleService } from '../../services/personale.service';
import { Dipendente } from '../../models/dipendente.model';
import { NotificheDropdownComponent } from '../notifiche-dropdown/notifiche-dropdown.component';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, NotificheDropdownComponent],
  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.css'
})
export class AdminLayoutComponent implements OnInit {
  menuMobileAperto = false;
  menuUtenteAperto = false;
  profilo: Dipendente | null = null;
  numeroNotifiche = 0;

  constructor(
    public auth: AuthService,
    private personaleService: PersonaleService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.personaleService.getMioProfilo().subscribe(profilo => this.profilo = profilo);
  }

  get iniziali(): string {
    if (!this.profilo) return '';
    const parti = this.profilo.nomeCompleto.split(' ');
    return parti.map(p => p.charAt(0).toUpperCase()).slice(0, 2).join('');
  }

  toggleMenuMobile(): void {
    this.menuMobileAperto = !this.menuMobileAperto;
    console.log('Nuovo valore:', this.menuMobileAperto);
  }

  toggleMenuUtente(): void {
    this.menuUtenteAperto = !this.menuUtenteAperto;
  }

  chiudiMenuUtente(): void {
    this.menuUtenteAperto = false;
  }

  torniAlSito(): void {
    this.router.navigate(['/']);
  }

  logout(): void {
    this.auth.logout();
    window.location.href = '/';
  }
}