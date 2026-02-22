# FAQ.md - Preguntas Frecuentes

## Instalación y Setup

### P: ¿Qué versión de Node.js necesito?
**R:** Node.js 20.x o superior. Verifica con `node --version`. Descarga desde https://nodejs.org/

### P: ¿Cuál es la diferencia entre npm install y npm ci?
**R:**
- `npm install` - Actualiza dependencias a versiones compatibles
- `npm ci` - Instala exactamente las versiones en package-lock.json (mejor para CI/CD)

### P: ¿Por qué fallaNpm install?
**R:** Intenta:
```bash
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

### P: ¿Dónde configuro la URL del backend?
**R:** En `src/environments/environment.ts`
```typescript
export const environment = {
  apiUrl: 'http://localhost:8080/api'
};
```

### P: ¿El backend debe estar corriendo antes de iniciar el frontend?
**R:** No es necesario para desarrollo inicial, pero sí para probar integraciones HTTP.

---

## Desarrollo

### P: ¿Cómo ejecuto el servidor de desarrollo?
**R:**
```bash
npm start
# o
ng serve
```
Accede a `http://localhost:4200/`

### P: ¿Cómo genero un nuevo componente?
**R:**
```bash
ng generate component features/patient/pages/mi-componente
# o abreviado
ng g c features/patient/pages/mi-componente
```

### P: ¿Cómo genero un servicio?
**R:**
```bash
ng generate service core/services/mi-servicio
# o
ng g s core/services/mi-servicio
```

### P: ¿Cómo uso un servicio en un componente?
**R:**
```typescript
constructor(private miServicio: MiService) {}

ngOnInit(): void {
  this.miServicio.obtenerDatos().subscribe(datos => {
    console.log(datos);
  });
}
```

### P: ¿Cómo hago unsubscribe correctamente?
**R:**
```typescript
private destroy$ = new Subject<void>();

ngOnInit(): void {
  this.servicio.datos$
    .pipe(takeUntil(this.destroy$))
    .subscribe(...);
}

ngOnDestroy(): void {
  this.destroy$.next();
  this.destroy$.complete();
}
```

### P: ¿Cómo uso el async pipe?
**R:**
```html
<!-- Componente -->
citas$ = this.citaService.getCitas();

<!-- Template -->
<div *ngFor="let cita of citas$ | async">
  {{ cita.fecha | date }}
</div>
```

### P: ¿Cómo accedo a los parámetros de ruta?
**R:**
```typescript
constructor(private route: ActivatedRoute) {}

ngOnInit(): void {
  this.route.params.subscribe(params => {
    const id = params['id'];
  });
}
```

### P: ¿Cómo hago que un formulario sea reactivo?
**R:**
```typescript
import { FormBuilder, Validators } from '@angular/forms';

form = this.fb.group({
  email: ['', [Validators.required, Validators.email]],
  password: ['', Validators.required]
});

constructor(private fb: FormBuilder) {}
```

---

## Testing

### P: ¿Cómo ejecuto los tests?
**R:**
```bash
npm test

# Con coverage
npm test -- --code-coverage

# Un archivo específico
ng test --include='**/mi-componente/**'
```

### P: ¿Cuál debe ser el coverage mínimo?
**R:** El proyecto requiere mínimo **80% de coverage**.

### P: ¿Cómo testeen servicios HTTP?
**R:**
```typescript
it('should get citas', () => {
  const mockCitas = [{ id: 1, fecha: new Date() }];

  service.getCitas().subscribe(citas => {
    expect(citas.length).toBe(1);
  });

  const req = httpMock.expectOne('/api/citas');
  req.flush(mockCitas);
});
```

### P: ¿Qué es TestBed?
**R:** Es la herramienta de Angular para testear componentes, servicios e inyecciones.

---

## Autenticación

### P: ¿Dónde se almacena el JWT?
**R:** En `localStorage` bajo la clave configurada en `environment.ts` (por defecto `auth_token`).

