import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

interface AuthResponse {
  token: string;
  ruolo: string;
}

interface DatiRegistrazione {
  nome: string;
  cognome: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiBaseUrl}/api/auth`;

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap(res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('ruolo', res.ruolo);
      })
    );
  }

  register(dati: DatiRegistrazione, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/register?password=${encodeURIComponent(password)}`, dati);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('ruolo');
  }

  getRuolo(): string | null {
    return localStorage.getItem('ruolo');
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
}
