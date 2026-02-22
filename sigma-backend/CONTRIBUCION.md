# Guía de Contribución - Sigma Backend

## Código de Conducta de Colaboradores

### Nuestro Compromiso

En el proyecto Sigma, nos comprometemos a proporcionar un entorno acogedor e inclusivo para todos. Esperamos que todos los colaboradores respeten los derechos y dignidad de otros miembros de la comunidad.

### Comportamiento Esperado

- Utilizar lenguaje respetuoso e inclusivo
- Ser receptivo a críticas constructivas
- Enfocarse en lo que es mejor para la comunidad
- Mostrar empatía hacia otros miembros

### Comportamiento Inaceptable

- Lenguaje o imágenes sexuales
- Ataques personales o insultos
- Acoso público o privado
- Publicar información privada sin consentimiento
- Otra conducta que sea inapropiada para un entorno profesional

---

## Proceso de Contribución

### 1. Reportar Bugs

#### Antes de Reportar

- Verificar los issues existentes
- Actualizar a la última versión
- Recolectar información de debug

#### Cómo Reportar

```markdown
### Descripción del Bug
Breve descripción del problema

### Para Reproducir
Pasos para reproducir:
1. ...
2. ...
3. ...

### Comportamiento Esperado
Qué debería suceder

### Comportamiento Actual
Qué sucede en realidad

### Capturas de Pantalla
Si es aplicable

### Información del Sistema
- OS: Windows 10
- Java Version: 21.0.1
- Spring Boot: 3.5.7
- Base de Datos: SQL Server 2022

### Logs o Mensajes de Error
```

### 2. Sugerir Mejoras

```markdown
### Descripción de la Mejora
Descripción clara de la mejora

### Motivación
Por qué sería útil

### Implementación Propuesta
Cómo se podría implementar

### Beneficios Adicionales
Otros beneficios de esta mejora
```

### 3. Pull Requests

#### Antes de Empezar

```bash
# 1. Fork el repositorio en GitHub

# 2. Clonar tu fork
git clone https://github.com/TU_USUARIO/sigma-backend.git

# 3. Crear rama de feature
git checkout -b feature/nombre-descriptivo develop

# 4. Instalar dependencias
mvnw.cmd clean install
```

#### Desarrollo

```bash
# 1. Hacer cambios en tu rama
# 2. Seguir estándares de código (ver abajo)
# 3. Ejecutar tests locales
mvnw.cmd test

# 4. Verificar compilación
mvnw.cmd clean package

# 5. Hacer commits descriptivos
git add .
git commit -m "feat: Descripción del cambio"
```

#### Enviar Pull Request

```bash
# 1. Actualizar rama con cambios remotos
git fetch origin
git rebase origin/develop

# 2. Push a tu fork
git push origin feature/nombre-descriptivo

# 3. En GitHub: Create Pull Request
# - Comparar tu rama con develop del repo original
# - Llenar template de PR
# - Esperar revisión
```

#### Template para PR

```markdown
## Descripción
Descripción clara del cambio

## Tipo de Cambio
- [ ] Bug fix (cambio no rompedor que arregla un issue)
- [ ] Característica nueva (cambio no rompedor que agrega funcionalidad)
- [ ] Cambio rompedor (fix o feature que causa cambios no compatibles)
- [ ] Cambio de documentación

## Relacionado con Issue
Fixes #(issue_number)

## Cambios Realizados
- Cambio 1
- Cambio 2
- Cambio 3

## Cómo Probar
Pasos para validar:
1. ...
2. ...

## Checklist
- [ ] Mi código sigue el estilo de código del proyecto
- [ ] He actualizado la documentación
- [ ] Agregué tests para el nuevo código
- [ ] Todos los tests pasan localmente
- [ ] No hay conflictos con develop
```

---

## Estándares de Código

### Convenciones de Nombres

**Clases:**
```java
// PascalCase
public class PacienteController { }
public class CitaMedicaService { }
public class AnalisisClinicoDTO { }
```

**Métodos y Variables:**
```java
// camelCase
public ResponseEntity<DashboardPacienteDTO> obtenerDatosDashboard() { }
private String nombreCompleto;
```

**Constantes:**
```java
// UPPER_SNAKE_CASE
public static final int TIMEOUT_SEGUNDOS = 30;
public static final String APP_VERSION = "1.0.0";
```

**Paquetes:**
```java
// lowercase con puntos
com.sigma.sigma_backend.controller
com.sigma.sigma_backend.service
com.sigma.sigma_backend.dto
```

### Formato de Código

#### Indentación
- Usar 4 espacios (no tabs)
- Configurar IDE apropiadamente

#### Longitud de Línea
- Máximo 120 caracteres
- Si es más, quebrar a nueva línea

#### Formato de Clase

