import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Opera } from '../models/opera.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CatalogoService {
  private apiUrl = `${environment.apiBaseUrl}/api/opere`;

  constructor(private http: HttpClient) {}

  getOpere(): Observable<Opera[]> {
    return this.http.get<Opera[]>(this.apiUrl);
  }

  creaOpera(opera: Opera): Observable<Opera> {
    return this.http.post<Opera>(this.apiUrl, opera);
  }

  aggiornaOpera(id: number, opera: Opera): Observable<Opera> {
    return this.http.put<Opera>(`${this.apiUrl}/${id}`, opera);
  }

  eliminaOpera(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}