### P: ¿Qué pasa si el token expira?
**R:** El `AuthInterceptor` intenta refrescar el token. Si falla, redirige a login.

### P: ¿Cómo verifico si un usuario está autenticado?
**R:**
```typescript
constructor(private authService: AuthService) {}

if (this.authService.isAuthenticated()) {
  // Usuario autenticado
}
```

### P: ¿Cómo protejo una ruta?
**R:**
```typescript
{
  path: 'admin',
  component: AdminComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMINISTRADOR'] }
}
```

### P: ¿Cómo hago logout?
**R:**
```typescript
this.authService.logout();
// Limpia token y redirige a login
```

---

## Deployment

### P: ¿Cómo preparo el proyecto para producción?
**R:**
```bash
npm run build

# Los archivos compilados están en dist/sigma-frontend/browser/
```

### P: ¿Cuál es el tamaño final después de build?
**R:** Típicamente 300-500 KB (gzipped). Varía según features.

### P: ¿Cómo sirvo la aplicación en producción?
**R:**
```bash
# Opción 1: Nginx (recomendado)
# Copiar dist/sigma-frontend/browser/* a /var/www/

# Opción 2: Node.js
npm install -g serve
serve -s dist/sigma-frontend/browser -l 3000

# Opción 3: Docker
docker build -t sigma-frontend:1.0 .
docker run -p 8000:3000 sigma-frontend:1.0
```

### P: ¿Cómo configuro variables de entorno en producción?
**R:**
1. Crear `environment.prod.ts` con URLs de producción
2. Usar durante build: `npm run build -- --configuration production`

### P: ¿Qué es CORS y por qué afecta mi app?
**R:** CORS (Cross-Origin Resource Sharing) es un mecanismo de seguridad. El backend debe permitir requests desde tu dominio:
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://sigma.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

---

## Troubleshooting

### P: "Port 4200 is already in use"
**R:**
```bash
# Cambiar puerto
ng serve --port 4201

# O matar proceso en ese puerto
# Windows: netstat -ano | findstr :4200
# Linux: lsof -i :4200
```

### P: "Cannot find module '@angular/..."
**R:**
```bash
npm install
# Si persiste:
rm -rf node_modules package-lock.json
npm install
```

### P: "Build fails with memory error"
**R:**
```bash
export NODE_OPTIONS=--max-old-space-size=4096
npm run build
```

