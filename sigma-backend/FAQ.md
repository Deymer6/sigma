# FAQ - Preguntas Frecuentes - Sigma Backend

## Instalación y Configuración

### P: ¿Cuáles son los requisitos mínimos del sistema?

**R:** 
- Java 21 o superior
- SQL Server 2019 o superior
- 4GB RAM (mínimo), 8GB recomendado
- 500MB espacio libre en disco
- Windows, Linux o macOS

---

### P: Obtengo error "java.lang.UnsupportedClassVersionError" al ejecutar

**R:** Significa que estás usando una versión antigua de Java.

```bash
# Verificar versión
java -version

# Debería mostrar Java 21 o superior
# Si no, descargar e instalar Java 21 desde:
# https://www.oracle.com/java/technologies/downloads/#java21

# Actualizar variable de entorno JAVA_HOME
# En Windows:
setx JAVA_HOME "C:\Program Files\Java\jdk-21"
```

---

### P: ¿Cómo cambio la contraseña de la base de datos?

**R:**

```sql
-- En SQL Server Management Studio
USE master;
ALTER LOGIN sigma_user WITH PASSWORD = 'NuevaContraseña123!';

-- Luego actualizar en application.properties
spring.datasource.password=NuevaContraseña123
```

---

### P: ¿La aplicación usa SQLite o necesita SQL Server?

**R:** Necesita **SQL Server específicamente**. No funciona con SQLite ni other bases de datos sin cambios en el código.

Si necesitas cambiar a PostgreSQL:
1. Cambiar dependencia en pom.xml
2. Usar driver PostgreSQL
3. Cambiar dialecto de Hibernate

---

### P: ¿Qué pasos debo seguir para ejecutar localmente?

**R:**

```bash
# 1. Verificar Java
java -version

# 2. Clonar repositorio
git clone https://github.com/usuario/sigma-backend.git
cd sigma-backend

# 3. Instalar dependencias
./mvnw clean install

# 4. Crear base de datos
# Abre SQL Server Management Studio y ejecuta:
# CREATE DATABASE SIGMA;

# 5. Configurar credenciales en application.properties
# ubicado en src/main/resources/

# 6. Ejecutar aplicación
./mvnw spring-boot:run

# 7. Acceder a http://localhost:8080
```

---

## Autenticación y JWT

### P: ¿Cómo funciona la autenticación en este sistema?

**R:** 

1. Usuario envía email y contraseña a `/api/auth/login`
2. Sistema valida contra la base de datos
3. Si es correcto, genera un JWT (JSON Web Token)
4. Cliente almacena el JWT
5. Incluye JWT en encabezado `Authorization: Bearer <token>` para solicitudes posteriores
6. Sistema valida JWT en cada solicitud

---

### P: ¿Cuánto tiempo dura un token JWT?

**R:** Los tokens expiran en **24 horas**.

Para cambiar:

```java
// En el servicio de autenticación, buscar:
.setIssuedAt(new Date())
.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas en ms

// Para 48 horas: 172800000
// Para 1 hora: 3600000
```

---

### P: ¿Cómo refrescar un token expirado?

**R:** Actualmente debes hacer login nuevamente. Para implementar refresh tokens:

```java
// Crear endpoint para refresh
@PostMapping("/refresh-token")
public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String token) {
    // Validar token viejo
    // Generar token nuevo
    // Retornar nuevo token
}
```

---

### P: ¿Dónde se almacena el JWT en el frontend?

**R:** No está implementado en el backend. El frontend debe:

```javascript
// Opción 1: localStorage (simple pero menos seguro)
localStorage.setItem('token', response.token);

// Opción 2: sessionStorage (se borra al cerrar navegador)
sessionStorage.setItem('token', response.token);

// Opción 3: Cookie con HttpOnly (más seguro)
// Requiere configuración adicional en backend
```

---

## Base de Datos

### P: ¿Cómo veo los datos en la base de datos?

**R:**

```bash
# Opción 1: SQL Server Management Studio
# Conectar a localhost
# Abrir base de datos SIGMA
# Ver tablas en Object Explorer

# Opción 2: Línea de comandos
sqlcmd -S localhost -U sa -P admin123

# Dentro de sqlcmd:
USE SIGMA;
SELECT * FROM usuarios;
SELECT * FROM pacientes;
SELECT * FROM citas_medicas;
GO
```

---

### P: ¿Cómo reseteo la base de datos?

**R:**

```sql
-- Advertencia: ESTO ELIMINARÁ TODOS LOS DATOS

-- Opción 1: Eliminar base de datos completa
DROP DATABASE SIGMA;
CREATE DATABASE SIGMA;

-- Opción 2: Limpiar tablas individuales
USE SIGMA;
DELETE FROM citas_medicas;
DELETE FROM historiales_clinicos;
DELETE FROM analisis_clinicos;
DELETE FROM pacientes;
DELETE FROM obstetras;
DELETE FROM usuarios;
DELETE FROM roles;

-- La aplicación recreará las tablas al iniciar
```

