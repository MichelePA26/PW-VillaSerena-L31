import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Notifica } from '../../models/notifica.model';
import { NotificaService } from '../../services/notifica.service';

@Component({
  selector: 'app-notifiche-dropdown',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifiche-dropdown.component.html',
  styleUrl: './notifiche-dropdown.component.css'
})
export class NotificheDropdownComponent implements OnInit {
  @Input() variante: 'chiaro' | 'scuro' = 'chiaro'; // per adattarsi a navbar chiara/sidebar scura

  notifiche: Notifica[] = [];
  aperta = false;

  constructor(private notificaService: NotificaService, private router: Router) {}

  ngOnInit(): void {
    this.carica();
  }

  carica(): void {
    this.notificaService.getMie().subscribe(n => this.notifiche = n);
  }

  toggle(): void {
    this.aperta = !this.aperta;
  }

  chiudi(): void {
    this.aperta = false;
  }

  vaiA(notifica: Notifica): void {
    this.notificaService.segnaComeLetta(notifica.id).subscribe(() => {
      this.notifiche = this.notifiche.filter(n => n.id !== notifica.id);
    });
    this.chiudi();
    if (notifica.link) {
      this.router.navigateByUrl(notifica.link);
    }
  }
}