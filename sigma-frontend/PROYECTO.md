# PROYECTO.md - Sigma (Sistema Integral de Gestión Médica Avanzada)

## Descripción General del Proyecto

**SIGMA** es un sistema integral de gestión clínica especializado en servicios obstétricos. Proporciona soluciones tecnológicas para la administración de pacientes, citas médicas, análisis clínicos e historiales de salud.

El proyecto se divide en dos aplicaciones principales:
- **Backend:** API REST desarrollada en Spring Boot 3.5.7 con Java 21
- **Frontend:** Aplicación web responsiva desarrollada en Angular 20.2.0

## Objetivos del Proyecto

### Objetivos Generales
1. Modernizar la gestión clínica obstétrica mediante tecnología digital
2. Mejorar la experiencia del paciente y del profesional médico
3. Centralizar información médica en una plataforma segura
4. Facilitar la toma de decisiones basada en datos históricos

### Objetivos Específicos

**Para Pacientes:**
- Agendar citas de forma online
- Consultar resultados de análisis clínicos
- Acceder a su historial médico completo
- Comunicación directa con obstetras
- Recordatorios de citas por correo/SMS

**Para Obstetras:**
- Gestionar agenda de citas
- Acceder a historiales de pacientes
- Registrar análisis clínicos
- Actualizar notas médicas
- Seguimiento del estado de pacientes

**Para Administradores:**
- Gestión completa de usuarios
- Auditoría y reportes del sistema
- Configuración de parámetros
- Monitoreo del sistema
- Gestión de copias de seguridad

## Stack Tecnológico Completo

### Frontend (sigma-frontend/)

```
Framework:       Angular 20.2.0
Lenguaje:        TypeScript 5.5+
Estilos:         SCSS
Routing:         Angular Router
HTTP:            HttpClient
Estado:          RxJS (Observables)
SSR:             Angular Universal
UI Components:   Bootstrap Icons, FontAwesome, SweetAlert2
Build:           Angular CLI 20.2.2
Testing:         Jasmine & Karma
Package Manager: npm
Node.js:         v20.x+
```

### Backend (sigma-backend/)

```
Framework:       Spring Boot 3.5.7
Lenguaje:        Java 21
Base de Datos:   SQL Server
ORM:             Hibernate / Spring Data JPA
Autenticación:   JWT (JJWT 0.11.5)
Seguridad:       Spring Security
Validación:      Spring Validation
Build:           Maven
Testing:         JUnit 5 & Mockito
Documentación:   OpenAPI/Swagger
```

### Infraestructura

```
Control de Versiones:  Git & GitHub
CI/CD:                 (Configurado en deployment)
Containerización:      Docker (opcional)
Deployment:            On-premises o Cloud
Base de Datos:         SQL Server Express
```

## Equipo de Desarrollo

### Roles y Responsabilidades

| Rol | Responsabilidades |
|-----|-------------------|
| **Project Manager** | Coordinación general, timeline, stakeholders |
| **Tech Lead Backend** | Arquitectura backend, decisiones técnicas |
| **Tech Lead Frontend** | Arquitectura frontend, experiencia de usuario |
| **Desarrolladores Backend** | Implementación de APIs, servicios, BD |
| **Desarrolladores Frontend** | Componentes, vistas, integración |
| **QA / Testing** | Pruebas, reporte de bugs |
| **DevOps** | Deployment, infraestructura, monitoreo |

## Características Principales

### 1. Autenticación y Autorización

```
✅ Login seguro con JWT
✅ Roles diferenciados (Paciente, Obstetra, Admin)
✅ Guards de rutas
✅ Interceptores para token refresh
✅ Logout seguro
✅ Recuperación de contraseña
```

### 2. Gestión de Pacientes

```
✅ Registro de pacientes
✅ Perfil de paciente (datos personales, alergias, etc.)
✅ Historial médico completo
✅ Vinculación con obstetras
✅ Estado de citas
✅ Análisis clínicos del paciente
```

### 3. Gestión de Citas

```
✅ Búsqueda de obstetras disponibles
✅ Agendamiento de citas
✅ Confirmación de citas
✅ Cancelación de citas
✅ Recordatorios
✅ Historial de citas
```

