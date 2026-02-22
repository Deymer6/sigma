# Guía de Git - Sigma Backend

## Preparación Antes de Subir a Git

### 1. Crear archivo .gitignore

Este archivo debe estar en la raíz del proyecto para excluir archivos que no deben subirse a Git.

```
# Maven
target/
.m2/
*.jar
*.war

# IDE
.idea/
.vscode/
*.swp
*.swo
*.iml
.project
.classpath
.settings/
*.log

# System
.DS_Store
Thumbs.db

# Compilados
*.class

# Archivo de propiedades con credenciales sensibles (si tienes)
# Descomenta si usas archivo de propiedades externo
# application-prod.properties

# Uploads (opcional, si es muy grande)
uploads/

# Node (si usas npm)
node_modules/

# Gradle (si lo usas)
.gradle/
gradlew
gradlew.bat
gradle/

# Temporal
*.tmp
*.temp
```

### 2. Crear archivo .gitattributes

Para consistencia en saltos de línea:

```
* text=auto
*.java text eol=lf
*.xml text eol=lf
*.properties text eol=lf
*.md text eol=lf
*.bat text eol=crlf
*.sh text eol=lf
```

### 3. Verificar archivos sensibles

Asegúrate que `application.properties` NO contiene:
- Contraseñas reales (usa variables de entorno)
- Claves API privadas
- Tokens secretos

**Alternativa segura: Crear `application-local.properties`**

```properties
# application-local.properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;
spring.datasource.username=sa
spring.datasource.password=admin123
```

Luego agregar a `.gitignore`:
```
application-local.properties
```

---

## Comandos Git Básicos

### Inicializar Repositorio

```bash
cd sigma-backend
git init
git config user.name "Tu Nombre"
git config user.email "tu@email.com"
```

### Agregar Archivos al Staging

```bash
# Agregar todos los archivos
git add .

# Agregar solo cambios específicos
git add src/
git add README.md
git add DOCUMENTACION_TECNICA.md

# Ver estado
git status
```

### Crear Primer Commit

```bash
git commit -m "docs: Documentación inicial del proyecto Sigma Backend

- README.md con descripción general
- DOCUMENTACION_TECNICA.md con detalles arquitectónicos
- .gitignore para excluir archivos innecesarios
- Estructura del proyecto documentada"
```

### Conectar con Repositorio Remoto

```bash
# Para GitHub (reemplaza con tu URL)
git remote add origin https://github.com/tu-usuario/sigma-backend.git

# Verificar remoto agregado
git remote -v

# Establecer rama principal
git branch -M main

# Subir a GitHub
git push -u origin main
```

---

## Estructura de Commits Recomendada

Usar formato de commit convencional:

```
<tipo>: <descripción corta>

<descripción detallada opcional>

<referencias a issues opcionales>
```

**Tipos comunes:**
- `feat:` - Nueva característica
- `fix:` - Corrección de bug
- `docs:` - Cambios de documentación
- `style:` - Cambios de formato (no cambian lógica)
- `refactor:` - Refactorización de código
- `perf:` - Mejoras de rendimiento
- `test:` - Agregar o actualizar tests
- `chore:` - Cambios de configuración, dependencias

**Ejemplos:**

```bash
git commit -m "feat: Agregar endpoint de dashboard para pacientes"

git commit -m "fix: Corregir validación de email en registro

- Validación case-insensitive para emails
- Mensaje de error más descriptivo
- Fixes #123"

git commit -m "docs: Actualizar README con instrucciones de instalación"

git commit -m "chore: Actualizar versión de Spring Boot a 3.5.7"
```

---

## Ramas (Branches) Recomendadas

```
main/master
├── production (rama productiva)
│
develop
├── feature/auth-jwt
├── feature/dashboard-paciente
├── feature/citas-medicas
├── bugfix/validacion-email
└── hotfix/security-issue
```

### Crear y Cambiar de Ramas

