import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PagamentoService {
  private apiUrl = `${environment.apiBaseUrl}/api/pagamenti`;

  constructor(private http: HttpClient) {}

  creaOrdine(prenotazioneId: number): Observable<{ id: string }> {
    return this.http.post<{ id: string }>(`${this.apiUrl}/crea-ordine`, { prenotazioneId });
  }

  catturaOrdine(orderId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${orderId}/cattura`, {});
  }
}