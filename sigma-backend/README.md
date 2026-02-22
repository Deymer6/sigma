# Sigma Backend - Sistema de Gestión Clínica Obstétrica

## Descripción General

**Sigma Backend** es una aplicación de gestión clínica especializada en servicios obstétricos, desarrollada con **Spring Boot 3.5.7** y **Java 21**. El sistema implementa autenticación JWT, control de roles de usuario, y gestión integral de citas médicas, análisis clínicos e historiales de pacientes.

## Tecnologías Principales

- **Framework:** Spring Boot 3.5.7
- **Lenguaje:** Java 21
- **Base de Datos:** SQL Server
- **Autenticación:** JWT (JSON Web Tokens)
- **Seguridad:** Spring Security
- **Validación:** Spring Validation
- **ORM:** Spring Data JPA / Hibernate
- **Build Tool:** Maven
- **Utilidades:** Lombok

### Dependencias Clave

```xml
- Spring Boot Starter Web (REST APIs)
- Spring Boot Starter Data JPA (Acceso a datos)
- Spring Boot Starter Security (Autenticación/Autorización)
- Spring Boot Starter Validation (Validación de datos)
- Microsoft SQL Server JDBC Driver
- JJWT 0.11.5 (JSON Web Token)
- Lombok (Reducción de boilerplate)
```

## Estructura del Proyecto

```
sigma-backend/
├── src/main/java/com/sigma/sigma_backend/
│   ├── SigmaBackendApplication.java          # Clase principal de la aplicación
│   ├── config/                               # Configuraciones de la aplicación
│   │   ├── DataInitializer.java              # Inicialización de datos
│   │   ├── GlobalExceptionHandler.java       # Manejo global de excepciones
│   │   ├── JwtAuthFilter.java                # Filtro de autenticación JWT
│   │   ├── SecurityConfig.java               # Configuración de seguridad
│   │   └── WebConfig.java                    # Configuración web
│   ├── controller/                           # Controladores REST
│   │   ├── AdminController.java              # Endpoints de administrador
│   │   ├── AnalisisClinicoController.java    # Endpoints de análisis clínicos
│   │   ├── AuthController.java               # Endpoints de autenticación
│   │   ├── CitaMedicaController.java         # Endpoints de citas médicas
│   │   ├── HistorialClinicoController.java   # Endpoints de historiales
│   │   ├── ObstetraController.java           # Endpoints de obstetras
│   │   └── PacienteController.java           # Endpoints de pacientes
│   ├── dto/                                  # Data Transfer Objects
│   │   ├── AnalisisClinicoDTO.java
│   │   ├── AuthResponse.java                 # Respuesta de autenticación con JWT
│   │   ├── CitaMedicaDTO.java
│   │   ├── CreateObstetraRequest.java
│   │   ├── DashboardAdminDTO.java            # DTO del dashboard admin
│   │   ├── DashboardPacienteDTO.java         # DTO del dashboard paciente
│   │   ├── DashboardStaffDTO.java            # DTO del dashboard staff
│   │   ├── DetalleAtencionDTO.java
│   │   ├── FinalizarAtencionRequest.java
│   │   ├── HistorialClinicoDTO.java
│   │   ├── LoginRequest.java                 # DTO para login
│   │   ├── ObstetraDTO.java
│   │   ├── PacienteCardDTO.java
│   │   ├── RegisterRequest.java              # DTO para registro
│   │   ├── RegistroObstetraRequest.java
│   │   └── RegistroPacienteRequest.java
│   ├── model/                                # Entidades JPA
│   │   ├── AnalisisClinico.java              # Análisis clínicos realizados
│   │   ├── CitaMedica.java                   # Citas médicas
│   │   ├── HistorialClinico.java             # Historial de pacientes
│   │   ├── Obstetra.java                     # Profesionales obstetras
│   │   ├── Paciente.java                     # Datos de pacientes
│   │   ├── PersonalAdministrativo.java       # Personal administrativo
│   │   ├── Role.java                         # Roles de usuarios
│   │   └── Usuario.java                      # Usuarios del sistema
│   ├── repository/                           # Repositorios JPA
│   └── service/                              # Lógica de negocio
├── src/main/resources/
│   ├── application.properties                # Configuración de la aplicación
│   ├── static/
│   └── templates/
├── src/test/
│   └── java/com/sigma/sigma_backend/
│       └── SigmaBackendApplicationTests.java
├── pom.xml                                   # Configuración de Maven
├── mvnw & mvnw.cmd                          # Maven Wrapper
└── uploads/                                  # Directorio para archivos subidos
```

