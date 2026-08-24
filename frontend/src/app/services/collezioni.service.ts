import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Collezione } from '../models/collezione.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CollezioniService {
  private apiUrl = `${environment.apiBaseUrl}/api/collezioni`;

  constructor(private http: HttpClient) {}

  getCollezioni(): Observable<Collezione[]> {
    return this.http.get<Collezione[]>(this.apiUrl);
  }
}