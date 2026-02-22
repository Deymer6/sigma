import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  
  {
    path: '', 
    loadChildren: () => 
        import('./features/public/public.routes').then(m => m.publicRoutes)
  },
  {
    path: 'patient',
    loadChildren: () => 
        import('./features/patient/patient.routes').then(m => m.patientRoutes),
    canActivate: [
      authGuard, 
      roleGuard(['ROL_PACIENTE'])
    ]
  },
  
{
    path: 'doctor',
    loadChildren: () => import('./features/doctor/pages/doctor.routes').then(m => m.doctorRoutes),
    canActivate: [
      authGuard, 
      roleGuard(['ROL_OBSTETRA'])
    ]
},
{
    path: 'admin',
    loadChildren: () => import('./features/admin/pages/admin.routes').then(m => m.adminRoutes),
    canActivate: [
      authGuard, 
      roleGuard(['ROL_ADMINISTRATIVO'])
    ]
},
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full'
  }
];  