## Configuración de la Base de Datos

### Propiedades de Conexión (`application.properties`)

```properties
spring.application.name=sigma-backend
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;
spring.datasource.username=USUARIO_DB
spring.datasource.password=CONTRASEÑA_SEGURA
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

### Requisitos:
- SQL Server ejecutándose en `localhost:1433`
- Base de datos llamada `SIGMA`

## Roles y Autorización

El sistema implementa un control de acceso basado en roles (RBAC):

| Rol | Descripción | Permisos |
|-----|-------------|---------|
| **PACIENTE** | Usuarios que buscan atención médica | Ver citas propias, historial, dashboard |
| **OBSTETRA** | Profesionales médicos especializados | Crear/modificar citas, añadir análisis, ver historiales |
| **ADMINISTRATIVO** | Personal de administración | Gestionar usuarios, consultar datos |
| **ADMIN** | Administrador del sistema | Acceso total, gestión de usuarios, reportes |

## Componentes Principales

### 1. Autenticación (AuthController)

**Endpoints:**
- `POST /api/auth/login` - Autenticación de usuario
- `POST /api/auth/register-paciente` - Registro de paciente
- `POST /api/auth/register-obstetra` - Registro de obstetra

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 86400,
  "usuario": { "id": 1, "email": "user@example.com" }
}
```

### 2. Gestión de Pacientes (PacienteController)

**Endpoints:**
- `GET /api/pacientes/dashboard-info` - Obtener dashboard del paciente

### 3. Gestión de Citas Médicas (CitaMedicaController)

**Funcionalidades:**
- Crear nuevas citas
- Listar citas disponibles
- Confirmar/cancelar citas
- Historial de citas

### 4. Análisis Clínicos (AnalisisClinicoController)

**Funcionalidades:**
- Registrar análisis clínicos
- Consultar análisis realizados
- Generar reportes de análisis

### 5. Historial Clínico (HistorialClinicoController)

**Funcionalidades:**
- Crear registros de historial
- Consultar historial del paciente
- Actualizar información clínica

### 6. Gestión de Obstetras (ObstetraController)

**Funcionalidades:**
- Registrar obstetras
- Listar obstetras disponibles
- Gestionar disponibilidad

## Seguridad

### Configuración de Seguridad (SecurityConfig.java)

- **JWT Filter:** Valida tokens en cada solicitud
- **CORS:** Configurado para permitir solicitudes desde diferentes orígenes
- **CSRF:** Deshabilitado (API stateless)
- **Session Policy:** Stateless (sin sesiones en servidor)
- **Password Encoding:** BCryptPasswordEncoder

### Rutas Públicas:
- `/api/auth/**` - Autenticación
- `/uploads/**` - Archivos públicos

### Rutas Protegidas:
Todas las rutas excepto las públicas requieren autenticación JWT válida

## Manejo de Excepciones

El `GlobalExceptionHandler` centraliza el manejo de excepciones:
- Excepciones de validación
- Excepciones de seguridad
- Excepciones de negocio
- Excepciones de base de datos

Respuesta de error estándar:
```json
{
  "error": "Descripción del error",
  "message": "Detalles adicionales",
  "timestamp": "2026-02-22T10:30:00Z"
}
```

## Instalación y Configuración

### Prerrequisitos
- Java 21 o superior
- SQL Server 2019 o superior
- Maven 3.8 o superior (incluido en el proyecto)

### Pasos de Instalación

1. **Clonar o descargar el proyecto**
   ```bash
   git clone <repository-url>
   cd sigma-backend
   ```

2. **Crear la base de datos en SQL Server**
   ```sql
   CREATE DATABASE SIGMA;
   ```

3. **Configurar credenciales** en `application.properties` si es necesario

4. **Compilar el proyecto**
   ```bash
   ./mvnw clean install
   ```

5. **Ejecutar la aplicación**
   ```bash
   ./mvnw spring-boot:run
   ```

   O ejecutar el archivo JAR generado:
   ```bash
   java -jar target/sigma-backend-0.0.1-SNAPSHOT.jar
   ```

