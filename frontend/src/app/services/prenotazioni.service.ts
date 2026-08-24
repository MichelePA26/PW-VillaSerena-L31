import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Prenotazione } from '../models/prenotazione.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PrenotazioniService {
  private apiUrl = `${environment.apiBaseUrl}/api/prenotazioni`;

  constructor(private http: HttpClient) {}

  // L'utente è ricavato dal token lato server: qui basta passare l'evento e i posti
  prenota(eventoId: number, numeroPosti: number): Observable<Prenotazione> {
    return this.http.post<Prenotazione>(this.apiUrl, { eventoId, numeroPosti });
  }

  getMiePrenotazioni(): Observable<Prenotazione[]> {
    return this.http.get<Prenotazione[]>(`${this.apiUrl}/mie`);
  }
}