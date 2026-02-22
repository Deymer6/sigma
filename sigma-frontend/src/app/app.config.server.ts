import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { provideServerRendering, withRoutes } from '@angular/ssr';
import { appConfig } from './app.config';

// Asumimos que tienes este archivo de rutas del servidor
import { serverRoutes } from './app.routes.server'; 

// 1. Importa el proveedor que falta
import { provideHttpClient } from '@angular/common/http';

const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(withRoutes(serverRoutes)),
    
    // 2. ¡LA SOLUCIÓN! Añade esto
    provideHttpClient()
  ]
};

export const config = mergeApplicationConfig(appConfig, serverConfig);