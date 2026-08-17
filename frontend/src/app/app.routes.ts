import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { AuthComponent } from './components/auth/auth.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: AuthComponent, data: { modalitaIniziale: 'login' } },
  { path: 'registrati', component: AuthComponent, data: { modalitaIniziale: 'registrazione' } },
];