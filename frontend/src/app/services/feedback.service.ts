import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Feedback } from '../models/feedback.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FeedbackService {
  private apiUrl = `${environment.apiBaseUrl}/api/feedback`;

  constructor(private http: HttpClient) {}

  invia(prenotazioneId: number, voto: number, commento: string): Observable<Feedback> {
    return this.http.post<Feedback>(this.apiUrl, { prenotazioneId, voto, commento });
  }
  
  getPerEvento(eventoId: number): Observable<Feedback[]> {
  return this.http.get<Feedback[]>(`${this.apiUrl}/evento/${eventoId}`);
}
} 