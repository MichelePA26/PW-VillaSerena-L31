import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notifica } from '../models/notifica.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class NotificaService {
  private apiUrl = `${environment.apiBaseUrl}/api/notifiche`;

  constructor(private http: HttpClient) {}

  getMie(): Observable<Notifica[]> {
    return this.http.get<Notifica[]>(`${this.apiUrl}/mie`);
  }

  segnaComeLetta(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/letta`, {});
  }
}