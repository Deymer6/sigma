import { Routes } from '@angular/router';
import { SecureLayout } from '../../../shared/layouts/secure-layout/secure-layout'; 

export const doctorRoutes: Routes = [
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
        path: 'dashboard', // La Agenda del Día
        loadComponent: () => 
          import('./dashboard/dashboard').then(m => m.Dashboard)
      },
      {
        path: 'mis-pacientes', // Buscador de Pacientes
        loadComponent: () => 
          import('./mis-pacientes/mis-pacientes').then(m => m.MisPacientes)
      },
      
      { 
        path: 'paciente/:idPaciente/historial', 
        
        loadComponent: () => import('./historial-paciente/historial-paciente.component')
                                .then(m => m.HistorialPacienteComponent) 
      },
      {
        
        path: 'atencion/:idCita', 
        loadComponent: () => 
          import('./atencion-medica/atencion-medica').then(m => m.AtencionMedica)
      }
    ]
  }
];