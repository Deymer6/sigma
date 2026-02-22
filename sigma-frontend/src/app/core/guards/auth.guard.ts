import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

// Exportamos la función 'authGuard' (en minúscula)
export const authGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  
  if (authService.isLoggedIn()) {
    return true; // Sí está logueado, déjalo pasar
  }

  // No está logueado. Redirige a la página de login
  router.navigate(['/login']);
  return false; // No lo dejes pasar
};