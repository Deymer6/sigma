# Información del Proyecto - Sigma Backend

## Resumen Ejecutivo

**Sigma Backend** es una aplicación de gestión clínica especializada en servicios obstétricos y de atención materna. Proporciona un sistema integral de gestión de pacientes, citas médicas, análisis clínicos e historiales clínicos con autenticación segura mediante JWT.

---

## Información General del Proyecto

| Información | Valor |
|-------------|-------|
| **Nombre del Proyecto** | Sigma Backend |
| **Versión Actual** | 0.0.1-SNAPSHOT |
| **Estado del Proyecto** | En Desarrollo |
| **Creado el** | 2025 |
| **Última Actualización** | 22 de febrero de 2026 |
| **Institución** | Ciclo VIII - Calidad y Pruebas de Software |
| **Tipo de Aplicación** | API REST de Backend |
| **Licencia** | Abierta (Por definir) |

---

## Tecnología Stack

### Backend
- **Framework Principal:** Spring Boot 3.5.7
- **Lenguaje:** Java 21
- **Build Tool:** Maven 3.8+

### Base de Datos
- **SGBD:** Microsoft SQL Server
- **ORM:** Spring Data JPA / Hibernate
- **Versión Mínima:** SQL Server 2019

### Seguridad
- **Autenticación:** JWT (JJWT 0.11.5)
- **Encriptación de Contraseñas:** BCrypt
- **Framework de Seguridad:** Spring Security
- **Modo de Sesión:** Stateless

### Utilidades
- **Generación de Código:** Lombok
- **Validación:** Spring Validation (Hibernate Validator)
- **Desarrollo:** Spring Boot DevTools

---

## Características Principales

### 1. Gestión de Usuarios
- ✅ Registro de pacientes
- ✅ Registro de obstetras
- ✅ Autenticación con JWT
- ✅ Control de roles (PACIENTE, OBSTETRA, ADMINISTRATIVA, ADMIN)
- ✅ Perfil de usuario

### 2. Gestión de Citas Médicas
- ✅ Crear nuevas citas
- ✅ Ver citas disponibles
- ✅ Confirmar/cancelar citas
- ✅ Historial de citas
- ✅ Notificaciones de citas

### 3. Análisis Clínicos
- ✅ Registrar análisis realizados
- ✅ Adjuntar resultados
- ✅ Historial de análisis
- ✅ Reportes de análisis

### 4. Historial Clínico
- ✅ Crear registros médicos
- ✅ Consultar historial completo
- ✅ Actualizar diagnósticos
- ✅ Observaciones médicas

### 5. Paneles de Control (Dashboard)
- ✅ Dashboard para pacientes
- ✅ Dashboard para personal médico
- ✅ Dashboard administrativo
- ✅ Reportes y estadísticas

### 6. Administración
- ✅ Gestión de usuarios
- ✅ Control de roles
- ✅ Gestión de obstetras
- ✅ Auditoría de acciones

---

## Arquitectura del Proyecto

### Estructura por Capas

```
┌─────────────────────────────────────────────┐
│           Controladores REST                │ ← Capa de Presentación (Controllers)
│  (AuthController, PacienteController, etc)  │
└─────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────┐
│         Lógica de Negocio                   │ ← Capa de Servicio (Services)
│    (PacienteService, CitaMedicaService, etc) │
└─────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────┐
│    Acceso a Datos                           │ ← Capa de Persistencia (Repositories)
│  (JPA Repositories, Spring Data)            │
└─────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────┐
│       Base de Datos                         │ ← SQL Server
│        (SQL Server SIGMA)                   │
└─────────────────────────────────────────────┘
```

### Patrón de Diseño

El proyecto utiliza el **patrón MVC (Model-View-Controller)** con capas:

1. **Controllers:** Reciben solicitudes HTTP y las traducen a llamadas de servicio
2. **Services:** Implementan la lógica de negocio
3. **Repositories:** Acceden a la base de datos
4. **Models/Entities:** Representan los datos
5. **DTOs:** Transfieren datos entre capas

---

## Entidades Principales

### 1. Usuario
Clase base para todos los usuarios del sistema.
- Email único
- Contraseña hasheada
- Roles asociados
- Estado activo/inactivo

### 2. Paciente (extiende Usuario)
Representa a los pacientes.
- Cantidad de historiales
- Citas asociadas
- Análisis clínicos

### 3. Obstetra (extiende Usuario)
Profesional médico especializado.
- Matrícula profesional
- Especialidad
- Consultorio asignado

### 4. CitaMedica
Representa las citas entre paciente y obstetra.
- Estados: PROGRAMADA, CONFIRMADA, COMPLETADA, CANCELADA
- Motivo de la consulta
- Notas médicas