---

### P: ¿Cómo puedo ver en tiempo real qué SQL está ejecutando Hibernate?

**R:**

```properties
# En application.properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

## Desarrollo

### P: ¿Por qué mi endpoint retorna 403 Forbidden?

**R:** Significa que el usuario autenticado no tiene el rol requerido.

1. Verificar que incluiste el token JWT
2. Verificar que el token es válido
3. Verificar que el usuario tiene el rol correcto
4. Ver la anotación `@PreAuthorize` en el controller

```java
@GetMapping("/admin-only")
@PreAuthorize("hasAuthority('ROL_ADMIN')")  // Solo ROL_ADMIN puede acceder
public ResponseEntity<Object> adminOnly() { }
```

---

### P: ¿Cómo agrego un nuevo endpoint?

**R:**

```java
// 1. Crear método en servicio (src/main/java/.../service/)
public Objeto nuevoMetodo(String parametro) {
    // Lógica
}

// 2. Crear método en controlador (src/main/java/.../controller/)
@RestController
@RequestMapping("/api/ruta")
public class MiControlador {
    
    @PostMapping("/nuevo")
    @PreAuthorize("hasAuthority('ROL_NECESARIO')")
    public ResponseEntity<Objeto> nuevo(@RequestBody NuevoDTO dto) {
        Objeto resultado = servicio.nuevoMetodo(dto.getParametro());
        return ResponseEntity.ok(resultado);
    }
}

// 3. Si es una ruta nueva, agregar en SecurityConfig.java
.requestMatchers("/api/ruta/**").hasAuthority("ROL_NECESARIO")
```

---

### P: ¿Dónde van los archivos que los usuarios suben?

**R:** En la carpeta `uploads/` en la raíz del proyecto.

```properties
# Configurar ruta diferente en application.properties
file.upload-dir=C:/sigma-uploads

# O usar ruta relativa
file.upload-dir=./uploads
```

```java
// En el controlador
@PostMapping("/uploading")
public ResponseEntity<String> upload(@RequestParam MultipartFile archivo) {
    String ruta = "uploads/" + archivo.getOriginalFilename();
    Files.write(Paths.get(ruta), archivo.getBytes());
    return ResponseEntity.ok("Archivo guardado");
}
```

---

### P: ¿Cómo agrego validaciones a un DTO?

**R:**

```java
@Data
public class ejemplo DTO {
    
    @NotNull(message = "El nombre no puede ser nulo")
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @Email(message = "Email inválido")
    private String email;
    
    @Positive(message = "La edad debe ser positiva")
    @Min(value = 18, message = "Debe ser mayor de 18")
    private Integer edad;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Cédula debe tener 10 dígitos")
    private String cedula;
}

// En el controlador
@PostMapping
public ResponseEntity<Object> crear(@Valid @RequestBody MiDTO dto) {
    // Las validaciones se ejecutan automáticamente
}
```

---

### P: ¿Cómo devuelvo diferentes tipos de respuesta?

**R:**

```java
// Respuesta exitosa
return ResponseEntity.ok(objeto);
return ResponseEntity.status(201).body(objeto);

// Con header personalizado
HttpHeaders headers = new HttpHeaders();
headers.set("X-Custom-Header", "valor");
return new ResponseEntity<>(objeto, headers, HttpStatus.CREATED);

// Respuesta sin contenido
return ResponseEntity.noContent().build();

// Respuesta de error
return ResponseEntity.badRequest().body("Mensaje de error");
return ResponseEntity.status(404).body("No encontrado");
return ResponseEntity.status(500).body("Error interno");
```

---

## Testing

### P: ¿Por qué algunos tests fallan?

**R:** Causas comunes:

```bash
# 1. Base de datos no está conectada
# Solución: Iniciar SQL Server

# 2. Datos de prueba no existen
# Solución: Ejecutar @BeforeEach para crear datos

# 3. Puertos ya en uso
# Solución: Cambiar puerto en test o matar proceso

# 4. Falta de dependencia
# Solución: Agregear @SpringBootTest en clase de test
```

---

### P: ¿Cómo escribo un test unitario?

**R:**

```java
@SpringBootTest
class PacienteServiceTest {
    
    @MockBean
    private PacienteRepository repository;
    
    @InjectMocks
    private PacienteService service;
    
    @Test
    void testCrearPaciente() {
        // Arrange - Preparar datos
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setEmail("test@test.com");
        
        // Act - Ejecutar método
        service.crearPaciente(request);
        
        // Assert - Verificar resultado
        verify(repository, times(1)).save(any());
    }
}

