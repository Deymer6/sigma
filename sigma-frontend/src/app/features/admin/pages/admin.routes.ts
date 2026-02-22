import { Routes } from '@angular/router';
import { SecureLayout } from '../../../shared/layouts/secure-layout/secure-layout';

export const adminRoutes: Routes = [
  {
    path: '',
    component: SecureLayout, 
    children: [
      {
        path: '',
        redirectTo: 'dashboard', 
        pathMatch: 'full'
      },
      
      
      {
        path: 'dashboard', 
        loadComponent: () => 
          import('./dashboard-admin/dashboard-admin').then(m => m.DashboardAdmin)
      },

      
      {
        path: 'gestion-obstetras', 
        loadComponent: () => 
          import('./gestion-obstetras/gestion-obstetras').then(m => m.GestionObstetras)
      },
      
      {
        path: 'gestion-pacientes', 
        loadComponent: () => 
          import('./gestion-pacientes/gestion-pacientes').then(m => m.GestionPacientes)
      }
    ]
  }
];