### Usando Maven Wrapper (Windows)
```bash
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

## Pruebas

Ejecutar pruebas unitarias:
```bash
./mvnw test
```

## Estructura de Solicitud/Respuesta

### Solicitud de Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "contraseña123"
}
```

### Respuesta Exitosa
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400,
  "usuario": {
    "id": 1,
    "email": "usuario@example.com",
    "nombre": "Juan Pérez"
  }
}
```

### Usar Token en Solicitudes Posteriores
```bash
GET /api/pacientes/dashboard-info
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Flujo de Autenticación

1. Usuario envía credenciales a `/api/auth/login`
2. Sistema valida contra la base de datos
3. Sistema genera JWT con claim de email y roles
4. Cliente almacena JWT
5. Cliente incluye JWT en encabezado `Authorization` para cada solicitud
6. `JwtAuthFilter` valida el JWT antes de procesar la solicitud
7. Spring Security carga los permisos basados en roles del JWT

## Endpoints Principales

| Método | Endpoint | Autenticación | Rol Requerido |
|--------|----------|---------------|----------------|
| POST | /api/auth/login | No | - |
| POST | /api/auth/register-paciente | No | - |
| POST | /api/auth/register-obstetra | No | - |
| GET | /api/pacientes/dashboard-info | Sí | PACIENTE |
| GET | /api/staff/** | Sí | OBSTETRA, ADMIN |
| GET | /api/admin/** | Sí | ADMIN |
| POST | /api/citas | Sí | PACIENTE/OBSTETRA |
| GET | /api/historiales | Sí | PACIENTE/OBSTETRA |
| POST | /api/analisis-clinicos | Sí | OBSTETRA |

## Uso de Lombok

El proyecto utiliza Lombok para reducir código boilerplate:
- `@Data` - Genera getters, setters, toString, equals, hashCode
- `@Entity` - Combina con anotaciones de Lombok
- `@Repository` - Interfaces de repositorio

## Inicialización de Datos

El archivo `DataInitializer.java` inicia datos por defecto:
- Roles del sistema
- Usuarios de prueba
- Datos iniciales de configuración

## Estructura de Carpetas de Recursos

- `src/main/resources/static/` - Archivos estáticos (CSS, JS, imágenes)
- `src/main/resources/templates/` - Templates (si se usa Thymeleaf)
- `uploads/` - Directorio para archivos cargados por usuarios

## Mantenimiento y Desarrollo

### Agregar un Nuevo Endpoint

1. Crear DTO en `dto/` si es necesario
2. Crear método en servicio correspondiente en `service/`
3. Crear método en controlador correspondiente en `controller/`
4. Proteger con `@PreAuthorize` si es necesario
5. Agregar en `SecurityConfig.java` si requiere nueva ruta

### Agregar una Nueva Entidad

1. Crear clase de modelo en `model/` con anotaciones JPA
2. Crear repositorio en `repository/`
3. Crear servicio en `service/`
4. Crear DTO en `dto/`
5. Crear controlador en `controller/`

## Troubleshooting

### Error de Conexión a Base de Datos
- Verificar que SQL Server esté ejecutándose
- Verificar credenciales en `application.properties`
- Verificar que la base de datos `SIGMA` existe
- Verificar que el puerto 1433 está disponible

### Error de JWT Inválido
- Verificar formato: `Bearer <token>`
- Verificar que el token no ha expirado
- Verificar que el usuario existe en la base de datos

### Error 403 Forbidden
- Verificar que el usuario tiene el rol requerido
- Verificar que el token es válido
- Verificar configuración en `SecurityConfig.java`

## Notas de Desarrollo

- La aplicación utiliza actualización automática de esquema: `spring.jpa.hibernate.ddl-auto=update`
- Los tokens JWT expiran en 24 horas (ajustable en configuración)
- Las contraseñas se encriptan con BCrypt
- Todas las APIs retornan JSON

## Contacto y Soporte

Para preguntas o problemas, consulte la documentación de Spring Boot:
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)

## Versión

- Versión Actual: 0.0.1-SNAPSHOT
- Última Actualización: 22 de febrero de 2026

---

**Desarrollado con Spring Boot y Java** | **Especializado en Gestión Clínica Obstétrica**
