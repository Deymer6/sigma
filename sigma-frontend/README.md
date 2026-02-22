# Sigma Frontend - Sistema de Gestión Clínica Obstétrica

## Descripción General

**Sigma Frontend** es la aplicación web responsiva para gestión clínica especializada en servicios obstétricos, desarrollada con **Angular 20.2.0** y **TypeScript**. El sistema proporciona interfaces intuitivas para pacientes, obstetras y administradores con autenticación JWT y control de roles.

## Tecnologías Principales

- **Framework:** Angular 20.2.0 (Standalone Components)
- **Lenguaje:** TypeScript 5.5+
- **Estilos:** SCSS con Bootstrap Icons
- **Autenticación:** JWT (JSON Web Tokens)
- **HTTP:** HttpClient con Interceptores
- **Enrutamiento:** Angular Router con Guards
- **Rendering:** SSR (Server-Side Rendering)
- **Build Tool:** Angular CLI 20.2.2
- **Librerías UI:** FontAwesome, SweetAlert2

### Dependencias Principales

```json
{
  "@angular/core": "^20.2.0",
  "@angular/common": "^20.2.0",
  "@angular/forms": "^20.2.0",
  "@angular/router": "^20.2.0",
  "@angular/ssr": "^20.2.2",
  "@angular/platform-browser": "^20.2.0",
  "@fortawesome/angular-fontawesome": "^3.0.0",
  "jwt-decode": "^4.0.0",
  "rxjs": "^7.8.0",
  "sweetalert2": "^11.26.3"
}
```

## Estructura del Proyecto

```
sigma-frontend/
├── src/
│   ├── main.ts                           # Punto de entrada principal
│   ├── main.server.ts                    # Punto de entrada SSR
│   ├── server.ts                         # Configuración del servidor Express
│   ├── styles.scss                       # Estilos globales
│   ├── index.html                        # HTML principal
│   ├── app/
│   │   ├── app.ts                        # Componente raíz
│   │   ├── app.routes.ts                 # Rutas principales
│   │   ├── app.routes.server.ts          # Rutas para SSR
│   │   ├── app.config.ts                 # Configuración de la app
│   │   ├── app.config.server.ts          # Configuración SSR
│   │   ├── core/                         # Servicios, guards, interceptores
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts         # Guard de autenticación
│   │   │   │   └── role.guard.ts         # Guard de roles
│   │   │   ├── interceptors/
│   │   │   │   └── auth.interceptor.ts   # Interceptor JWT
│   │   │   ├── interfaces/               # Interfaces TypeScript
│   │   │   │   ├── analisis.interface.ts
│   │   │   │   ├── dashboard.interface.ts
│   │   │   │   ├── historial.interface.ts
│   │   │   │   └── obstetra.interface.ts
│   │   │   └── services/                 # Servicios
│   │   │       ├── auth.ts               # Servicio de autenticación
│   │   │       ├── admin.service.ts      # Servicios administrativos
│   │   │       ├── analisis.service.ts   # Servicios de análisis
│   │   │       ├── cita.service.ts       # Servicios de citas
│   │   │       ├── historial.service.ts  # Servicios de historiales
│   │   │       ├── obstetra.service.ts   # Servicios de obstetras
│   │   │       ├── patient.service.ts    # Servicios de pacientes
│   │   │       └── staff.service.ts      # Servicios de personal
│   │   ├── features/                     # Módulos de características
│   │   │   ├── admin/                    # Panel administrativo
│   │   │   │   └── pages/
│   │   │   ├── doctor/                   # Panel de obstetras
│   │   │   │   └── pages/
│   │   │   ├── patient/                  # Portal de pacientes
│   │   │   │   ├── patient.routes.ts
│   │   │   │   └── pages/
│   │   │   └── public/                   # Páginas públicas
│   │   │       ├── public.routes.ts
│   │   │       └── pages/
│   │   ├── shared/                       # Componentes y utilidades compartidas
│   │   │   ├── components/               # Componentes reutilizables
│   │   │   ├── layouts/                  # Layouts compartidos
│   │   │   └── styles/                   # Variables SCSS compartidas
│   │   └── environments/
│   │       └── environment.ts            # Configuración del entorno
│   └── environments/
│       └── environment.ts                # Variables de entorno
├── angular.json                          # Configuración de Angular CLI
├── package.json                          # Dependencias del proyecto
├── tsconfig.json                         # Configuración de TypeScript
├── tsconfig.app.json                     # TypeScript para aplicación
├── tsconfig.spec.json                    # TypeScript para tests
└── README.md                             # Este archivo
```

