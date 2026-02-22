# Documentación Técnica Detallada - Sigma Backend

## Tabla de Contenidos

1. [Entidades de Base de Datos](#entidades-de-base-de-datos)
2. [Configuración de JPA](#configuración-de-jpa-y-base-de-datos)
3. [Servicios](#servicios)
4. [Controladores](#controladores)
5. [DTOs](#dtos)
6. [Configuración JWT](#configuración-jwt)
7. [Manejo de Seguridad](#manejo-de-seguridad)
8. [Guía de Desarrollo](#guía-de-desarrollo)

---

## Entidades de Base de Datos

### 1. Usuario (Tabla: usuarios)

Entidad base para todos los usuarios del sistema.

**Atributos principales:**
- `id` - Identificador único (PK)
- `email` - Email único del usuario
- `password` - Contraseña hasheada con BCrypt
- `nombre` - Nombre completo
- `activo` - Estado del usuario
- `fechaRegistro` - Fecha de creación

**Relaciones:**
- OneToMany con Role (un usuario puede tener múltiples roles)
- Herencia: Paciente, Obstetra, PersonalAdministrativo extienden Usuario

---

### 2. Paciente (Tabla: pacientes)

Extiende Usuario. Representa a los pacientes del sistema.

**Atributos principales:**
- `cedula` - Número de cédula único
- `fechaNacimiento` - Fecha de nacimiento
- `telefono` - Número de contacto
- `direccion` - Dirección de residencia
- `grupoSanguineo` - Grupo sanguíneo
- `alergias` - Alergias conocidas

**Relaciones:**
- OneToMany con CitaMedica
- OneToMany con HistorialClinico
- OneToMany con AnalisisClinico

---

### 3. Obstetra (Tabla: obstetras)

Extiende Usuario. Representa a los profesionales médicos especializados.

**Atributos principales:**
- `numeroMatricula` - Matrícula profesional única
- `especialidad` - Especialidad médica
- `experiencia` - Años de experiencia
- `disponibilidad` - Horarios disponibles
- `consultorio` - Número de consultorio asignado

**Relaciones:**
- OneToMany con CitaMedica (como médico asignado)
- OneToMany con AnalisisClinico (análisis realizados)

---

### 4. CitaMedica (Tabla: citas_medicas)

Representa las citas entre pacientes y obstetras.

**Atributos principales:**
- `id` - Identificador único (PK)
- `pacienteId` - Referencia a Paciente (FK)
- `obstetraId` - Referencia a Obstetra (FK)
- `fechaCita` - Fecha y hora de la cita
- `motivo` - Motivo de la consulta
- `estado` - Estado (PROGRAMADA, COMPLETADA, CANCELADA)
- `notas` - Notas del médico

**Estados posibles:**
```
PROGRAMADA    → Cita scheduled
CONFIRMADA    → Paciente confirmó asistencia
COMPLETADA    → Cita realizada
CANCELADA     → Cita cancelada
```

---

### 5. HistorialClinico (Tabla: historiales_clinicos)

Registro del historial médico del paciente.

**Atributos principales:**
- `id` - Identificador único (PK)
- `pacienteId` - Referencia a Paciente (FK)
- `obstetraId` - Referencia a Obstetra (FK)
- `fecha` - Fecha del registro
- `diagnostico` - Diagnóstico realizado
- `tratamiento` - Tratamiento prescrito
- `observaciones` - Observaciones adicionales

---

### 6. AnalisisClinico (Tabla: analisis_clinicos)

Registros de análisis y pruebas clínicas realizadas.

**Atributos principales:**
- `id` - Identificador único (PK)
- `pacienteId` - Referencia a Paciente (FK)
- `tipoAnalisis` - Tipo de análisis (Ultrasonido, Laboratorio, etc.)
- `fecha` - Fecha del análisis
- `resultado` - Resultado del análisis
- `obstetricaId` - Obstetra que solicitó el análisis
- `estado` - Estado (PENDIENTE, COMPLETADO)
- `archivo` - Archivo adjunto (ruta)

---

### 7. Role (Tabla: roles)

Define los roles del sistema.

**Roles predefinidos:**
```
ROL_PACIENTE         → Rol de paciente
ROL_OBSTETRA         → Rol de obstetra
ROL_ADMINISTRATIVO   → Rol de administrativo
ROL_ADMIN            → Rol de administrador
```

---

### 8. PersonalAdministrativo (Tabla: personal_administrativo)

Extiende Usuario. Representa al personal administrativo.

**Atributos principales:**
- `puesto` - Puesto de trabajo
- `departamento` - Departamento asignado
- `fechaIngreso` - Fecha de ingreso

---

## Configuración de JPA y Base de Datos

### Creación Automática de Tablas (DDL Auto)

La aplicación crea las tablas automáticamente mediante Hibernate sin necesidad de scripts SQL manuales. Esto se controla mediante la propiedad `spring.jpa.hibernate.ddl-auto` en `application.properties`.

**Configuración actual:**
```properties
spring.jpa.hibernate.ddl-auto=create
```

### Opciones de DDL-AUTO

| Opción | Comportamiento | Cuándo Usar |
|--------|---|---|
| **create** | Crea todas las tablas (elimina si existen previamente) | ✅ Desarrollo inicial, Testing, CI/CD |
| **create-drop** | Crea al iniciar, elimina al cerrar | ⚠️ Solo testing automatizado |
| **update** | Actualiza el schema sin perder datos | ✅ Desarrollo, Actualización de tablas |
| **validate** | Valida que el schema actual coincida con las entidades | ✅ Producción (seguro, sin cambios) |
| **none** | No hace nada, control manual | ⚠️ Scripts SQL manuales |

### Proceso de Creación de Tablas

Cuando inicia la aplicación con `create`:

1. **Lee las entidades** - Analiza all las clases con `@Entity`
2. **Genera SQL** - Crea statements DDL basado en las anotaciones
3. **Ejecuta DDL** - Creates/altera tablas en la BD
4. **Registra cambios** - Hibernate mantiene información de esquema

### Ejemplo: Entidad Usuario

```java
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "activo")
    private Boolean activo = true;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaRegistro;
}
```

**SQL generado:**
```sql
CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    activo BIT,
    fecha_registro DATETIME2
);
```

### Para Cambiar a Producción

Se recomienda cambiar a `validate` una vez que el schema está estable:

```properties
# application.properties
spring.jpa.hibernate.ddl-auto=validate

# O si necesitas actualizar:
spring.jpa.hibernate.ddl-auto=update
```

Esto evita cambios accidentales en el schema de producción.

### Relaciones y Cascadas

Las relaciones between entidades se crean automáticamente:

```java
@Entity
@Table(name = "citas_medicas")
public class CitaMedica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "obstetra_id", nullable = false)
    private Obstetra obstetra;
}
```

**SQL generado:**
```sql
CREATE TABLE citas_medicas (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    paciente_id BIGINT NOT NULL FOREIGN KEY REFERENCES pacientes(id),
    obstetra_id BIGINT NOT NULL FOREIGN KEY REFERENCES obstetras(id)
);
```

---

## Servicios

### PacienteService

**Responsabilidades:**
- Gestión de datos de pacientes
- Obtener información del dashboard
- Validar datos del paciente
- Operaciones CRUD

**Métodos principales:**
```java
DashboardPacienteDTO obtenerDatosDashboard(String email)
Paciente obtenerPacientePorEmail(String email)
void crearPaciente(RegistroPacienteRequest request)
List<Paciente> listarPacientes()
```

---

### CitaMedicaService

**Responsabilidades:**
- Gestión de citas médicas
- Crear nueva cita
- Confirmar/cancelar citas
- Listar citas disponibles
- Gestionar disponibilidad de obstetras

**Métodos principales:**
```java
CitaMedica crearCita(CitaMedicaDTO dto)
void confirmarCita(Long citaId)
void cancelarCita(Long citaId, String motivo)
List<CitaMedica> listarCitasDisponibles(LocalDate fecha)
List<CitaMedica> obtenerCitasPaciente(String emailPaciente)
```

---

### AnalisisClinicoService

**Responsabilidades:**
- Registrar análisis clínicos
- Consultar análisis realizados
- Generar reportes
- Gestionar archivos de análisis

**Métodos principales:**
```java
AnalisisClinico crearAnalisis(AnalisisClinicoDTO dto)
List<AnalisisClinico> obtenerAnalisisPaciente(Long pacienteId)
void completarAnalisis(Long analisisId, String resultado)
byte[] descargarResultado(Long analisisId)
```

---

### HistorialClinicoService

**Responsabilidades:**
- Crear registros de historial
- Consultar historial completo del paciente
- Actualizar información clínica
- Validar integridad de datos

**Métodos principales:**
```java
HistorialClinico crearRegistro(HistorialClinicoDTO dto)
List<HistorialClinico> obtenerHistorialPaciente(Long pacienteId)
HistorialClinico actualizarRegistro(Long historiaId, HistorialClinicoDTO dto)
```

---

### AuthService

**Responsabilidades:**
- Autenticación de usuarios
- Generación de JWT
- Validación de credenciales
- Registro de nuevos usuarios

**Métodos principales:**
```java
AuthResponse login(LoginRequest request)
AuthResponse registerPaciente(RegistroPacienteRequest request)
AuthResponse registerObstetra(RegistroObstetraRequest request)
String generarToken(Usuario usuario)
boolean validarToken(String token)
```

---

### ObstetraService

**Responsabilidades:**
- Gestión de obstetras
- Registrar nuevos obstetras
- Consultar disponibilidad
- Gestionar información profesional

**Métodos principales:**
```java
Obstetra crearObstetra(CreateObstetraRequest request)
List<Obstetra> listarObstetras()
Obstetra obtenerPorId(Long id)
List<Obstetra> obtenerDisponibles(LocalDate fecha, LocalTime hora)
```

---

## Controladores

### AuthController

**Ruta base:** `/api/auth`

**Endpoints:**

| Método | Ruta | Descripción | Autenticación |
|--------|------|-------------|---------------|
| POST | `/login` | Autenticar usuario | No |
| POST | `/register-paciente` | Registrar nuevo paciente | No |
| POST | `/register-obstetra` | Registrar nuevo obstetra | No |
| POST | `/refresh-token` | Refrescar token JWT | Sí |

---

### PacienteController

**Ruta base:** `/api/pacientes`

**Endpoints:**

| Método | Ruta | Descripción | Rol Requerido |
|--------|------|-------------|---------------|
| GET | `/dashboard-info` | Obtener datos del dashboard | PACIENTE |
| GET | `/{id}` | Obtener datos del paciente | PACIENTE |
| PUT | `/{id}` | Actualizar datos del paciente | PACIENTE |
| GET | `/mis-citas` | Listar mis citas | PACIENTE |
| GET | `/mi-historial` | Obtener mi historial clínico | PACIENTE |

---

### CitaMedicaController

**Ruta base:** `/api/citas`

**Endpoints:**

| Método | Ruta | Descripción | Rol Requerido |
|--------|------|-------------|---------------|
| POST | `/` | Crear nueva cita | PACIENTE |
| GET | `/disponibles` | Listar citas disponibles | PACIENTE |
| GET | `/{id}` | Obtener detalles de cita | PACIENTE/OBSTETRA |
| PUT | `/{id}/confirmar` | Confirmar cita | PACIENTE |
| PUT | `/{id}/cancelar` | Cancelar cita | PACIENTE |
| GET | `/paciente/{pacienteId}` | Citas del paciente | OBSTETRA |

---

### AnalisisClinicoController

**Ruta base:** `/api/analisis-clinicos`

**Endpoints:**

| Método | Ruta | Descripción | Rol Requerido |
|--------|------|-------------|---------------|
| POST | `/` | Crear análisis clínico | OBSTETRA |
| GET | `/paciente/{pacienteId}` | Obtener análisis del paciente | OBSTETRA |
| PUT | `/{id}/completar` | Marcar análisis como completado | OBSTETRA |
| GET | `/{id}/descargar` | Descargar reporte del análisis | OBSTETRA/PACIENTE |

---

### HistorialClinicoController

**Ruta base:** `/api/historiales`

**Endpoints:**

| Método | Ruta | Descripción | Rol Requerido |
|--------|------|-------------|---------------|
| POST | `/` | Crear registro de historial | OBSTETRA |
| GET | `/paciente/{pacienteId}` | Obtener historial del paciente | OBSTETRA/PACIENTE |
| PUT | `/{id}` | Actualizar registro de historial | OBSTETRA |
| GET | `/{id}` | Obtener detalles de registro | OBSTETRA |

---

### AdminController

**Ruta base:** `/api/admin`

**Endpoints:**

| Método | Ruta | Descripción | Rol Requerido |
|--------|------|-------------|---------------|
| GET | `/dashboard` | Obtener dashboard administrativo | ADMIN |
| GET | `/usuarios` | Listar todos los usuarios | ADMIN |
| PUT | `/usuarios/{id}/estado` | Cambiar estado de usuario | ADMIN |
| GET | `/reportes/pacientes` | Reporte de pacientes | ADMIN |
| GET | `/reportes/citas` | Reporte de citas | ADMIN |

---

### ObstetraController

**Ruta base:** `/api/obstetras`

**Endpoints:**

| Método | Ruta | Descripción | Rol Requerido |
|--------|------|-------------|---------------|
| POST | `/` | Registrar nuevo obstetra | ADMIN |
| GET | `/` | Listar obstetras | ADMIN |
| GET | `/{id}` | Obtener detalles | ADMIN |
| PUT | `/{id}` | Actualizar obstetra | ADMIN |
| GET | `/disponibles` | Listar obstetras disponibles | PACIENTE |

---

## DTOs

### LoginRequest

```java
{
  "email": "usuario@example.com",
  "password": "password123"
}
```

### RegistroPacienteRequest

```java
{
  "email": "paciente@example.com",
  "password": "password123",
  "nombre": "Juan Pérez",
  "cedula": "1234567890",
  "fechaNacimiento": "1990-05-15",
  "telefono": "3101234567",
  "direccion": "Calle 123, Ciudad",
  "grupoSanguineo": "O+"
}
```

### RegistroObstetraRequest

```java
{
  "email": "obstetra@example.com",
  "password": "password123",
  "nombre": "Dra. María López",
  "numeroMatricula": "OB-123456",
  "especialidad": "Obstetricia General",
  "experiencia": 10,
  "consultorio": "201"
}
```

### CitaMedicaDTO

```java
{
  "pacienteId": 1,
  "obstetraId": 2,
  "fechaCita": "2026-03-15T14:30:00",
  "motivo": "Consulta de control",
  "notas": "Traer últimos exámenes"
}
```

### DashboardPacienteDTO

```java
{
  "paciente": {
    "id": 1,
    "nombre": "Juan Pérez",
    "email": "paciente@example.com"
  },
  "proximasCitas": [
    {
      "id": 1,
      "fechaCita": "2026-03-15T14:30:00",
      "obstetra": "Dra. María López",
      "estado": "CONFIRMADA"
    }
  ],
  "historialReciente": [
    {
      "fecha": "2026-02-20",
      "diagnostico": "Embarazo gestacional",
      "medico": "Dra. María López"
    }
  ],
  "ultimosAnalisis": [
    {
      "tipo": "Ultrasonido",
      "fecha": "2026-02-18",
      "estado": "COMPLETADO"
    }
  ]
}
```

---

## Configuración JWT

### Estructura del Token

El token JWT generado contiene:

**Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload:**
```json
{
  "exp": 1708600800,
  "iat": 1708514400,
  "sub": "usuario@example.com",
  "roles": ["ROL_PACIENTE"],
  "userId": 1
}
```

### Validación del Token

- **Algoritmo:** HS256 (HMAC SHA-256)
- **Tiempo de expiración:** 24 horas
- **Clave secreta:** Configurada en `SecurityConfig`
- **Claims requeridos:** `exp`, `iat`, `sub`

---

## Manejo de Seguridad

### JwtAuthFilter

Se ejecuta para cada solicitud HTTP (excepto públicas).

**Proceso:**
1. Extrae el token del encabezado `Authorization`
2. Valida la firma del token
3. Valida la expiración
4. Extrae los claims
5. Carga la autoridad del usuario en Spring Security

### GlobalExceptionHandler

Maneja excepciones globales de la aplicación.

**Excepciones manejadas:**
- `JwtException` → 401 Unauthorized
- `UsernameNotFoundException` → 401 Unauthorized
- `BadCredentialsException` → 401 Unauthorized
- `AccessDeniedException` → 403 Forbidden
- `MethodArgumentNotValidException` → 400 Bad Request
- `Exception` → 500 Internal Server Error

---

## Guía de Desarrollo

### Creando un Nuevo Controlador

```java
@RestController
@RequestMapping("/api/nuevo")
public class NuevoController {
    
    private final NuevoService nuevoService;
    
    public NuevoController(NuevoService nuevoService) {
        this.nuevoService = nuevoService;
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('ROL_PACIENTE')")
    public ResponseEntity<List<NuevoDTO>> listar() {
        return ResponseEntity.ok(nuevoService.listar());
    }
}
```

### Creando un Nuevo Servicio

```java
@Service
public class NuevoService {
    
    private final NuevoRepository repository;
    
    public NuevoService(NuevoRepository repository) {
        this.repository = repository;
    }
    
    public List<Nuevo> listar() {
        return repository.findAll();
    }
}
```

### Creando un Nuevo Repositorio

```java
public interface NuevoRepository extends JpaRepository<Nuevo, Long> {
    List<Nuevo> findByPacienteId(Long pacienteId);
    Optional<Nuevo> findByIdAndPacienteId(Long id, Long pacienteId);
}
```

### Validaciones Comunes

```java
@Valid @RequestBody NuevoDTO dto

// En DTO:
@NotNull(message = "El campo no puede ser nulo")
@NotBlank(message = "El campo no puede estar vacío")
@Email(message = "Debe ser un email válido")
@Size(min = 8, message = "Mínimo 8 caracteres")
@Pattern(regexp = "...", message = "Formato inválido")
```

---

## Mejores Prácticas

1. **Inyección de Dependencias:** Usar constructor injection
2. **Servicios:** Toda la lógica de negocio va en servicios
3. **Controladores:** Solo orquestar llamadas a servicios
4. **DTOs:** Transferir datos entre capas
5. **Validación:** Usar anotaciones de `@Valid`
6. **Excepciones:** Lanzar excepciones específicas en servicios
7. **Logs:** Registrar operaciones importantes
8. **Seguridad:** Validar permisos en controladores
9. **Bases de Datos:** Usar transacciones para operaciones críticas
10. **Testing:** Escribir tests para servicios y controladores

---

Documento actualizado: 22 de febrero de 2026
