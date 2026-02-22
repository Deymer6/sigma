import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';
import { jwtDecode } from 'jwt-decode'; // <-- 1. Importa el decodificador

// 2. Define la estructura de tu token (lo que pusimos en Spring Boot)
interface JwtPayload {
  sub: string;
  authorities: string[]; // <-- Spring Security lo llama 'authorities'
  iat: number;
  exp: number;
}

// 3. El guardia ahora 'recibe' un array de roles permitidos
export const roleGuard: (allowedRoles: string[]) => CanActivateFn = 
  (allowedRoles: string[]) => {
    return (route, state) => {
      
      const authService = inject(AuthService);
      const router = inject(Router);

      // 4. Obtener el token
      const token = authService.getToken();
      if (!token) {
        router.navigate(['/login']); // No hay token, fuera
        return false;
      }

      try {
        // 5. Decodificar el token
        const payload = jwtDecode<JwtPayload>(token);

        // Validar sesión antes de verificar roles


        // 6. Verificar si el rol del usuario está en la lista de roles permitidos
        const userRole = payload.authorities[0]; // Asumimos que solo tiene un rol
        
        if (allowedRoles.includes(userRole)) {
          return true; // <-- ¡Sí tiene el rol, déjalo pasar!
        }

        // 7. Tiene token, pero no el rol. Redirige al 'home' (o un 'no autorizado')
        router.navigate(['/']); // Redirige al home
        return false;
      
      } catch (error) {
        // El token es inválido o expiró
        authService.logout();
        router.navigate(['/login']);
        return false;
      }
    };
  };