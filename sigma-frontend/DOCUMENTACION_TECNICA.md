# Documentación Técnica Detallada - Sigma Frontend

## Tabla de Contenidos

1. [Arquitectura de la Aplicación](#arquitectura-de-la-aplicación)
2. [Estructura de Componentes](#estructura-de-componentes)
3. [Servicios](#servicios)
4. [Guards y Interceptores](#guards-y-interceptores)
5. [Interfaces y Tipos](#interfaces-y-tipos)
6. [Enrutamiento](#enrutamiento)
7. [Gestión de Estado con RxJS](#gestión-de-estado-con-rxjs)
8. [Comunicación HTTP](#comunicación-http)
9. [Patrones Implementados](#patrones-implementados)
10. [Best Practices](#best-practices)

---

## Arquitectura de la Aplicación

### Estructura General

La aplicación sigue una arquitectura modular con separación de responsabilidades:

```
┌─────────────────────────────────────┐
│      Presentación (Features)         │
│  ┌───────────────────────────────┐  │
│  │ Components & Pages            │  │
│  │ ├─ Admin                      │  │
│  │ ├─ Doctor                     │  │
│  │ ├─ Patient                    │  │
│  │ └─ Public                     │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
         ↕ (Inyección de Servicios)
┌─────────────────────────────────────┐
│      Lógica de Negocio (Core)        │
│  ┌───────────────────────────────┐  │
│  │ Servicios                      │  │
│  │ ├─ AuthService                │  │
│  │ ├─ CitaService                │  │
│  │ ├─ HistorialService           │  │
│  │ └─ AnalisisService            │  │
│  └───────────────────────────────┘  │
│  ┌───────────────────────────────┐  │
│  │ Guards & Interceptores         │  │
│  │ ├─ AuthGuard                  │  │
│  │ ├─ RoleGuard                  │  │
│  │ └─ AuthInterceptor            │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
         ↕ (HttpClient)
┌─────────────────────────────────────┐
│      Backend (Spring Boot)           │
│  ├─ REST APIs                       │
│  └─ Business Logic                  │
└─────────────────────────────────────┘
```

### Principios de Diseño

1. **Separación de Responsabilidades:** Cada componente/servicio tiene una responsabilidad clara
2. **Reutilización:** Componentes compartidos en `shared/`
3. **Standalone Components:** Uso de Components independientes (sin NgModules)
4. **Inyección de Dependencias:** Angular DI para servicios y configuración
5. **Type Safety:** Uso de TypeScript e interfaces tipadas

---

## Estructura de Componentes

### Componentes Globales (app.ts)

```typescript
// app.ts
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('sigma');
}
```

**Responsabilidad:** Componente raíz que aloja el outlet de rutas.

### Estructura de Features

#### 1. Public Feature (Páginas Públicas)

```
feature/public/
├── public.routes.ts          # Rutas del módulo público
├── pages/
│   ├── home/                 # Página de inicio
│   │   ├── home.ts          # Componente
│   │   └── home.scss        # Estilos
│   ├── login/                # Página de login
│   │   ├── login.ts
│   │   └── login.scss
│   └── register/             # Página de registro
│       ├── register.ts
│       └── register.scss
```

**Rutas:**
```typescript
export const PUBLIC_ROUTES: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '**', redirectTo: '' }
];
```

#### 2. Patient Feature (Portal de Pacientes)

```
feature/patient/
├── patient.routes.ts
├── pages/
│   ├── dashboard/            # Panel principal
│   ├── citas/                # Gestión de citas
│   │   ├── lista/
│   │   ├── crear/
│   │   └─ detalles/
│   ├── analisis/             # Análisis clínicos
│   │   ├── lista/
│   │   └─ detalles/
│   ├── historial/            # Historial médico
│   │   └─ detalles/
│   └── perfil/               # Perfil del paciente
│       └─ editar/
```

#### 3. Doctor Feature (Panel de Obstetras)

```
feature/doctor/
├── pages/
│   ├── dashboard/            # Panel de obstetras
│   ├── citas/                # Citas programadas
│   │   ├── lista/
│   │   └─ detalles/
│   ├── pacientes/            # Gestión de pacientes
│   │   ├── lista/
│   │   └─ detalles/
│   ├── analisis/             # Análisis clínicos
│   │   ├── crear/
│   │   └─ actualizar/
│   └── notas/                # Notas médicas
│       └─ editor/
```

#### 4. Admin Feature (Panel Administrativo)

```
feature/admin/
├── pages/
│   ├── dashboard/            # Panel de admin
│   ├── usuarios/             # Gestión de usuarios
│   │   ├── lista/
│   │   ├── crear/
│   │   ├── editar/
│   │   └─ detalles/
│   ├── reportes/             # Reportes del sistema
│   │   ├── citas/
│   │   ├── pacientes/
│   │   └─ logs/
│   └── configuracion/        # Configuración
│       └─ parametros/
```

### Componentes Compartidos (shared/)

```
shared/
├── components/
│   ├── navbar/                # Barra de navegación
│   ├── sidebar/               # Barra lateral
│   ├── footer/                # Pie de página
│   ├── card/                  # Componente de tarjeta
│   ├── button/                # Botones reutilizables
│   ├── form/                  # Componentes de formulario
│   ├── table/                 # Tabla de datos
│   ├── pagination/            # Paginación
│   ├── modal/                 # Modal reutilizable
│   └── breadcrumb/            # Migas de pan
├── layouts/
│   ├── app-layout/            # Layout principal
│   └── auth-layout/           # Layout de autenticación
└── styles/
    ├── _variables.scss         # Variables globales
    ├── _mixins.scss            # Mixins útiles
    └── _common.scss            # Estilos comunes
```

### Patrón de Componentes

```typescript
// Ejemplo: CitaComponent
import { Component, Input, OnInit } from '@angular/core';
import { CitaService } from '../../services/cita.service';
import { Cita } from '../../interfaces/cita.interface';

@Component({
  selector: 'app-cita-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cita-card.component.html',
  styleUrl: './cita-card.component.scss'
})
export class CitaCardComponent implements OnInit {
  @Input() citaId!: number;
  cita$!: Observable<Cita>;

  constructor(private citaService: CitaService) {}

  ngOnInit(): void {
    this.cita$ = this.citaService.getCitaById(this.citaId);
  }

  cancelarCita(id: number): void {
    this.citaService.cancelCita(id).subscribe({
      next: () => {
        // Mostrar mensaje éxito
        this.cita$ = this.citaService.getCitaById(this.citaId);
      },
      error: (err) => {
        // Manejar error
      }
    });
  }
}
```

---

## Servicios

### AuthService (core/services/auth.ts)

**Responsabilidad:** Gestión de autenticación y autorización.

```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API_URL = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadUser();
  }

  // Login
  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.API_URL}/login`,
      { email, password }
    ).pipe(
      tap(response => {
        this.setToken(response.token);
        this.currentUserSubject.next(response.user);
        this.router.navigate(['/dashboard']);
      }),
      catchError(error => this.handleError(error))
    );
  }

  // Logout
  logout(): void {
    localStorage.removeItem(environment.jwtTokenName);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  // Obtener token actual
  getToken(): string | null {
    return localStorage.getItem(environment.jwtTokenName);
  }

  // Verificar si está autenticado
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  // Verificar si tiene rol
  hasRole(roles: string[]): boolean {
    const user = this.currentUserSubject.value;
    return user ? roles.includes(user.role) : false;
  }

  // Obtener usuario actual
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // Refrescar token
  refreshToken(): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.API_URL}/refresh`,
      {}
    ).pipe(
      tap(response => this.setToken(response.token))
    );
  }

  private setToken(token: string): void {
    localStorage.setItem(environment.jwtTokenName, token);
  }

  private loadUser(): void {
    // Cargar usuario desde localStorage o dar de baja sesión
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('Error:', error);
    return throwError(() => error);
  }
}
```

### CitaService (core/services/cita.service.ts)

**Responsabilidad:** Gestión de citas médicas.

```typescript
@Injectable({ providedIn: 'root' })
export class CitaService {
  private readonly API_URL = `${environment.apiUrl}/citas`;

  constructor(private http: HttpClient) {}

  // Obtener todas las citas
  getCitas(): Observable<Cita[]> {
    return this.http.get<Cita[]>(this.API_URL).pipe(
      catchError(error => this.handleError(error))
    );
  }

  // Obtener cita por ID
  getCitaById(id: number): Observable<Cita> {
    return this.http.get<Cita>(`${this.API_URL}/${id}`);
  }

  // Crear nueva cita
  createCita(cita: CreateCitaDTO): Observable<Cita> {
    return this.http.post<Cita>(this.API_URL, cita);
  }

  // Actualizar cita
  updateCita(id: number, cita: UpdateCitaDTO): Observable<Cita> {
    return this.http.put<Cita>(`${this.API_URL}/${id}`, cita);
  }

  // Cancelar cita
  cancelCita(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  // Obtener citas de un paciente
  getCitasByPaciente(pacienteId: number): Observable<Cita[]> {
    return this.http.get<Cita[]>(
      `${environment.apiUrl}/pacientes/${pacienteId}/citas`
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    return throwError(() => error);
  }
}
```

### HistorialService (core/services/historial.service.ts)

```typescript
@Injectable({ providedIn: 'root' })
export class HistorialService {
  private readonly API_URL = `${environment.apiUrl}/historiales`;

  constructor(private http: HttpClient) {}

  getHistorialByPaciente(pacienteId: number): Observable<Historial> {
    return this.http.get<Historial>(
      `${this.API_URL}/paciente/${pacienteId}`
    );
  }

  createHistorial(historial: CreateHistorialDTO): Observable<Historial> {
    return this.http.post<Historial>(this.API_URL, historial);
  }

  updateHistorial(id: number, historial: UpdateHistorialDTO): Observable<Historial> {
    return this.http.put<Historial>(`${this.API_URL}/${id}`, historial);
  }
}
```

### AnalisisService (core/services/analisis.service.ts)

```typescript
@Injectable({ providedIn: 'root' })
export class AnalisisService {
  private readonly API_URL = `${environment.apiUrl}/analisis`;

  constructor(private http: HttpClient) {}

  getAnalisisByPaciente(pacienteId: number): Observable<Analisis[]> {
    return this.http.get<Analisis[]>(
      `${this.API_URL}/paciente/${pacienteId}`
    );
  }

  createAnalisis(analisis: CreateAnalisisDTO): Observable<Analisis> {
    return this.http.post<Analisis>(this.API_URL, analisis);
  }

  getAnalisisById(id: number): Observable<Analisis> {
    return this.http.get<Analisis>(`${this.API_URL}/${id}`);
  }

  updateAnalisis(id: number, analisis: UpdateAnalisisDTO): Observable<Analisis> {
    return this.http.put<Analisis>(`${this.API_URL}/${id}`, analisis);
  }
}
```

---

## Guards y Interceptores

### AuthGuard (core/guards/auth.guard.ts)

Protege rutas que requieren autenticación.

```typescript
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivateFn {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree {
    if (this.authService.isAuthenticated()) {
      return true;
    }

    // Redirigir a login si no está autenticado
    this.router.navigate(['/login'], {
      queryParams: { returnUrl: state.url }
    });
    return false;
  }
}
```

### RoleGuard (core/guards/role.guard.ts)

Protege rutas por rol específico.

```typescript
@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivateFn {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot
  ): boolean | UrlTree {
    const requiredRoles = route.data['roles'] as string[];

    if (this.authService.hasRole(requiredRoles)) {
      return true;
    }

    // Redirigir a acceso denegado
    this.router.navigate(['/access-denied']);
    return false;
  }
}
```

### AuthInterceptor (core/interceptors/auth.interceptor.ts)

Agrega token JWT a todas las peticiones y maneja refresh.

```typescript
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();

    // Agregar token si existe
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Manejar error 401
        if (error.status === 401) {
          return this.handle401Error(req, next);
        }

        return throwError(() => error);
      })
    );
  }

  private handle401Error(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    return this.authService.refreshToken().pipe(
      switchMap((response) => {
        const newToken = response.token;
        req = req.clone({
          setHeaders: {
            Authorization: `Bearer ${newToken}`
          }
        });
        return next.handle(req);
      }),
      catchError(() => {
        this.authService.logout();
        return throwError(() => new Error('Session expired'));
      })
    );
  }
}
```

---

## Interfaces y Tipos

### Usuario (core/interfaces/usuario.interface.ts)

```typescript
export interface User {
  id: number;
  email: string;
  nombre: string;
  role: 'PACIENTE' | 'OBSTETRA' | 'ADMINISTRADOR';
  activo: boolean;
  fechaRegistro: Date;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}
```

### Cita (core/interfaces/cita.interface.ts)

```typescript
export interface Cita {
  id: number;
  pacienteId: number;
  obstetraId: number;
  fechaCita: Date;
  motivo: string;
  estado: 'PROGRAMADA' | 'CONFIRMADA' | 'COMPLETADA' | 'CANCELADA';
  notas?: string;
}

export interface CreateCitaDTO {
  obstetraId: number;
  fechaCita: Date;
  motivo: string;
}

export interface UpdateCitaDTO {
  estado?: string;
  notas?: string;
}
```

### Análisis (core/interfaces/analisis.interface.ts)

```typescript
export interface Analisis {
  id: number;
  pacienteId: number;
  tipo: string;
  fecha: Date;
  resultados: string;
  valoresReferencia?: string;
  interpretacion?: string;
}

export interface CreateAnalisisDTO {
  pacienteId: number;
  tipo: string;
  resultados: string;
}
```

### Historial (core/interfaces/historial.interface.ts)

```typescript
export interface Historial {
  id: number;
  pacienteId: number;
  diagnosticos: string[];
  medicamentos: string[];
  alergias: string[];
  notas: string;
  ultimaActualizacion: Date;
}
```

---

## Enrutamiento

### Rutas Principales (app.routes.ts)

```typescript
export const APP_ROUTES: Routes = [
  {
    path: '',
    component: AppLayout,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      {
        path: 'home',
        loadComponent: () => import('./features/public/pages/home/home')
          .then(m => m.HomeComponent)
      }
    ]
  },
  {
    path: 'auth',
    component: AuthLayout,
    children: [
      {
        path: 'login',
        loadComponent: () => import('./features/public/pages/login/login')
          .then(m => m.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () => import('./features/public/pages/register/register')
          .then(m => m.RegisterComponent)
      }
    ]
  },
  {
    path: 'paciente',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['PACIENTE'] },
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/patient/pages/dashboard/dashboard')
          .then(m => m.DashboardComponent)
      },
      {
        path: 'citas',
        loadComponent: () => import('./features/patient/pages/citas/citas')
          .then(m => m.CitasComponent)
      },
      // Más rutas de paciente...
    ]
  },
  {
    path: 'doctor',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['OBSTETRA'] },
    children: [
      // Rutas de doctor..
    ]
  },
  {
    path: 'admin',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMINISTRADOR'] },
    children: [
      // Rutas de admin...
    ]
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
```

---

## Gestión de Estado con RxJS

### Patrón Observable-Based

```typescript
// Ejemplo en un servicio
@Injectable({ providedIn: 'root' })
export class PacienteService {
  private citasSubject = new BehaviorSubject<Cita[]>([]);
  public citas$ = this.citasSubject.asObservable();

  constructor(private http: HttpClient) {}

  loadCitas(pacienteId: number): void {
    this.http.get<Cita[]>(`...`)
      .pipe(
        tap(citas => this.citasSubject.next(citas)),
        catchError(error => {
          console.error('Error loading citas', error);
          return of([]);
        })
      )
      .subscribe();
  }

  // En el componente
  citas$ = this.pacienteService.citas$;
}
```

### Uso con Async Pipe

```html
<!-- En template -->
<div *ngFor="let cita of citas$ | async">
  {{ cita.fecha | date }}
</div>
```

---

## Comunicación HTTP

### Peticiones GET

```typescript
// Obtener datos
this.http.get<T>(url).subscribe(
  (data) => console.log(data),
  (error) => console.error(error),
  () => console.log('Completado')
);
```

### Peticiones POST

```typescript
// Crear recurso
const body = { nombre: 'Juan', email: 'juan@example.com' };
this.http.post<T>(url, body).subscribe(
  (response) => console.log(response),
  (error) => console.error(error)
);
```

### Manejo de Errores

```typescript
this.http.get<T>(url).pipe(
  catchError(error => {
    if (error.status === 404) {
      return of(null);
    }
    return throwError(() => new Error('Error fetching data'));
  })
).subscribe();
```

---

## Patrones Implementados

### 1. Patrón Smart/Dumb Components

**Smart Component (Container):**
```typescript
@Component({
  selector: 'app-cita-list',
  template: `<app-cita-item *ngFor="let cita of citas$ | async" 
                            [cita]="cita"></app-cita-item>`,
})
export class CitaListComponent {
  citas$ = this.citaService.getCitas();
  constructor(private citaService: CitaService) {}
}
```

**Dumb Component (Presentational):**
```typescript
@Component({
  selector: 'app-cita-item',
  template: `<div>{{ cita.fecha | date }}</div>`,
})
export class CitaItemComponent {
  @Input() cita!: Cita;
}
```

### 2. Patrón OnPush Change Detection

```typescript
@Component({
  selector: 'app-optimized',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `...`
})
export class OptimizedComponent {
  // Cambios detectados solo cuando inputs cambian
}
```

### 3. Patrón RxJS Resources

```typescript
private destroy$ = new Subject<void>();

ngOnInit(): void {
  this.service.data$.pipe(
    takeUntil(this.destroy$)
  ).subscribe(data => {
    this.data = data;
  });
}

ngOnDestroy(): void {
  this.destroy$.next();
  this.destroy$.complete();
}
```

---

## Best Practices

### 1. Usar Type Safety

```typescript
// ✅ Correcto
interface Usuario {
  id: number;
  nombre: string;
}

// ❌ Evitar
const usuario: any = {};
```

### 2. Unsubscribe Properly

```typescript
// ✅ Con takeUntil
private destroy$ = new Subject<void>();

ngOnInit(): void {
  this.service.data$
    .pipe(takeUntil(this.destroy$))
    .subscribe(...);
}

ngOnDestroy(): void {
  this.destroy$.next();
  this.destroy$.complete();
}

// O usar async pipe en el template
// <div *ngFor="let item of items$ | async">
```

### 3. Error Handling

```typescript
// ✅ Manejar errores correctamente
this.http.get<T>(url).pipe(
  catchError(error => {
    this.showError(error.message);
    return of(null);
  })
).subscribe();
```

### 4. Lazy Loading

```typescript
// ✅ Cargar módulos on-demand
{
  path: 'admin',
  loadChildren: () => import('./admin/admin.routes')
    .then(m => m.ADMIN_ROUTES)
}
```

### 5. Environment-Based Configuration

```typescript
// ✅ Usar variables de entorno
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};

// En servicios
constructor() {
  this.apiUrl = environment.apiUrl;
}
```

---

**Última actualización:** 22 de febrero de 2026  
**Versión:** 1.0.0