```bash
# Crear rama desde develop
git checkout -b feature/nueva-caracteristica develop

# Hacer cambios y commits
git add .
git commit -m "feat: Nueva característica implementada"

# Cambiar a rama develop
git checkout develop

# Actualizar develop con cambios remotos
git pull origin develop

# Hacer merge de la rama feature
git merge feature/nueva-caracteristica

# Subir cambios a remoto
git push origin develop

# Eliminar rama local después de merged
git branch -d feature/nueva-caracteristica

# Eliminar rama remota
git push origin --delete feature/nueva-caracteristica
```

---

## Workflow Git Recomendado (Git Flow)

### Desarrollo de Nueva Característica

```bash
# 1. Partir desde develop actualizado
git checkout develop
git pull origin develop

# 2. Crear rama de característica
git checkout -b feature/nombre-caracteristica

# 3. Hacer cambios y commits
git add .
git commit -m "feat: Descripción del cambio"

# 4. Subir rama al remoto
git push -u origin feature/nombre-caracteristica

# 5. Crear Pull Request en GitHub
# (desde GitHub: Compare & pull request)

# 6. Después de aprobación, hacer merge desde GitHub o:
git checkout develop
git pull origin develop
git merge feature/nombre-caracteristica
git push origin develop
```

### Correción de Bug (Hotfix)

```bash
# 1. Partir desde main
git checkout main
git pull origin main

# 2. Crear rama de hotfix
git checkout -b hotfix/nombre-del-bug

# 3. Hacer cambios
git add .
git commit -m "fix: Descripción del bug arreglado"

# 4. Subir y hacer merge a main y develop
git push -u origin hotfix/nombre-del-bug
# (Crear PR... después de merge)

# 5. Cambiar a main y actualizar
git checkout main
git pull origin main

# 6. Actualizar también develop
git checkout develop
git pull origin develop
git merge hotfix/nombre-del-bug
git push origin develop
```

---

## Comandos Útiles

### Ver Historial de Commits

```bash
# Ver últimos commits
git log --oneline -10

# Ver commits de una rama específica
git log --oneline main

# Ver commits con detalles
git log --graph --oneline --all

# Ver cambios en un commit específico
git show <hash-del-commit>
```

### Deshacer Cambios

```bash
# Deshacer cambios no staged
git restore <archivo>

# Deshacer cambios staged (sin perder cambios)
git restore --staged <archivo>

# Deshacer último commit (mantener cambios)
git reset --soft HEAD~1

# Deshacer último commit (perder cambios)
git reset --hard HEAD~1

# Revertir commit específico
git revert <hash-del-commit>
```

### Stashing (Guardar Cambios Temporalmente)

```bash
# Guardar cambios sin commitear
git stash

# Ver stashes guardados
git stash list

# Recuperar último stash
git stash pop

# Recuperar stash específico
git stash pop stash@{0}

# Eliminar stash
git stash drop
```

### Sincronizar con Remoto

```bash
# Traer cambios sin mergear
git fetch origin

# Traer y mergear cambios
git pull origin <rama>

# Actualizar rama local con remota
git pull --rebase origin <rama>

# Subir commits locales
git push origin <rama>

# Forzar push (usar con cuidado)
git push origin <rama> --force-with-lease
```

---

## Issues y Pull Requests en GitHub

### Crear un Issue

1. Ir a GitHub → Issues → New Issue
2. Título descriptivo (ej: "Bug: Error al crear cita")
3. Descripción detallada
4. Asignar etiquetas (bug, feature, documentation)
5. Asignar a desarrollador

### Crear un Pull Request

