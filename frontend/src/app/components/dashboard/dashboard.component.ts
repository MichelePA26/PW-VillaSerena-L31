import { Component, ElementRef, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Dashboard, FeedbackDashboard } from '../../models/dashboard.model';
import { DashboardService } from '../../services/dashboard.service';
import { AuthService } from '../../services/auth.service';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, AfterViewInit {
  @ViewChild('graficoPrenotazioni') graficoCanvas?: ElementRef<HTMLCanvasElement>;

  dashboard: Dashboard | null = null;
  caricamento = true;

  paginaFeedback = 0;
  readonly elementiPerPagina = 3;

  constructor(
    private dashboardService: DashboardService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.dashboardService.get().subscribe({
      next: d => {
        this.dashboard = d;
        this.caricamento = false;
        setTimeout(() => this.disegnaGrafico(), 0);
      },
      error: () => this.caricamento = false
    });
  }

  ngAfterViewInit(): void {}

  private disegnaGrafico(): void {
    if (!this.graficoCanvas || !this.dashboard) return;
    const dati = this.dashboard.prenotazioniPerEvento;

    new Chart(this.graficoCanvas.nativeElement, {
      type: 'bar',
      data: {
        labels: dati.map(d => d.titoloEvento),
        datasets: [{
          data: dati.map(d => d.numeroPrenotazioni),
          backgroundColor: '#c9a876',
          borderRadius: 4,
          maxBarThickness: 22
        }]
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          x: { grid: { color: '#f0ebe3' }, ticks: { color: '#a89a89' } },
          y: { grid: { display: false }, ticks: { color: '#6b6259' } }
        }
      }
    });
  }

  get feedbackPaginati(): FeedbackDashboard[] {
    if (!this.dashboard) return [];
    const inizio = this.paginaFeedback * this.elementiPerPagina;
    return this.dashboard.ultimiFeedback.slice(inizio, inizio + this.elementiPerPagina);
  }

  get totalePagineFeedback(): number {
    if (!this.dashboard) return 1;
    return Math.max(1, Math.ceil(this.dashboard.ultimiFeedback.length / this.elementiPerPagina));
  }

  paginaFeedbackPrecedente(): void {
    if (this.paginaFeedback > 0) this.paginaFeedback--;
  }

  paginaFeedbackSuccessiva(): void {
    if (this.paginaFeedback < this.totalePagineFeedback - 1) this.paginaFeedback++;
  }

  get isHR(): boolean {
    return this.auth.getRuolo() === 'HR';
  }
}