## Instalación Rápida

### Requisitos Previos

- **Node.js:** v20.x o superior
- **npm:** v10.x o superior
- **Angular CLI:** 20.2.2
- **Backend Sigma:** ejecutándose en `http://localhost:8080`

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/deymer6/sigma.git
   cd sigma/sigma-frontend
   ```

2. **Instalar dependencias**
   ```bash
   npm install
   ```

3. **Configurar variables de entorno**
   ```bash
   cp src/environments/environment.example.ts src/environments/environment.ts
   ```
   Edita `environment.ts` con la URL de tu backend:
   ```typescript
   export const environment = {
     production: false,
     apiUrl: 'http://localhost:8080/api'
   };
   ```

4. **Ejecutar servidor de desarrollo**
   ```bash
   npm start
   ```
   Accede a `http://localhost:4200/`

## Configuración Detallada

### Variables de Entorno

Archivo: `src/environments/environment.ts`

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  jwtTokenName: 'auth_token',
  jwtRefreshTokenName: 'refresh_token'
};
```

### Autenticación JWT

El sistema usa JWT para autenticación:

1. **AuthService** (`core/services/auth.ts`):
   - Login/Logout
   - Gestión de tokens
   - Refresh de tokens
   - Obtención del usuario actual

2. **AuthInterceptor** (`core/interceptors/auth.interceptor.ts`):
   - Agrega token JWT a todas las peticiones
   - Maneja errores de autenticación (401)
   - Refresca tokens cuando expiran

3. **AuthGuard** (`core/guards/auth.guard.ts`):
   - Protege rutas de usuarios no autenticados
   - Redirige a login si no hay sesión

### Sistema de Roles

El sistema implementa control de acceso basado en roles:

**Roles disponibles:**
- **PACIENTE** - Acceso al portal de pacientes
- **OBSTETRA** - Acceso al panel de obstetras
- **ADMINISTRADOR** - Acceso al panel administrativo

**RoleGuard** (`core/guards/role.guard.ts`):
```typescript
// Protege rutas por rol específico
canActivate(route) {
  const requiredRoles = route.data['roles'];
  return authService.hasRole(requiredRoles);
}
```

**Uso en rutas:**
```typescript
{
  path: 'admin',
  component: AdminComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMINISTRADOR'] }
}
```

## Módulos Principales

### 1. **Public** - Módulo Público
Páginas sin autenticación requerida:
- Home
- Login
- Registro
- Información general

### 2. **Patient** - Portal de Pacientes
Acceso para pacientes registrados:
- Mis citas
- Agenda citas
- Mis análisis clínicos
- Mi historial médico
- Mi perfil

### 3. **Doctor** - Panel de Obstetras
Acceso para obstetras:
- Citas programadas
- Gestión de pacientes
- Análisis clínicos
- Consultar historiales

### 4. **Admin** - Panel Administrativo
Acceso para administradores:
- Gestión de usuarios
- Auditoría del sistema
- Reportes
- Configuración del sistema

## Servicios Principales

### AuthService (core/services/auth.ts)
```typescript
- login(email, password): Observable
- logout(): void
- isAuthenticated(): boolean
- getCurrentUser(): User
- hasRole(roles): boolean
- getToken(): string
```

### CatalogService (core/services/cita.service.ts)
```typescript
- getCitas(): Observable<Cita[]>
- getCitaById(id): Observable<Cita>
- createCita(cita): Observable<Cita>
- updateCita(id, cita): Observable<Cita>
- cancelCita(id): Observable
```

### HistorialService (core/services/historial.service.ts)
```typescript
- getHistorialByPaciente(id): Observable<Historial>
- createHistorial(historial): Observable<Historial>
- updateHistorial(id, historial): Observable<Historial>
```

### AnalisisService (core/services/analisis.service.ts)
```typescript
- getAnalisisByPaciente(id): Observable<Analisis[]>
- createAnalisis(analisis): Observable<Analisis>
- getAnalisisById(id): Observable<Analisis>
```

## Scripts NPM Disponibles

```bash
# Desarrollo
npm start                    # Inicia servidor en localhost:4200