```java
package com.sigma.sigma_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// ... otros imports en orden alfabético

/**
 * Servicio para gestión de pacientes.
 * Maneja operaciones CRUD y lógica relacionada.
 */
@Service
@Transactional
public class PacienteService {
    
    private final PacienteRepository pacienteRepository;
    
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }
    
    /**
     * Obtiene un paciente por su email.
     * 
     * @param email Email del paciente
     * @return Paciente encontrado
     * @throws UsernameNotFoundException Si no existe
     */
    public Paciente obtenerPorEmail(String email) {
        return pacienteRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Paciente no encontrado"));
    }
}
```

### Ejemplo de Método Bien Escrito

```java
/**
 * Crea una nueva cita médica validando disponibilidad.
 * 
 * @param citaDTO Datos de la cita a crear
 * @return CitaMedica creada y persistida
 * @throws BusinessException Si el obstetra no está disponible
 * @throws ValidationException Si los datos son inválidos
 */
@Transactional(rollbackFor = Exception.class)
public CitaMedica crearCita(CitaMedicaDTO citaDTO) {
    // Validar entrada
    if (citaDTO.getFechaCita().isBefore(LocalDateTime.now())) {
        throw new ValidationException("La cita debe ser en el futuro");
    }
    
    // Verificar disponibilidad
    boolean disponible = verificarDisponibilidad(
        citaDTO.getObstetraId(),
        citaDTO.getFechaCita()
    );
    if (!disponible) {
        throw new BusinessException("Obstetra no disponible en esa fecha");
    }
    
    // Crear y persistir
    CitaMedica citaMedica = new CitaMedica();
    citaMedica.setPaciente(pacienteRepository.findById(citaDTO.getPacienteId()).orElseThrow());
    citaMedica.setObstetra(obstetraRepository.findById(citaDTO.getObstetraId()).orElseThrow());
    citaMedica.setFechaCita(citaDTO.getFechaCita());
    citaMedica.setMotivo(citaDTO.getMotivo());
    citaMedica.setEstado(EstadoCita.PROGRAMADA);
    
    return citaMedicaRepository.save(citaMedica);
}
```

### Uso de Anotaciones

```java
// Usar @RequiredArgsConstructor de Lombok para inyección
@Service
@RequiredArgsConstructor
public class ServicioEjemplo {
    private final RepositorioEjemplo repositorio;
    private final OtroServicio otroServicio;
    
    // Constructor se genera automáticamente
}

// Usar @Transactional para transacciones
@Transactional
public void operacionCritica() { }

// Usar @PreAuthorize para seguridad
@GetMapping("/datos-sensibles")
@PreAuthorize("hasAuthority('ROL_ADMIN')")
public ResponseEntity<Object> datosAdmin() { }
```

---

## Testing

### Requerimientos de Tests

- **Cobertura mínima:** 80%
- **Tests unitarios:** Para servicios
- **Tests de integración:** Para controladores
- **Tests de seguridad:** Para endpoints protegidos

### Estructura de Test

```java
@SpringBootTest
@AutoConfigureMockMvc
class PacienteServiceTest {
    
    @MockBean
    private PacienteRepository pacienteRepository;
    
    @InjectMocks
    private PacienteService pacienteService;
    
    @Test
    void testObtenerPacienteExitoso() {
        // Arrange
        Paciente paciente = new Paciente();
        paciente.setEmail("test@example.com");
        when(pacienteRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(paciente));
        
        // Act
        Paciente resultado = pacienteService.obtenerPorEmail("test@example.com");
        
        // Assert
        assertNotNull(resultado);
        assertEquals("test@example.com", resultado.getEmail());
        verify(pacienteRepository, times(1)).findByEmail("test@example.com");
    }
    
    @Test
    void testObtenerPacienteNoExiste() {
        // Arrange
        when(pacienteRepository.findByEmail("noexiste@example.com"))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(
            UsernameNotFoundException.class,
            () -> pacienteService.obtenerPorEmail("noexiste@example.com")
        );
    }
}
```

### Ejecutar Tests

```bash
# Todos los tests
mvnw.cmd test

# Test específico
mvnw.cmd test -Dtest=PacienteServiceTest

# Con cobertura
mvnw.cmd clean test jacoco:report

# Ver reporte de cobertura
# Abrir: target/site/jacoco/index.html
```

---

## Documentación

### Comentarios JavaDoc

```java
/**
 * Obtiene el dashboard personalizado para un paciente.
 * 
 * Carga información de:
 * - Datos básicos del paciente
 * - Próximas citas médicas
 * - Historial clínico reciente
 * - Últimos análisis realizados
 * 
 * @param email Email del paciente
 * @return {@link DashboardPacienteDTO} con datos del dashboard
 * @throws UsernameNotFoundException Si el paciente no existe
 * @throws SecurityException Si el usuario no tiene permiso
 * 
 * @see DashboardPacienteDTO
 * @see PacienteService#obtenerPorEmail(String)
 * 
 * @since 1.0
 */
public DashboardPacienteDTO obtenerDatosDashboard(String email) {
    // Implementación
}
```

