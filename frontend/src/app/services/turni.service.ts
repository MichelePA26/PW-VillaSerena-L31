import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Turno } from '../models/turno.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TurniService {
  private apiUrl = `${environment.apiBaseUrl}/api/turni`;

  constructor(private http: HttpClient) {}

  getTutti(): Observable<Turno[]> {
    return this.http.get<Turno[]>(this.apiUrl);
  }

  getPerDipendente(dipendenteId: number): Observable<Turno[]> {
    return this.http.get<Turno[]>(`${this.apiUrl}/dipendente/${dipendenteId}`);
  }

  crea(turno: Turno): Observable<Turno> {
    return this.http.post<Turno>(this.apiUrl, turno);
  }

  aggiorna(id: number, turno: Turno): Observable<Turno> {
    return this.http.put<Turno>(`${this.apiUrl}/${id}`, turno);
  }

  elimina(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}