import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { PersonaleService } from '../../services/personale.service';
import { Dipendente } from '../../models/dipendente.model';

@Component({
  selector: 'app-impostazioni',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './impostazioni.component.html',
  styleUrl: './impostazioni.component.css'
})
export class ImpostazioniComponent implements OnInit {
  profilo: Dipendente | null = null;

  telefono = '';
  indirizzo = '';
  messaggioProfilo = '';
  successoProfilo = false;

  passwordAttuale = '';
  nuovaPassword = '';
  confermaPassword = '';
  messaggioPassword = '';
  successoPassword = false;

  constructor(
    private authService: AuthService,
    private personaleService: PersonaleService
  ) {}

  ngOnInit(): void {
    this.personaleService.getMioProfilo().subscribe(profilo => {
      this.profilo = profilo;
      this.telefono = profilo.telefono || '';
      this.indirizzo = '';
    });
  }

  salvaProfilo(): void {
    this.personaleService.aggiornaMioProfilo(this.telefono, this.indirizzo).subscribe({
      next: profilo => {
        this.profilo = profilo;
        this.successoProfilo = true;
        this.messaggioProfilo = 'Dati aggiornati correttamente.';
      },
      error: () => {
        this.successoProfilo = false;
        this.messaggioProfilo = 'Aggiornamento non riuscito.';
      }
    });
  }

  cambiaPassword(): void {
    this.messaggioPassword = '';

    if (this.nuovaPassword !== this.confermaPassword) {
      this.successoPassword = false;
      this.messaggioPassword = 'Le password non coincidono.';
      return;
    }

    this.authService.cambiaPassword(this.passwordAttuale, this.nuovaPassword).subscribe({
      next: () => {
        this.successoPassword = true;
        this.messaggioPassword = 'Password aggiornata correttamente.';
        this.passwordAttuale = '';
        this.nuovaPassword = '';
        this.confermaPassword = '';
      },
      error: err => {
        this.successoPassword = false;
        this.messaggioPassword = err.error?.errore || 'Impossibile aggiornare la password.';
      }
    });
  }
}