### 5. HistorialClinico
Registros históricos de consultas y diagnósticos.
- Diagnósticos
- Tratamientos
- Observaciones

### 6. AnalisisClinico
Registros de análisis y pruebas realizadas.
- Tipo de análisis
- Resultados
- Archivos adjuntos

---

## Flujos de Trabajo

### Flujo de Autenticación

```
┌─────────────────────────────────────┐
│  Usuario: email + password          │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  AuthController.login()             │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  AuthService.autenticar()           │
│  - Validar email/password           │
│  - Cargar user de BD                │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  AuthService.generarToken()         │
│  - Crear JWT con claims             │
│  - Incluir roles y email            │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  AuthResponse                       │
│  { token, expiresIn, usuario }      │
└─────────────────────────────────────┘
```

### Flujo de Protección de Endpoints

```
┌─────────────────────────────────────┐
│  Request con JWT en header          │
│  Authorization: Bearer <token>      │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  JwtAuthFilter                      │
│  - Extrae token del header          │
│  - Valida firma y expiración        │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Spring Security                    │
│  - Carga autoridades (roles)        │
│  - Marca como autenticado           │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  @PreAuthorize verificación         │
│  - Chequea roles requeridos         │
└──────────────┬──────────────────────┘
               │
         ┌─────┴──────┐
         ▼             ▼
    ✅ OK        ❌ 403 Forbidden
```

---

## Endpoints Disponibles

### Autenticación (`/api/auth`)
```
POST   /login                    - Autenticación de usuario
POST   /register-paciente        - Registro de paciente
POST   /register-obstetra        - Registro de obstetra
POST   /refresh-token            - Refrescar JWT (futuro)
```

### Pacientes (`/api/pacientes`)
```
GET    /dashboard-info           - Dashboard del paciente
GET    /{id}                     - Obtener datos del paciente
PUT    /{id}                     - Actualizar datos
GET    /mis-citas                - Ver mis citas
GET    /mi-historial             - Ver mi historial
```

### Citas Médicas (`/api/citas`)
```
POST   /                         - Crear nueva cita
GET    /disponibles              - Listar citas disponibles
GET    /{id}                     - Obtener detalles
PUT    /{id}/confirmar           - Confirmar cita
PUT    /{id}/cancelar            - Cancelar cita
```

### Análisis Clínicos (`/api/analisis-clinicos`)
```
POST   /                         - Crear análisis
GET    /paciente/{pacienteId}    - Análisis del paciente
PUT    /{id}/completar           - Marcar como completado
GET    /{id}/descargar           - Descargar reporte
```

### Historial Clínico (`/api/historiales`)
```
POST   /                         - Crear registro
GET    /paciente/{pacienteId}    - Obtener historial
PUT    /{id}                     - Actualizar registro
GET    /{id}                     - Obtener detalles
```

### Administración (`/api/admin`)
```
GET    /dashboard                - Dashboard administrativo
GET    /usuarios                 - Listar todos los usuarios
PUT    /usuarios/{id}/estado     - Cambiar estado de usuario
GET    /reportes/pacientes       - Reporte de pacientes
GET    /reportes/citas           - Reporte de citas
```

---

## Instalación Rápida

```bash
# 1. Requisitos
- Java 21+
- SQL Server 2019+
- Maven 3.8+

# 2. Clonar proyecto
git clone <URL>
cd sigma-backend

# 3. Instalar dependencias
./mvnw clean install

# 4. Crear base de datos
# Ejecutar en SQL Server:
# CREATE DATABASE SIGMA;

# 5. Configurar credenciales
# Editar: src/main/resources/application.properties

# 6. Ejecutar
./mvnw spring-boot:run

# 7. Acceder
# http://localhost:8080/api/auth/login
```

---

## Documentación Disponible

En la raíz del proyecto encontrarás:

1. **README.md** - Descripción general e instrucciones básicas
2. **DOCUMENTACION_TECNICA.md** - Detalles arquitectónicos y técnicos
3. **GUIA_GIT.md** - Instrucciones para manejo de Git y GitHub
4. **INSTALACION_DEPLOYMENT.md** - Instalación local y deployment en producción
5. **CONTRIBUCION.md** - Guía para contribuidores al proyecto
6. **FAQ.md** - Preguntas frecuentes y troubleshooting
7. **PROYECTO.md** - Este archivo, resumen del proyecto

---

## Roles y Permisos

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **PACIENTE** | Usuario final | Ver citas propias, ver historial, dashboard |
| **OBSTETRA** | Médico especializado | Crear citas, añadir análisis, ver historiales, crear registros |
| **ADMINISTRATIVO** | Personal administrativo | Gestionar usuarios, ver reportes |
| **ADMIN** | Administrador sistema | Acceso total, gestión de sistema |

---

## Configuración de Entorno

