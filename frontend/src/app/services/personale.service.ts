import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AssunzioneRequest, Dipendente } from '../models/dipendente.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PersonaleService {
  private apiUrl = `${environment.apiBaseUrl}/api/personale`;

  constructor(private http: HttpClient) {}

  getDipendenti(): Observable<Dipendente[]> {
    return this.http.get<Dipendente[]>(this.apiUrl);
  }

  assumi(request: AssunzioneRequest): Observable<Dipendente> {
    return this.http.post<Dipendente>(this.apiUrl, request);
  }

  cessa(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getMioProfilo(): Observable<Dipendente> {
    return this.http.get<Dipendente>(`${this.apiUrl}/me`);
  }

  aggiornaMioProfilo(telefono: string, indirizzo: string): Observable<Dipendente> {
    return this.http.put<Dipendente>(`${this.apiUrl}/me`, { telefono, indirizzo });
  }

  aggiorna(id: number, request: AssunzioneRequest): Observable<Dipendente> {
    return this.http.put<Dipendente>(`${this.apiUrl}/${id}`, request);
  }

  resetPassword(id: number, nuovaPassword: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/reset-password`, { nuovaPassword });
  }
}