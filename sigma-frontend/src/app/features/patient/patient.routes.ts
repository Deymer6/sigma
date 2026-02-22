import { Routes } from '@angular/router';

import { SecureLayout } from '../../shared/layouts/secure-layout/secure-layout'; 

export const patientRoutes: Routes = [
  {
    path: '',
    component: SecureLayout, 
    children: [
      
      {
        path: 'dashboard', 
        loadComponent: () => 
            import('./pages/dashboard-paciente/dashboard-paciente.component').then(m => m.DashboardPacienteComponent)
      },
      {
        path: 'mis-citas', 
        loadComponent: () => 
            import('./pages/mis-citas/mis-citas.component').then(m => m.MisCitasComponent)
      },
      {
      path: 'historial',
      loadComponent: () => 
        import('./pages/historial/historial').then(m => m.HistorialComponent)
      },
      {
      path: 'mis-resultados',
      loadComponent: () => 
        import('./pages/mis-resultados/mis-resultados').then(m => m.MisResultados)
      },
      {
        path: '', 
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  }
];