// Ejecutar tests
./mvnw test

// Ejecutar un test específico
./mvnw test -Dtest=PacienteServiceTest#testCrearPaciente
```

---

## Deployment

### P: ¿Cómo despliego la aplicación en un servidor?

**R:** Ver documento `INSTALACION_DEPLOYMENT.md` en la raíz del proyecto:

Pasos rápidos:
1. Compilar: `mvnw.cmd package`
2. Copiar JAR al servidor
3. Configurar base de datos en el servidor
4. Ejecutar: `java -jar sigma-backend-0.0.1-SNAPSHOT.jar`

---

### P: ¿Cómo ejecuto la aplicación en background?

**R:**

```bash
# Windows
# Crear archivo run.bat
start javaw -jar sigma-backend-0.0.1-SNAPSHOT.jar

# Linux/macOS
# Ejecutar en background
nohup java -jar sigma-backend-0.0.1-SNAPSHOT.jar &

# Ver logs
tail -f nohup.out

# O usar screen
screen java -jar sigma-backend-0.0.1-SNAPSHOT.jar
# Presionar Ctrl+A + D para desprender
```

---

### P: ¿Cómo configuro HTTPS?

**R:**

```properties
# En application.properties
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat

# Generar keystore (una sola vez)
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 \
  -keystore keystore.p12 -validity 365 -storetype PKCS12 \
  -storepass password
```

---

## Problemas Comunes

### P: "Connection refused - localhost:1433"

**R:**

```bash
# SQL Server no está ejecutándose
# En Windows:
net start MSSQLSERVER

# O desde Services
# Abrir Services → buscar "SQL Server" → Start

# Verificar que está ejecutándose
netstat -ano | findstr 1433
```

---

### P: "Port 8080 already in use"

**R:**

```bash
# Ver proceso en puerto 8080
netstat -ano | findstr :8080

# Matar proceso (PID es el número mostrado)
taskkill /PID <PID> /F

# O cambiar puerto en application.properties
server.port=8081
```

---

### P: "java.security.SignatureException: Signature verification failed"

**R:** El JWT está corrupto o fue modificado.

La clave secreta en el servidor no coincide con la usada para firmar el token.

```java
// Verificar que ambas usan la misma clave secreta
private static final String SECRET_KEY = "tu-clave-secreta-muy-larga-y-segura";

// La clave debe ser >= 256 bits (32 caracteres)
```

---

### P: "No rows were returned by the query"

**R:** La entidad buscada no existe en la base de datos.

```java
// En lugar de:
Paciente p = repository.getById(id);  // Lanza excepción si no existe

// Usar:
Optional<Paciente> p = repository.findById(id);
if (p.isPresent()) {
    // Hacer algo
} else {
    throw new EntityNotFoundException("Paciente no encontrado");
}

// O en una línea
Paciente p = repository.findById(id)
    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
```

---

## Git y GitHub

### P: ¿Cómo contribuyo al proyecto?

**R:** Ver documento `CONTRIBUCION.md` en la raíz:

Pasos rápidos:
1. Fork el repositorio
2. Clonar: `git clone https://github.com/TU_USUARIO/sigma-backend.git`
3. Crear rama: `git checkout -b feature/nombre`
4. Hacer cambios
5. Commit: `git commit -m "feat: Descripción"`
6. Push: `git push origin feature/nombre`
7. Pull Request desde GitHub

---

### P: Tengo conflicto de merge, ¿cómo lo resuelvo?

**R:**

```bash
# 1. Ver archivos con conflicto
git status

# 2. Editar archivos manualmente
# Buscar: <<<<<<<, =======, >>>>>>>
# Elegir qué código mantener

# 3. Después de resolver
git add .
git commit -m "Resolve merge conflict"
git push
```

---

### P: ¿Cómo vuelvo a un commit anterior?

**R:**

```bash
# Ver commits anteriores
git log --oneline -10

# Volver a un commit (sin perder cambios)
git reset --soft <hash>

# Volver a un commit (perdiendo cambios)
git reset --hard <hash>

# Crear un nuevo commit que revierta cambios
git revert <hash>
```

---

## Seguridad

### P: ¿Dónde almaceno credenciales sensibles?

**R:**

```bash
# NUNCA en código ni en git
# Usar variables de entorno

# Crear archivo .env (agregar a .gitignore)
DATABASE_PASSWORD=tu_password
JWT_SECRET=tu_secret
API_KEY=tu_api_key

# En application.properties
spring.datasource.password=${DATABASE_PASSWORD}

# En Java
String password = System.getenv("DATABASE_PASSWORD");
```