### Documentando Cambios

En el archivo correspondiente o en README:

```markdown
## Versión 1.1.0 (2026-02-28)

### Características Nuevas
- Agregar filtro por fecha en historial clínico
- Implementar búsqueda de obstetras por especialidad

### Bugs Corregidos
- Corregir validación de email duplicado en registro
- Solucionar error 500 al cancelar cita sin motivo

### Cambios de Breaking
- Cambiar formato de respuesta getSueldoObstetra()

### Deprecado
- Método obtenerCitasPorMes() fue reemplazado por obtenerCitasPorFecha()
```

---

## Flujo de Revisión de Código

### Lo que Revisamos

1. **Funcionalidad:**
   - El código implementa correctamente lo solicitado
   - No rompe funcionalidad existente
   - Maneja casos edge

2. **Calidad:**
   - Nombres claros y descriptivos
   - Código DRY (Don't Repeat Yourself)
   - Sin código muerto
   - Complejidad razonable

3. **Testing:**
   - Tests apropriados
   - Buena cobertura
   - Tests significativos

4. **Documentación:**
   - Código autoexplicativo
   - JavaDoc donde sea necesario
   - README actualizado

5. **Seguridad:**
   - Validación de entrada
   - No expone información sensible
   - JWT tokens manejados correctamente

### Feedback Constructivo

El feedback está hecho para mejorar, no para criticar personalmente:

```
✅ BUENO: "Considera usar ResponseEntity para manejar diferentes tipos de respuesta"
❌ MALO: "Este código es horrible"

✅ BUENO: "Podríamos extraer esta lógica a un método separado para reutilizarla"
❌ MALO: "Por qué copiaste este código"
```

---

## Checklist para Contribuidores

Antes de enviar un PR:

### Código

- [ ] El código sigue las convenciones de nomenclatura
- [ ] No hay código duplicado
- [ ] Métodos tienen responsabilidad única
- [ ] No hay variables no utilizadas
- [ ] Manejo adecuado de excepciones

### Tests

- [ ] Tests unitarios pasan (`mvnw.cmd test`)
- [ ] Cobertura > 80%
- [ ] Tests describen claramente qué prueban
- [ ] Casos edge cubiertos

### Documentación

- [ ] JavaDoc para métodos públicos
- [ ] README actualizado si es necesario
- [ ] Cambios importantes documentados
- [ ] Ejemplos agregados si es necesario

### Seguridad

- [ ] Sin credenciales en código
- [ ] Validaciones de entrada presentes
- [ ] Anotaciones `@PreAuthorize` donde necesario
- [ ] Sin SQL injection
- [ ] Contraseñas hasheadas apropriadamente

### Compilación

- [ ] `mvnw.cmd clean package` compila sin errores
- [ ] No hay warnings (o están justificados)
- [ ] Se ejecuta correctamente localmente

### Git

- [ ] Rama creada desde develop actualizado
- [ ] Commits son atómicos y descriptivos
- [ ] No incluye commits de otra rama
- [ ] Sin conflictos con develop

---

## Recursos Útiles

### Documentación Spring
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)

### Maven
- [Maven Documentation](https://maven.apache.org/)
- [Maven Plugins](https://maven.apache.org/plugins/index.html)

### SQL Server
- [SQL Server Documentation](https://docs.microsoft.com/en-us/sql/sql-server/)
- [Transact-SQL Reference](https://docs.microsoft.com/en-us/sql/t-sql/language-reference)

### Git
- [Git Documentation](https://git-scm.com/doc)
- [GitHub Guides](https://guides.github.com/)

---

## Preguntas Frecuentes

### ¿Cómo sé qué trabajar?

Revisa la lista de issues en GitHub:
- Busca issues etiquetado con `good first issue`
- Comenta que quieres trabajar en un issue
- Espera confirmación antes de empezar

### ¿Cuánto tiempo tarda la revisión?

Típicamente:
- 24-48 horas para revisión inicial
- Puede requerir cambios adicionales
- Se integra después de aprobación

### ¿Qué pasa si mi PR no es aceptado?

No es personal. Los revisores proporcionarán razones:
- Puedes hacer cambios sugeridos
- O puedo aceptar la feedback para futuras contribuciones
- Siempre es una oportunidad de aprendizaje

### ¿Puedo contribuir documentación?

¡Por supuesto! La documentación es tan valiosa como el código:
- Reportar errores en documentación
- Sugerir mejoras en ejemplos
- Traducir a otros idiomas
- Agregar tutoriales

---

## Contacto

Para preguntas sobre contribuciones:
- Abrir un issue con la etiqueta `question`
- Comentar en PR relacionado
- Contactar a los maintainers

---

Gracias por contribuir a Sigma Backend.

**Documento actualizado:** 22 de febrero de 2026
**Última versión:** 1.0
