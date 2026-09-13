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
  getMie(): Observable<Prenotazione[]> {
    return this.http.get<Prenotazione[]>(`${this.apiUrl}/mie`);
  }

  accettaNuovaData(id: number): Observable<Prenotazione> {
    return this.http.put<Prenotazione>(`${this.apiUrl}/${id}/accetta-nuova-data`, {});
  }

  richiediRimborso(id: number): Observable<Prenotazione> {
    return this.http.put<Prenotazione>(`${this.apiUrl}/${id}/richiedi-rimborso`, {});
  }

  annullaNonPagata(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/annulla`, {});
  }

  getTutte(eventoId?: number): Observable<Prenotazione[]> {
    const url = eventoId ? `${this.apiUrl}?eventoId=${eventoId}` : this.apiUrl;
    return this.http.get<Prenotazione[]>(url);
  }

  cercaPerCodice(codice: string): Observable<Prenotazione> {
    return this.http.get<Prenotazione>(`${this.apiUrl}/cerca-biglietto/${codice}`);
  }

  effettuaCheckIn(codice: string): Observable<Prenotazione> {
    return this.http.put<Prenotazione>(`${this.apiUrl}/check-in/${codice}`, {});
  }
}