import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { CatalogoComponent } from './components/catalogo/catalogo.component';
import { EventiComponent } from './components/eventi/eventi.component';
import { AuthComponent } from './components/auth/auth.component';
import { FeedbackComponent } from './components/feedback/feedback.component';
import { GestioneOpereComponent } from './components/gestione-opere/gestione-opere.component';
import { GestioneEventiComponent } from './components/gestione-eventi/gestione-eventi.component';
import { GestionePersonaleComponent } from './components/gestione-personale/gestione-personale.component';
import { AdminLayoutComponent } from './components/admin-layout/admin-layout.component';
import { ruoloGuard } from './guards/ruolo.guard';
import { ImpostazioniComponent } from './components/impostazioni/impostazioni.component';
import { AreaPersonaleComponent } from './components/area-personale/area-personale.component';
import { GestioneFerieComponent } from './components/gestione-ferie/gestione-ferie.component';
import { GestioneTurniComponent } from './components/gestione-turni/gestione-turni.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'catalogo', component: CatalogoComponent },
  { path: 'eventi', component: EventiComponent },
  { path: 'feedback', component: FeedbackComponent, canActivate: [ruoloGuard(['VISITATORE', 'OPERATORE', 'HR'])] },
  { path: 'login', component: AuthComponent, data: { modalitaIniziale: 'login' } },
  { path: 'registrati', component: AuthComponent, data: { modalitaIniziale: 'registrazione' } },

  // Rimangono qui per l'Operatore, raggiungibili dalla navbar del sito pubblico
  { path: 'gestione-opere', component: GestioneOpereComponent, canActivate: [ruoloGuard(['OPERATORE', 'HR'])] },
  { path: 'gestione-eventi', component: GestioneEventiComponent, canActivate: [ruoloGuard(['OPERATORE', 'HR'])] },
  { path: 'area-personale', component: AreaPersonaleComponent, canActivate: [ruoloGuard(['OPERATORE', 'HR'])] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [ruoloGuard(['OPERATORE', 'HR'])] },

  // Pannello amministrativo separato, riservato a HR
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [ruoloGuard(['HR'])],
    children: [
      { path: '', component: DashboardComponent },
      { path: 'personale', component: GestionePersonaleComponent },
      { path: 'opere', component: GestioneOpereComponent },
      { path: 'eventi', component: GestioneEventiComponent },
      { path: 'impostazioni', component: ImpostazioniComponent },
      { path: 'ferie', component: GestioneFerieComponent },
      { path: 'turni', component: GestioneTurniComponent },
    ]
  },
];