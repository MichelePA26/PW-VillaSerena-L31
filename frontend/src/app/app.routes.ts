import { Routes } from '@angular/router';
import { CatalogoComponent } from './components/catalogo/catalogo.component';
import { AuthComponent } from './components/auth/auth.component';
import { GestioneOpereComponent } from './components/gestione-opere/gestione-opere.component';
import { ruoloGuard } from './guards/ruolo.guard';
import { HomeComponent } from './components/home/home.component';


export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'catalogo', component: CatalogoComponent },
  { path: 'login', component: AuthComponent, data: { modalitaIniziale: 'login' } },
  { path: 'registrati', component: AuthComponent, data: { modalitaIniziale: 'registrazione' } },
  { path: 'gestione-opere', component: GestioneOpereComponent, canActivate: [ruoloGuard(['OPERATORE', 'HR'])] },
];