### Variables de Entorno Importantes

```properties
# Base de Datos
SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=SIGMA
SPRING_DATASOURCE_USERNAME=sigma_user
SPRING_DATASOURCE_PASSWORD=****

# Aplicación
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Seguridad
JWT_SECRET=tu-clave-secreta-muy-larga
JWT_EXPIRATION=86400000
```

---

## Ciclo de Desarrollo

### Entorno de Desarrollo

```bash
# Cliente: UI (React, Angular, Vue, etc)
# |
# |-- HTTP REST --> API Backend (Spring Boot)
#                        |
#                        |-- SQL --> Base de Datos (SQL Server)
```

### Ambiente de Producción

```
Load Balancer
    |
    ├── Server 1 (Spring Boot App)
    ├── Server 2 (Spring Boot App)
    └── Server 3 (Spring Boot App)
            |
            └── SQL Server (Replicado)
            └── Backups
            └── Logs
```

---

## Requisitos No Funcionales

### Rendimiento
- Respuesta API < 500ms en condiciones normales
- Soportar 1000+ usuarios concurrentes
- Base de datos optimizada con índices

### Seguridad
- Contraseñas hasheadas con BCrypt
- Autenticación JWT con expiración
- CORS configurado
- Validación de entrada
- Manejo seguro de errores

### Disponibilidad
- Uptime objetivo > 99.5%
- Backups diarios automáticos
- Logs de auditoría
- Monitoreo continuo

### Escalabilidad
- Arquitectura stateless
- Facilidad para horizontal scaling
- Connection pooling optimizado

---

## Roadmap Futuro

### Versión 1.0.0
- [ ] Autenticación 2FA
- [ ] Refresh tokens automáticos
- [ ] Notificaciones por email
- [ ] Recordatorio de citas SMS
- [ ] Reportes avanzados

### Versión 1.1.0
- [ ] Integración con calendario (Google Calendar)
- [ ] Formularios digitales para pacientes
- [ ] Videoconferencias (Zoom, Meet)
- [ ] Prescripción electrónica
- [ ] Sistema de recetas

### Versión 2.0.0
- [ ] App móvil iOS/Android
- [ ] IA para diagósticos preliminares
- [ ] Integración con FastAPI
- [ ] Real-time notifications
- [ ] Análisis de datos avanzado

---

## Contribuyentes

### Autores Originales
- Equipo de Desarrollo SIGMA

### Especificaciones
- Ciclo VIII: Calidad y Pruebas de Software
- Profesor Titular
- Fecha: 2025-2026

---

## Métricas del Proyecto

### Código
- **Archivos de Código:** 8 controladores, 8 servicios, 8 repositorios
- **Entidades:** 8 modelos principales
- **DTOs:** 13 objetos de transferencia
- **Líneas de Código:** ~5000+
- **Cobertura de Tests:** Objetivo 80%+

### Dependencias
- **Dependencias Directas:** 20+
- **Dependencias Transitivas:** 100+
- **Versión Spring Boot:** 3.5.7

---

## Cumplimiento de Estándares

- ✅ REST API RESTful
- ✅ Versionado semántico (0.0.1)
- ✅ Arquitectura orientada a servicios
- ✅ Código limpio (Clean Code)
- ✅ SOLID principles
- ✅ Seguridad (OWASP Top 10)
- ✅ Documentación completa

---

## Contacto y Soporte

### Reportar Bugs
Crear un issue en GitHub con:
- Descripción del problema
- Pasos para reproducir
- Stack trace
- Información del sistema

### Solicitar Características
Crear issue con etiqueta `feature-request`

### Consultas Generales
Ver FAQ.md o documentación técnica

---

## Licencia

Por definir. Actualmente es un proyecto académico de evaluación.

---

## Fecha de Generación de Documentación

- **Creado:** 22 de febrero de 2026
- **Versión:** 1.0
- **Estado:** Completo y Actualizado

---

## Resumen de Documentación Creada

Este proyecto ahora cuenta con documentación completa:

✅ **README.md** - Guía de inicio  
✅ **DOCUMENTACION_TECNICA.md** - Detalles técnicos  
✅ **GUIA_GIT.md** - Control de versiones  
✅ **INSTALACION_DEPLOYMENT.md** - Instalación y deployment  
✅ **CONTRIBUCION.md** - Guía de contribución  
✅ **FAQ.md** - Preguntas frecuentes  
✅ **PROYECTO.md** - Información del proyecto (este archivo)  

**Total: 7 documentos** con más de **15,000+ palabras** de documentación técnica.

---

*Proyecto Sigma Backend - Sistema de Gestión Clínica Obstétrica*  
*Desarrollado con Spring Boot 3.5.7 y Java 21*  
*Especializado en atención maternal y servicios obstétricos*

