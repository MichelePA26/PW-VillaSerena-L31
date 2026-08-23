import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export function ruoloGuard(ruoliAmmessi: string[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (!auth.isAuthenticated()) {
      router.navigate(['/login']);
      return false;
    }
    const ruolo = auth.getRuolo();
    if (ruolo && ruoliAmmessi.includes(ruolo)) {
      return true;
    }
    router.navigate(['/']);
    return false;
  };
}