# Build
npm run build               # Construye para producción

# Testing
npm test                    # Ejecuta pruebas unitarias
npm run test:watch         # Ejecuta pruebas en modo watch

# Otros
npm run watch              # Build en watch mode
ng generate component name # Genera nuevo componente
ng generate service name   # Genera nuevo servicio
```

## Componentes Clave

### Layout Components
- `AppLayout` - Layout principal con navbar y sidebar
- `AuthLayout` - Layout para páginas de autenticación

### Shared Components
- `HeaderComponent` - Encabezado de página
- `SidebarComponent` - Barra lateral de navegación
- `FooterComponent` - Pie de página
- `CardComponent` - Componente de tarjeta reutilizable
- `TableComponent` - Tabla de datos reutilizable
- `PaginationComponent` - Paginación reutilizable

## Flujo de Autenticación

1. Usuario accede a `/login`
2. Ingresa email y contraseña
3. `AuthService` envía petición al backend
4. Backend valida credenciales y retorna JWT
5. Token se almacena en localStorage/sessionStorage
6. `AuthInterceptor` agrega token a todas las peticiones
7. Acceso a rutas protegidas mediante `AuthGuard`

## Manejo de Errores

El sistema implementa manejo centralizado de errores:

1. **HTTP Errors:** `auth.interceptor.ts` captura errores 401/403
2. **Application Errors:** Servicios manejan errores de negocio
3. **Toast Notifications:** SweetAlert2 para mensajes al usuario

## Deployment

**Para producción:**
```bash
npm run build
# Los archivos compilados están en: dist/sigma-frontend/

# Con SSR:
npm run serve:ssr:sigma-frontend
```

Ver [INSTALACION_DEPLOYMENT.md](INSTALACION_DEPLOYMENT.md) para instrucciones detalladas.

## Contratos API (Backend)

El frontend consume siguientes endpoints:

### Autenticación
```
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/refresh
GET /api/auth/me
```

### Pacientes
```
GET /api/pacientes
GET /api/pacientes/{id}
PUT /api/pacientes/{id}
GET /api/pacientes/{id}/citas
```

### Citas
```
GET /api/citas
POST /api/citas
GET /api/citas/{id}
PUT /api/citas/{id}
DELETE /api/citas/{id}
```

### Análisis
```
GET /api/analisis
POST /api/analisis
GET /api/analisis/{id}
GET /api/analisis/paciente/{id}
```

### Historiales
```
GET /api/historiales/{id}
POST /api/historiales
PUT /api/historiales/{id}
```

## Troubleshooting

### El servidor no inicia
```bash
# Limpiar cache de Node
rm -rf node_modules package-lock.json
npm install
npm start
```

### Errores de conexión al backend
- Verifica que el backend esté ejecutándose en puerto 8080
- Revisa la URL en `environment.ts`
- Comprueba CORS en el backend

### TokenExpiredError
- El token JWT ha expirado
- Se intenta hacer refresh automático
- Si falla, se redirige a login

## Contribuir

Ver [CONTRIBUCION.md](CONTRIBUCION.md) para guía de contribución.

## Licencia

Este proyecto es parte del SIGMA (Sistema Integral de Gestión Médica Avanzada).

## Contacto y Soporte

Para preguntas o soporte, abre un issue en el repositorio o contacta al equipo.

---

**Última actualización:** 22 de febrero de 2026  
**Versión:** 1.0.0
