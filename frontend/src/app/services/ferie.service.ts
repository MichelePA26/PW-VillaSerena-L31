import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RichiestaFerie, StatoRichiesta } from '../models/richiesta-ferie.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FerieService {
  private apiUrl = `${environment.apiBaseUrl}/api/ferie`;

  constructor(private http: HttpClient) {}

  crea(richiesta: RichiestaFerie): Observable<RichiestaFerie> {
    return this.http.post<RichiestaFerie>(this.apiUrl, richiesta);
  }

  getMie(): Observable<RichiestaFerie[]> {
    return this.http.get<RichiestaFerie[]>(`${this.apiUrl}/mie`);
  }

  getTutte(): Observable<RichiestaFerie[]> {
    return this.http.get<RichiestaFerie[]>(this.apiUrl);
  }

  aggiornaStato(id: number, stato: StatoRichiesta): Observable<RichiestaFerie> {
    return this.http.put<RichiestaFerie>(`${this.apiUrl}/${id}/stato?stato=${stato}`, {});
  }
}