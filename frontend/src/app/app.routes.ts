import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { CatalogoComponent } from './components/catalogo/catalogo.component';
import { EventiComponent } from './components/eventi/eventi.component';
import { AuthComponent } from './components/auth/auth.component';
import { GestioneOpereComponent } from './components/gestione-opere/gestione-opere.component';
import { ruoloGuard } from './guards/ruolo.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'catalogo', component: CatalogoComponent },
  { path: 'eventi', component: EventiComponent },
  { path: 'login', component: AuthComponent, data: { modalitaIniziale: 'login' } },
  { path: 'registrati', component: AuthComponent, data: { modalitaIniziale: 'registrazione' } },
  { path: 'gestione-opere', component: GestioneOpereComponent, canActivate: [ruoloGuard(['OPERATORE', 'HR'])] },
];