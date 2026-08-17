import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css'
})
export class AuthComponent {
  flipped = false;
  ricordami = false;
  mostraPasswordLogin = false;
  mostraPasswordRegistrazione = false;

  loginEmail = '';
  loginPassword = '';
  erroreLogin = '';

  regNome = '';
  regCognome = '';
  regEmail = '';
  regPassword = '';
  messaggioRegistrazione = '';
  registrazioneRiuscita = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    // La rotta decide da quale faccia del flip-card si parte
    // (vedi app.routes.ts: data.modalitaIniziale)
    this.flipped = this.route.snapshot.data['modalitaIniziale'] === 'registrazione';
  }

  vaiARegistrazione(): void { this.flipped = true; }
  vaiALogin(): void { this.flipped = false; }

  accedi(): void {
    this.erroreLogin = '';
    this.authService.login(this.loginEmail, this.loginPassword).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => this.erroreLogin = 'Email o password non corrette.'
    });
  }

  registrati(): void {
    this.messaggioRegistrazione = '';
    this.authService.register(
      { nome: this.regNome, cognome: this.regCognome, email: this.regEmail },
      this.regPassword
    ).subscribe({
      next: () => {
        this.registrazioneRiuscita = true;
        this.messaggioRegistrazione = 'Registrazione completata! Ora puoi accedere.';
        setTimeout(() => {
          this.flipped = false;
          this.messaggioRegistrazione = '';
        }, 1500);
      },
      error: () => {
        this.registrazioneRiuscita = false;
        this.messaggioRegistrazione = 'Registrazione non riuscita. Controlla i dati inseriti.';
      }
    });
  }

  toggleMostraPasswordLogin(): void {
    this.mostraPasswordLogin = !this.mostraPasswordLogin;
  }

  toggleMostraPasswordRegistrazione(): void {
    this.mostraPasswordRegistrazione = !this.mostraPasswordRegistrazione;
  }

}