---

### P: ¿Cómo evito SQL injection?

**R:** Usar PreparedStatements (que JPA hace automáticamente):

```java
// ✅ SEGURO - JPA maneja escape
Optional<Usuario> user = repository.findByEmail(email);

// ✅ SEGURO - Usando @Query con parámetros nombrados
@Query("SELECT u FROM Usuario u WHERE u.email = :email")
Optional<Usuario> findByEmailCustom(@Param("email") String email);

// ❌ PELIGROSO - Concatenación de strings
String query = "SELECT * FROM usuarios WHERE email = '" + email + "'";
```

---

### P: ¿Cómo protejo mis endpoints?

**R:**

```java
// Requerir autenticación
@GetMapping("/datos")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<Object> datos() { }

// Requerir rol específico
@GetMapping("/admin")
@PreAuthorize("hasAuthority('ROL_ADMIN')")
public ResponseEntity<Object> admin() { }

// Requerir múltiples roles
@GetMapping("/staff")
@PreAuthorize("hasAnyAuthority('ROL_OBSTETRA', 'ROL_ADMIN')")
public ResponseEntity<Object> staff() { }

// Expresión personalizada
@PreAuthorize("@securityService.puedoAcceder(#id, authentication.principal.id)")
public ResponseEntity<Object> datos(@PathVariable Long id) { }
```

---

## Performance

### P: ¿Cómo mejoro el rendimiento?

**R:**

```properties
# 1. Optimizar base de datos
# En application.properties
spring.datasource.hikari.maximum-pool-size=20
spring.jpa.properties.hibernate.jdbc.fetch_size=50

# 2. Usar caché
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=10m

# 3. Lazy loading
@OneToMany(fetch = FetchType.LAZY)
private List<Cita> citas;

# 4. Índices en base de datos
CREATE INDEX idx_paciente_email ON pacientes(email);
```

---

### P: ¿Cómo monitoreo el rendimiento?

**R:**

```bash
# Ver uso de memoria
top -p <PID>

# Ver threads
jps -l
jstack <PID>

# Ver heap memory
jmap -heap <PID>

# Agregar herramienta de monitoreo (Micrometer)
# Necesita spring-boot-starter-actuator
# Acceso: http://localhost:8080/actuator
```

---

## Otros

### P: ¿Cuál es la estructura de carpetas esperada?

**R:**

```
sigma-backend/
├── src/main/java/.../config/          # Configuraciones
├── src/main/java/.../controller/      # Controladores REST
├── src/main/java/.../dto/             # Data Transfer Objects
├── src/main/java/.../model/           # Entidades JPA
├── src/main/java/.../repository/      # Repositorios
├── src/main/java/.../service/         # Servicios (lógica)
├── src/main/java/.../exception/       # Excepciones personalizadas
├── src/main/resources/                # Configuración
├── src/test/                          # Tests
├── README.md                          # Descripción general
├── DOCUMENTACION_TECNICA.md           # Documentación técnica
├── GUIA_GIT.md                        # Guía de Git
├── INSTALACION_DEPLOYMENT.md          # Instalación y deployment
├── CONTRIBUCION.md                    # Guía de contribución
└── pom.xml                            # Dependencias Maven
```

---

### P: ¿Hay algún frontend incluido?

**R:** No. El backend es una API REST independiente.

El frontend debe ser desarrollado separadamente usando:
- React
- Angular
- Vue
- O cualquier otro framework

La comunicación es mediante requests HTTP con JSON.

---

### P: ¿Cuáles son las mejores prácticas en este proyecto?

**R:**

1. **Inyección de dependencias:** Usar constructor injection
2. **Servicios:** Toda lógica de negocio va aquí
3. **DTOs:** Transferir datos entre capas
4. **Validación:** Usar anotaciones `@Valid`
5. **Excepciones:** Lanzar excepciones específicas
6. **Testing:** Write tests para funcionalidad crítica
7. **Documentación:** Mantener README actualizado

---

### P: ¿Cómo contacto para reportar bugs?

**R:**

1. Crear un **Issue en GitHub** con:
   - Descripción clara del problema
   - Pasos para reproducir
   - Stack trace o mensaje de error
   - Información del sistema

2. O enviar email al mantenedor

---

## Créditos

Documento creado: **22 de febrero de 2026**
Última actualización: **Versión 1.0**

Para más información, consulta los otros documentos:
- [README.md](README.md)
- [DOCUMENTACION_TECNICA.md](DOCUMENTACION_TECNICA.md)
- [GUIA_GIT.md](GUIA_GIT.md)
- [INSTALACION_DEPLOYMENT.md](INSTALACION_DEPLOYMENT.md)
- [CONTRIBUCION.md](CONTRIBUCION.md)