### 4. Análisis Clínicos

```
✅ Registro de análisis
✅ Tipos de análisis diversos
✅ Resultados con valores de referencia
✅ Interpretación médica
✅ Historial de análisis
```

### 5. Historiales Clínicos

```
✅ Historial médico del paciente
✅ Diagnósticos previos
✅ Tratamientos realizados
✅ Alergias y medicamentos
✅ Notas médicas
```

### 6. Panel Administrativo

```
✅ Gestión de usuarios
✅ Asignación de roles
✅ Auditoría de accesos
✅ Reportes del sistema
✅ Configuración de parámetros
```

## Módulos del Sistema

### Módulo de Autenticación
- Manejo de credenciales seguras
- Token JWT con refresh
- Guards de protección
- Roles y permisos

### Módulo de Pacientes
- Registro y perfil
- Histórico médico
- Gestión de citas
- Análisis clínicos
- Estado de salud

### Módulo Médico
- Gestión de obstetras
- Especialidades
- Disponibilidad
- Consultorios
- Notas médicas

### Módulo de Reportes
- Estadísticas de citas
- Históricos de pacientes
- Análisis clínicos
- Auditoría

## Requisitos No Funcionales

### Seguridad
- ✅ Encriptación de contraseñas (BCrypt)
- ✅ HTTPS obligatorio en producción
- ✅ JWT con expiración
- ✅ CORS configurado
- ✅ Validación de entrada

### Rendimiento
- ✅ TTL de respuestas < 500ms
- ✅ Caché de datos frecuentes
- ✅ Paginación de resultados
- ✅ Lazy loading de módulos

### Escalabilidad
- ✅ Arquitectura modular
- ✅ Stateless en backend
- ✅ Independencia de frontend/backend
- ✅ Database ready para nuevas tablas

### Disponibilidad
- ✅ Manejo de errores robusto
- ✅ Validación de datos
- ✅ Rollback procedures
- ✅ Backup automático

## Timeline del Proyecto

```
Fase 1: Planificación y Diseño
├─ Análisis de requisitos         [✅ Completado]
├─ Diseño de arquitectura         [✅ Completado]
└─ Setup inicial de proyecto      [✅ Completado]

Fase 2: Desarrollo Backend
├─ Configuración de proyecto      [✅ Completado]
├─ Entidades y BD                 [✅ Completado]
├─ Servicios y controladores      [✅ Completado]
├─ Autenticación JWT              [✅ Completado]
├─ Testing                        [✅ Completado]
└─ Documentación                  [✅ Completado]

Fase 3: Desarrollo Frontend       [👈 ACTUAL]
├─ Setup de proyecto              [✅ Completado]
├─ Estructura base                [✅ Completado]
├─ Componentes principales        [🔄 En progreso]
├─ Servicios e integración        [🔄 En progreso]
├─ Testing                        [⏳ Pendiente]
└─ Documentación                  [👈 ACTUAL]

Fase 4: QA e Integración
├─ Testing completo               [⏳ Pendiente]
├─ Integración E2E                [⏳ Pendiente]
├─ Ajustes de performance         [⏳ Pendiente]
└─ Deploy a staging               [⏳ Pendiente]

Fase 5: Deployment
├─ Optimizaciones finales         [⏳ Pendiente]
├─ Deploy a producción            [⏳ Pendiente]
├─ Monitoreo inicial              [⏳ Pendiente]
└─ Soporte post-launch            [⏳ Pendiente]
```

## Arquitectura de Alto Nivel