### P: "404 on page refresh"
**R:** Es un problema de SPA. Soluciones:
- En `nginx`: Usar `try_files $uri $uri/ /index.html;`
- En `apache`: Usar `.htaccess`
- Ver [INSTALACION_DEPLOYMENT.md](INSTALACION_DEPLOYMENT.md#problema-404-on-refresh)

### P: "CORS error cuando hablo con backend"
**R:**
- Verifica que backend tiene CORS habilitado
- Asegúrate que URL del backend es correcta
- Ver [INSTALACION_DEPLOYMENT.md](INSTALACION_DEPLOYMENT.md#problema-cors-error)

### P: "Los cambios no se reflejan al guardar"
**R:**
```bash
# Detener servidor (Ctrl+C)
# Limpiar caché del navegador (Ctrl+Shift+Delete)
# Reiniciar servidor: npm start
```

### P: "Token expira muy rápido"
**R:** Ajusta la expiración en `environment.ts`:
```typescript
tokenExpiration: 3600000 // milisegundos (1 hora)
```

---

## Performance y Optimización

### P: ¿Cómo veo qué es grande en mi bundle?
**R:**
```bash
npm run build -- --stats-json
npm install webpack-bundle-analyzer
npx webpack-bundle-analyzer dist/sigma-frontend/browser/stats.json
```

### P: ¿Cómo optimizo el bundle?
**R:**
1. Usar lazy loading de módulos
2. Usar OnPush change detection
3. Usar async pipe en templates
4. Unsubscribe de Observables

### P: ¿Qué es lazy loading?
**R:**
```typescript
// Cargar componente solo cuando se accede a la ruta
{
  path: 'admin',
  loadComponent: () => import('./features/admin/admin')
    .then(m => m.AdminComponent)
}
```

---

## Git y Versionado

### P: ¿Cómo creo una rama para mi feature?
**R:**
```bash
git checkout -b feature/mi-feature
git push -u origin feature/mi-feature
```

### P: ¿Cómo hago un Pull Request?
**R:** Ver [CONTRIBUCION.md](CONTRIBUCION.md#pull-requests)

### P: ¿Cómo resuelvo conflictos de merge?
**R:** Ver [GUIA_GIT.md](GUIA_GIT.md#resolución-de-conflictos)

### P: ¿Cómo reverto un cambio?
**R:**
```bash
# Revertir último commit (mantener cambios)
git reset --soft HEAD~1

# Revertir sin cambios
git reset --hard HEAD~1

# Revertir commit específico
git revert <hash>
```

---

## Seguridad

### P: ¿Es seguro guardar el token en localStorage?
**R:** Sí, para SPAs es la opción recomendada si tienes HTTPS. Para máxima seguridad, usa HttpOnly cookies (requiere soporte del backend).

### P: ¿Cómo evito XSS (Cross-Site Scripting)?
**R:** Angular sanitiza automáticamente. Evita `bypassSecurityTrustHtml` a menos que sea necesario.

### P: ¿Cómo aseguro mis variables de entorno?
**R:**
- NUNCA commitear claves secretas
- NUNCA poner URLs reales en código
- Usar variables de entorno en build time

---

## Dependencias y Librerías

### P: ¿Cómo agrego una nueva librería?
**R:**
```bash
npm install nombre-libreria
npm install --save-dev nombre-libreria-dev
```

### P: ¿Cómo actualizo dependencias?
**R:**
```bash
# Ver versiones disponibles
npm outdated

# Actualizar todas
npm update

# Actualizar a latest (⚠️ puede romper cosas)
npm install -g npm-check-updates
ncu -u
npm install
```

### P: ¿Qué librerías principales usa el proyecto?
**R:** Ver `package.json` o [README.md](README.md#dependencias-principales)

---

## Contribución

### P: ¿Cómo contribuyo al proyecto?
**R:** Ver [CONTRIBUCION.md](CONTRIBUCION.md)

### P: ¿Hay convenciones de código?
**R:** Sí, ver [CONTRIBUCION.md#convenciones-de-código](CONTRIBUCION.md#convenciones-de-código)

### P: ¿Cómo reporto un bug?
**R:** Abre un issue en GitHub con información detallada.

---

## General

### P: ¿Dónde encuentro más información?
**R:**
- [README.md](README.md) - Descripción general
- [DOCUMENTACION_TECNICA.md](DOCUMENTACION_TECNICA.md) - Arquitectura
- [INSTALACION_DEPLOYMENT.md](INSTALACION_DEPLOYMENT.md) - Instalación y deployment
- [CONTRIBUCION.md](CONTRIBUCION.md) - Cómo contribuir
- [HELP.md](HELP.md) - Ayuda rápida

### P: ¿Hay un canal para hacer preguntas?
**R:** Sí, puedes:
- Abrir una discussion en GitHub
- Contactar al equipo de desarrollo
- Revisar issues existentes

### P: ¿Cuál es la versión actual del proyecto?
**R:** Versión 1.0.0 (22 de febrero de 2026)

### P: ¿Hay un roadmap?
**R:** Ver [PROYECTO.md](PROYECTO.md#próximos-pasos)

---

**¿No encuentras tu pregunta?** Abre un issue en GitHub o contacta al equipo.

Versión: 1.0.0 | Fecha: 22 de febrero de 2026
