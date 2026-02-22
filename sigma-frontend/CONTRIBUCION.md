# CONTRIBUCION.md - Guía de Contribución

## Bienvenido!

Gracias por tu interés en contribuir a **Sigma Frontend**. Este documento explica cómo participar en el desarrollo del proyecto de manera efectiva y consistente.

## Tabla de Contenidos

1. [Código de Conducta](#código-de-conducta)
2. [Cómo Empezar](#cómo-empezar)
3. [Flujo de Contribución](#flujo-de-contribución)
4. [Convenciones de Código](#convenciones-de-código)
5. [Commits y Mensajes](#commits-y-mensajes)
6. [Pull Requests](#pull-requests)
7. [Testing](#testing)
8. [Documentación](#documentación)
9. [Estándares de Calidad](#estándares-de-calidad)

---

## Código de Conducta

### Nuestros Valores

- **Respeto:** Tratar a todos con respeto y dignidad
- **Inclusión:** Valorar diferentes perspectivas y experiencias
- **Profesionalismo:** Mantener estándares altos de calidad
- **Colaboración:** Trabajar juntos para mejores resultados

### Comportamiento Esperado

- Usa lenguaje inclusivo
- Sé receptivo a crítica constructiva
- Mantén profesionalismo en comunicaciones
- Reporta comportamiento inapropiado

### Consecuencias

El incumplimiento del código de conducta puede resultar en:
- Advertencia
- Restricción de participación
- Expulsión del proyecto

---

## Cómo Empezar

### Requisitos Previos

1. Tener cuenta en GitHub
2. Tener Git instalado localmente
3. Tener Node.js 20.x y npm 10.x
4. Tener Angular CLI 20.2.2
5. Haber leído [README.md](README.md) y [DOCUMENTACION_TECNICA.md](DOCUMENTACION_TECNICA.md)

### Configurar Entorno Local

```bash
# 1. Fork el repositorio
# (Click en botón Fork en GitHub)

# 2. Clonar tu fork
git clone https://github.com/TU_USUARIO/sigma.git
cd sigma/sigma-frontend

# 3. Agregar upstream
git remote add upstream https://github.com/ORG_OFICIAL/sigma.git

# 4. Ver remotes
git remote -v
# origin  = tu fork
# upstream = repositorio oficial

# 5. Instalar dependencias
npm install

# 6. Crear rama de desarrollo
git checkout -b develop
git pull upstream develop
```

---

## Flujo de Contribución

### 1. Actualizar tu Fork

Antes de empezar a trabajar, actualiza tu código local:

```bash
# Cambiar a develop
git checkout develop

# Traer cambios del repositorio oficial
git pull upstream develop

# Pushear a tu fork
git push origin develop
```

### 2. Crear Rama de Feature

**Estructura de nombres:**

```
feature/*              - Nuevas features
bugfix/*               - Correcciones de bugs
refactor/*             - Refactorización de código
docs/*                 - Cambios de documentación
test/*                 - Agregar o mejorar tests
perf/*                 - Mejoras de performance
```

**Crear rama:**

```bash
# Ejemplo: Nueva feature para editar citas
git checkout -b feature/editar-citas

# Ejemplo: Fix de un bug
git checkout -b bugfix/validacion-formulario

# Ver ramas locales
git branch
```

### 3. Hacer Cambios

Implementa tu feature o fix:

```bash
# Ver cambios
git status

# Ver diferencias
git diff

# Ver cambios preparados
git diff --staged
```

### 4. Comprobar Tests

```bash
# Ejecutar todos los tests
npm test

# Tests de un archivo específico
ng test --include='**/editar-citas/**'

# Tests en modo watch
ng test --watch
```

### 5. Hacer Commits

Ver [Commits y Mensajes](#commits-y-mensajes) más abajo.

### 6. Push a tu Fork

```bash
# Pushear rama al origen
git push origin feature/editar-citas

# O si ya existe: 
git push -u origin feature/editar-citas
```

### 7. Crear Pull Request

1. Ir a GitHub
2. Click en "Compare & pull request"
3. Rellenar template de PR
4. Esperar revisión

---

## Convenciones de Código

### Estructura de Archivos

```
src/app/
├── features/
│   └── patient/
│       ├── pages/
│       │   ├── editar-citas/
│       │   │   ├── editar-citas.ts          ← Componente
│       │   │   ├── editar-citas.html        ← Template
│       │   │   └── editar-citas.scss        ← Estilos
│       │   └── lista-citas/
│       │       ├── lista-citas.ts
│       │       ├── lista-citas.html
│       │       └── lista-citas.scss
│       └── patient.routes.ts
├── core/
│   ├── services/
│   │   └── cita.service.ts                  ← Servicio
│   ├── guards/
│   │   └── auth.guard.ts
│   └── interceptors/
│       └── auth.interceptor.ts
└── shared/
    └── components/
        └── cita-card/
            ├── cita-card.ts
            ├── cita-card.html
            └── cita-card.scss
```

### Nomenclatura TypeScript

```typescript
// ✅ Componentes (PascalCase)
export class EditarCitasComponent { }

// ✅ Directivas (PascalCase)
export class HighlightDirective { }

// ✅ Servicios (PascalCase)
export class CitaService { }

// ✅ Interfaces (PascalCase con prefijo I opcional)
export interface Cita { }

// ✅ Constantes (UPPERCASE)
export const API_URL = 'http://localhost:8080/api';

// ✅ Variables/Funciones (camelCase)
const citaId = 1;
function calcularDuracion() { }

// ✅ Métodos privados (camelCase con #)
#cargarCitas() { }
```

### Nomenclatura de Archivos

```
// Componentes: nombre.component.ts
editar-citas.ts

// Servicios: nombre.service.ts
cita.service.ts

// Guards: nombre.guard.ts
auth.guard.ts

// Interceptores: nombre.interceptor.ts
auth.interceptor.ts

// Interfaces: nombre.interface.ts o nombre.model.ts
cita.interface.ts

// Rutas: nombre.routes.ts
patient.routes.ts
```

### Angular Style Guide

Seguimos [Google Angular Style Guide](https://angular.io/guide/styleguide):

```typescript
// ✅ Componentes pequeños y enfocados
@Component({
  selector: 'app-cita-item',
  standalone: true,
  imports: [CommonModule],
  template: `<div>{{ cita.fecha }}</div>`
})

// ✅ One class per file
// NO guardar múltiples clases en un archivo

// ✅ Usar OnDestroy correctamente
export class MyComponent implements OnDestroy {
  private destroy$ = new Subject<void>();
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

// ✅ Métodos en orden específico
export class MyComponent {
  @Input() data: any;
  @Output() saved = new EventEmitter();
  
  constructor() { }
  
  ngOnInit(): void { }
  
  ngOnDestroy(): void { }
  
  public method(): void { }
  
  private privateMethod(): void { }
}
```

### ESLint y Prettier

El proyecto tiene ESLint y Prettier configurado:

```bash
# Verificar linting
ng lint

# Auto-fix issues
ng lint --fix

# Prettier (auto-format)
npx prettier --write src/
```

---

## Commits y Mensajes

### Convención de Commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Tipos de Commit

```
feat:      Una nueva feature
fix:       Corrección de bug
docs:      Cambios en documentación
style:     Cambios que no afectan código (format, semicolons, etc)
refactor:  Código que ni agrega feature ni fix bugs
perf:      Mejora de performance
test:      Agregar o actualizar tests
ci:        Cambios en CI/CD
chore:     Cambios en build, dependencies, etc
```

### Ejemplos de Commits

```bash
# Feature nueva
git commit -m "feat(citas): agregar funcionalidad de editar cita"

# Bug fix
git commit -m "fix(auth): resolver token expiration issue"

# Documentación
git commit -m "docs: actualizar guía de instalación"

# Con scope y body
git commit -m "feat(citas): permitir cancelación de cita

- Agregar botón de cancelar en vista de citas
- Enviar confirmación por email
- Actualizar estado en BD"

# Commit que cierra issue
git commit -m "fix(validacion): resolver validación de formulario

Closes #123"
```

### Reglas para Mensajes

- ✅ Usar modo imperativo ("add" no "added")
- ✅ No capitalizar primera letra
- ✅ No incluir punto al final
- ✅ Máximo 50 caracteres en el subject
- ✅ Detallar en el body si es necesario

---

## Pull Requests

### Antes de Crear PR

- ✅ Actualizar rama desde upstream/develop
- ✅ Todos los tests pasan
- ✅ Código sigue las convenciones
- ✅ Documentación actualizada
- ✅ Branch tiene menos de 10-15 commits

### Template de Pull Request

```markdown
## Descripción
Breve descripción de los cambios.

## Tipo de Cambio
- [ ] Bug fix (corrección que no rompe funcionalidad existente)
- [ ] Feature (nueva funcionalidad)
- [ ] Breaking change (cambio que rompe funcionalidad existente)
- [ ] Documentación

## Cómo se Testea?
Describe los pasos para verificar tu cambio:
1. Ir a '...'
2. Click en '...'
3. Ver resultado '...'

## Checklist
- [ ] Mi código sigue las convenciones del proyecto
- [ ] He ejecutado los tests localmente
- [ ] He actualizado la documentación
- [ ] No hay warnings en la consola
- [ ] Mi rama está actualizada con develop

## Screenshots (si aplica)
[Agregar screenshots]

## Relacionado
Closes #123
```

### Proceso de Revisión

1. **Código Review:** Mínimo 2 approvals
2. **Tests:** Deben pasar
3. **Conflicts:** Resolver si hay
4. **Merge:** Squash and merge preferentemente

---

## Testing

### Escribir Tests

```typescript
// cita.service.spec.ts
describe('CitaService', () => {
  let service: CitaService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CitaService],
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(CitaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch citas', () => {
    const mockCitas = [
      { id: 1, fecha: new Date(), motivo: 'Test' }
    ];

    service.getCitas().subscribe(citas => {
      expect(citas.length).toBe(1);
      expect(citas[0].id).toBe(1);
    });

    const req = httpMock.expectOne('/api/citas');
    expect(req.request.method).toBe('GET');
    req.flush(mockCitas);
  });
});
```

### Coverage

```bash
# Generar coverage report
ng test --skip-initial build --code-coverage

# Mínimo requerido: 80%
```

---

## Documentación

### Comentar Código

```typescript
// ✅ Comentarios útiles
// Calcular duración de cita en minutos
const duracion = (fin - inicio) / 60000;

// ❌ Comentarios innecesarios
// Incrementar contador
contador++;

// JSDoc para métodos públicos
/**
 * Carga las citas del paciente.
 * @param pacienteId - ID del paciente
 * @returns Observable con array de citas
 * @throws {HttpErrorResponse} Si falla la petición
 */
public getCitasByPaciente(pacienteId: number): Observable<Cita[]> {
  return this.http.get<Cita[]>(`${this.API_URL}/${pacienteId}`);
}
```

### Actualizar Documentación

Si tu cambio afecta:
- Configuración → Actualizar [INSTALACION_DEPLOYMENT.md](INSTALACION_DEPLOYMENT.md)
- Nuevos servicios → Actualizar [DOCUMENTACION_TECNICA.md](DOCUMENTACION_TECNICA.md)
- Nuevas features → Actualizar [README.md](README.md)
- Problemas conocidos → Actualizar [FAQ.md](FAQ.md)

---

## Estándares de Calidad

### Pre-Commit Hooks (Opcional)

```bash
# Instalar husky
npm install husky --save-dev
npx husky install

# Crear hook pre-commit
cat > .husky/pre-commit << 'EOF'
#!/bin/sh
. "$(dirname "$0")/_/husky.sh"

echo "Running linter..."
npm run lint --fix

echo "Running tests..."
npm test -- --watch=false

echo "Checking commit message..."
EOF

chmod +x .husky/pre-commit
```

### Checklist Final

Antes de hacer merge:

- ✅ Tests: > 80% coverage
- ✅ Lint: Sin errores
- ✅ Build: Exitoso
- ✅ Performance: Sin degradación
- ✅ Accessibility: WCAG AA compliant
- ✅ Security: Sin vulnerabilidades
- ✅ Documentación: Actualizada

---

## Reportar Issues

### Reporte de Bugs

```markdown
**Describe el bug**
Una descripción clara de qué es el bug.

**Pasos para reproducir**
1. Ir a '...'
2. Click en '...'
3. Ver error '...'

**Comportamiento esperado**
Descripción de qué debería pasar.

**Comportamiento actual**
Qué está pasando.

**Screenshots**
[Si aplica]

**Entorno**
- OS: [e.g. Windows 10]
- Node: [e.g. 20.2.0]
- npm: [e.g. 10.2.0]
- Angular: [e.g. 20.2.0]
```

### Feature Requests

```markdown
**Es una enhancement?**
Descripción del enhancement deseado.

**Por qué es útil?**
Explicación del problema que resuelve.

**Solución propuesta**
Cómo se debería implementar.

**Alternativas consideradas**
Otras soluciones posibles.
```

---

## Comunicación

### Canales

- **Discusiones:** GitHub Discussions
- **Issues:** Para bugs y features
- **Pull Requests:** Para cambios de código
- **Email:** team@sigma.dev (para reportes sensibles)

### Esperado

- Responder en menos de 48 horas
- Sé respetuoso y profesional
- Proporciona contexto completo

---

## Recursos Útiles

- [Angular Style Guide](https://angular.io/guide/styleguide)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [RxJS Documentation](https://rxjs.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)

---

## Preguntas?

- Revisar [FAQ.md](FAQ.md)
- Leer [HELP.md](HELP.md)
- Abrir una discussion en GitHub
- Contactar al equipo

---

**¡Gracias por contribuir!** 🎉

Versión: 1.0.0 | Fecha: 22 de febrero de 2026