```
┌─────────────────────────────────────────────────┐
│              USUARIOS FINALES                    │
├─────────────────────────────────────────────────┤
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │      FRONTEND (Angular 20)               │  │
│  │  ──────────────────────────────────     │  │
│  │  ├─ Public Pages (Home, Login)          │  │
│  │  ├─ Patient Portal                      │  │
│  │  ├─ Doctor Dashboard                    │  │
│  │  └─ Admin Panel                         │  │
│  └──────────────────────────────────────────┘  │
│              ↕ (HttpClient + JWT)               │
│  ┌──────────────────────────────────────────┐  │
│  │      BACKEND (Spring Boot 3.5.7)        │  │
│  │  ──────────────────────────────────     │  │
│  │  ├─ REST APIs                           │  │
│  │  ├─ Business Logic (Services)           │  │
│  │  ├─ Authentication (JWT)                │  │
│  │  └─ Data Persistence (JPA/Hibernate)    │  │
│  └──────────────────────────────────────────┘  │
│              ↕ (JDBC)                           │
│  ┌──────────────────────────────────────────┐  │
│  │      DATABASE (SQL Server)               │  │
│  │  ──────────────────────────────────     │  │
│  │  ├─ Usuarios                            │  │
│  │  ├─ Pacientes                           │  │
│  │  ├─ Obstetras                           │  │
│  │  ├─ Citas Médicas                       │  │
│  │  ├─ Análisis Clínicos                   │  │
│  │  └─ Historiales Clínicos                │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

## Plan de Deployment

### Entornos

```
Desarrollo
└─ En máquina local del desarrollador
  ├─ Backend: localhost:8080
  └─ Frontend: localhost:4200

Staging
└─ En servidor de pruebas
  ├─ Backend: staging-api.sigma.local
  └─ Frontend: staging.sigma.local

Producción
└─ En servidor de producción
  ├─ Backend: api.sigma.com
  └─ Frontend: sigma.com
```

### Proceso de Deployment

```
1. Build de artifacts
   └─ Backend: JAR empaquetado
   └─ Frontend: Compilación SSR

2. Pruebas pre-deployment
   └─ Smoke tests
   └─ Validación de configuración

3. Deploy a staging
   └─ Backend deployment
   └─ Frontend deployment
   └─ Validación E2E

4. Approval y deploy a producción
   └─ Backup de BD
   └─ Deploy gradual
   └─ Monitoreo

5. Post-deployment
   └─ Validación en vivo
   └─ Análisis de logs
   └─ Notificación a usuarios
```

## Métricas de Éxito

### Rendimiento
- Tiempo de respuesta < 500ms
- Uptime > 99.5%
- Load time < 3 segundos

### Usabilidad
- UX score > 8/10
- User satisfaction > 90%
- Error rate < 2%

### Confiabilidad
- Test coverage > 80%
- Defect escape rate < 5%
- Security score A+

## Documentación del Proyecto

### Backend
- [README.md](../sigma-backend/README.md)
- [DOCUMENTACION_TECNICA.md](../sigma-backend/DOCUMENTACION_TECNICA.md)
- [INSTALACION_DEPLOYMENT.md](../sigma-backend/INSTALACION_DEPLOYMENT.md)
- [CONTRIBUCION.md](../sigma-backend/CONTRIBUCION.md)

### Frontend
- [README.md](README.md)
- [DOCUMENTACION_TECNICA.md](DOCUMENTACION_TECNICA.md)
- [INSTALACION_DEPLOYMENT.md](INSTALACION_DEPLOYMENT.md)
- [CONTRIBUCION.md](CONTRIBUCION.md)

## Convenciones del Proyecto

### Nomenclatura
```
Backend:  camelCase, PascalCase para clases
Frontend: camelCase, kebab-case para archivos, PascalCase para componentes
Database: snake_case, singular/plural según contexto
```

### Estructura Git
```
main               - Código en producción
dev                - Rama de integración
feature/*          - Nuevas features
bugfix/*           - Correcciones
hotfix/*           - Fixes urgentes
```

### Code Style
```
Backend:  Google Java Style Guide
Frontend: Angular Style Guide + ESLint
```

## Próximos Pasos

1. ✅ Completar documentación frontend
2. 🔄 Finalizar componentes principales
3. ⏳ Ejecutar suite completa de tests
4. ⏳ Deploy a staging
5. ⏳ QA completo
6. ⏳ Deploy a producción

---

**Versión del Documento:** 1.0.0  
**Fecha de Creación:** 22 de febrero de 2026  
**Última Actualización:** 22 de febrero de 2026  
**Estado:** Activo
