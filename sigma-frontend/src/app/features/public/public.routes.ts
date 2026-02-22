import { Routes } from '@angular/router';

import { PublicLayout } from '../../shared/layouts/public-layout/public-layout';

export const publicRoutes: Routes = [
  {
    
    path: '',
    component: PublicLayout, 
    children: [
      
      { 
        path: '', 
        
        loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent) 
      },
      { 
        path: 'login', 
        
        loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) 
      },
      { 
        path: 'register', 
        
        loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent) 
      },
    ]
  }
];