1. Completar desarrollo en rama feature
2. Hacer git push de la rama
3. GitHub detecta cambios → "Compare & pull request"
4. Descripción clara del PR:
   - ¿Qué cambia?
   - ¿Por qué?
   - Cómo probar?
   - Issues relacionados (#123)

### Template para PR

```markdown
## Descripción
Breve descripción de los cambios

## Tipo de Cambio
- [ ] Bug fix
- [ ] Nueva característica
- [ ] Cambio que rompe compatibilidad
- [ ] Cambio de documentación

## Cambios Realizados
- Cambio 1
- Cambio 2

## Cómo Probar
Pasos para validar los cambios:
1. ...
2. ...

## Screenshots
Si es relevante, adjuntar screenshots

## Checklist
- [ ] Mi código está documentado
- [ ] He ejecutado tests locales
- [ ] No hay conflictos con develop
- [ ] Cambios están listos para producción
```

---

## Configuración Recomendada Local

### archivo .git/config local

```bash
git config user.name "Tu Nombre"
git config user.email "tu@email.com"
git config core.autocrlf true  # Windows
git config core.fileMode false # Windows
git config credential.helper store # Guardar credenciales
```

### Alias Útiles

```bash
git config --global alias.st status
git config --global alias.co checkout
git config --global alias.cmd commit
git config --global alias.br branch
git config --global alias.unstage 'restore --staged'
git config --global alias.last 'log -1 HEAD'
git config --global alias.log10 'log --oneline -10'
git config --global alias.graph 'log --graph --oneline --all'
```

---

## Solución de Problemas Comunes

### Conflictos de Merge

```bash
# Cuando hay conflictos
git status # Ver archivos en conflicto

# Editar manualmente los archivos
# Buscar: <<<<<<<, =======, >>>>>>>

# Después de resolver:
git add .
git commit -m "Merge: Resolver conflictos en feature/xxx"
git push origin develop
```

### Rama con Cambios Divergentes

```bash
# Rebase para mantener historial linear
git fetch origin
git rebase origin/develop

# Si hay conflictos
git rebase --continue # Después de resolver

# Si quieres abortar
git rebase --abort
```

### Deshacer Push a Remoto

```bash
# Ver último push
git log --oneline -5

# Resetear a commit anterior
git reset --hard <hash>

# Forzar push (usar con cuidado)
git push origin main --force-with-lease
```

---

## Buenas Prácticas

1. **Commits pequeños:** Cambios relacionados, fáciles de revertir
2. **Mensajes descriptivos:** Incluir por qué, no solo qué
3. **No commitear secretos:** Usar `.gitignore` y variables de entorno
4. **Sincronizarse frecuentemente:** `git pull` antes de hacer cambios
5. **Revisar código:** Code reviews antes de merge a main
6. **Proteger rama main:** Requiere PR reviews
7. **Tags para versiones:** `git tag -a v1.0.0 -m "Versión 1.0.0"`
8. **No reescribir historia pública:** `git push --force` solo en ramas personales

---

## Archivo de Configuración Recomendado: .env (si usas variables)

```properties
# No commitear este archivo, agregar a .gitignore
DATABASE_URL=jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;
DATABASE_USER=sa
DATABASE_PASSWORD=admin123
JWT_SECRET=tu-secreta-jwt-muy-larga
ENVIRONMENT=development
```

---

## Primeros Pasos para Subir el Proyecto

```bash
# 1. Crear repositorio en GitHub
# (GitHub → New Repository → sigma-backend)

# 2. En tu terminal
cd C:\Users\GRLL\Documents\CICLO\ VIII\....\sigma-backend

# 3. Inicializar git local
git init
git config user.name "Tu Nombre"
git config user.email "tu@email.com"

# 4. Crear .gitignore (copia el contenido arriba)

# 5. Agregar archivos
git add .
git status

# 6. Primer commit
git commit -m "Initial commit: Sigma Backend Project

- Estructura base con Spring Boot 3.5.7
- Autenticación con JWT
- Gestión de citas, análisis y historiales clínicos
- Roles de usuario (Paciente, Obstetra, Admin)"

# 7. Conectar con GitHub
git remote add origin https://github.com/TU_USUARIO/sigma-backend.git
git branch -M main
git push -u origin main

# 8. ¡Listo! El proyecto está en GitHub
```

---

Documento creado: 22 de febrero de 2026
Última actualización: Versión 1.0
