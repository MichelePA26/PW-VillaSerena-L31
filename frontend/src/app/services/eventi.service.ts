import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Evento } from '../models/evento.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EventiService {
  private apiUrl = `${environment.apiBaseUrl}/api/eventi`;

  constructor(private http: HttpClient) {}

  getEventi(): Observable<Evento[]> {
    return this.http.get<Evento[]>(this.apiUrl);
  }

  creaEvento(evento: Evento): Observable<Evento> {
    return this.http.post<Evento>(this.apiUrl, evento);
  }

  aggiornaEvento(id: number, evento: Evento): Observable<Evento> {
    return this.http.put<Evento>(`${this.apiUrl}/${id}`, evento);
  }

  eliminaEvento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}