# 🔐 SETUP SEGURO - Sigma Backend

## Manejar Credenciales de Forma Segura

Este documento explica cómo trabajar con credenciales sensibles sin comprometer la seguridad.

---

## ⚠️ NUNCA HAGAS ESTO

❌ **NO subas a Git:**
- `.env.local` (credenciales locales)
- `application.properties` con contraseñas reales
- `secrets.yml`
- Archivos con tokens o API keys

❌ **NO hardcodees credenciales** en el código

❌ **NO compartas** .env.local por email o chat

---

## ✅ MÉTODO CORRECTO

### Opción 1: Usar Variables de Entorno (RECOMENDADO)

#### Paso 1: Crear `.env.local` en la raíz del proyecto

```bash
# .env.local (NO se sube a Git)
DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;
DATASOURCE_USERNAME=sa
DATASOURCE_PASSWORD=tu_contraseña_real
```

#### Paso 2: Verificar que `.gitignore` incluye estos archivos

El `.gitignore` ya contiene:
```
.env
.env.local
.env.*.local
application-local.properties
```

#### Paso 3: Ejecutar con variables de entorno

**Windows PowerShell:**
```powershell
$env:DATASOURCE_URL = "jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;"
$env:DATASOURCE_USERNAME = "sa"
$env:DATASOURCE_PASSWORD = "admin123"
./mvnw.cmd spring-boot:run
```

**Windows Command Prompt:**
```batch
set DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;
set DATASOURCE_USERNAME=sa
set DATASOURCE_PASSWORD=admin123
mvnw.cmd spring-boot:run
```

**Linux/macOS:**
```bash
export DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;
export DATASOURCE_USERNAME=sa
export DATASOURCE_PASSWORD=admin123
./mvnw spring-boot:run
```

### Opción 2: Crear `application-local.properties`

#### Paso 1: Crear archivo local

```bash
# src/main/resources/application-local.properties
# Este archivo contiene configuración SOLO para desarrollo local
# NO se sube a Git (está en .gitignore)

spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;
spring.datasource.username=sa
spring.datasource.password=admin123

spring.jpa.show-sql=true
logging.level.root=DEBUG
```

#### Paso 2: Ejecutar con perfil local

```bash
# Windows
mvnw.cmd spring-boot:run --spring.profiles.active=local

# Linux/macOS
./mvnw spring-boot:run --spring.profiles.active=local
```

### Opción 3: Archivo application.properties compartido (RECOMENDADO)

**application.properties (se sube a Git):**
```properties
# Usa valores por defecto seguros o variables
spring.datasource.url=${DATASOURCE_URL:jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;}
spring.datasource.username=${DATASOURCE_USERNAME:sa}
spring.datasource.password=${DATASOURCE_PASSWORD:}
```

Los `${}` son **placeholders** que se reemplazan por variables de entorno.

---

## 🔄 Clonar el Proyecto en Otra Máquina

### Paso 1: Clonar desde GitHub

```bash
git clone https://github.com/tu-usuario/sigma-backend.git
cd sigma-backend
```

### Paso 2: Crear la base de datos

```sql
-- Abrir SQL Server Management Studio

-- 1. Crear base de datos
CREATE DATABASE SIGMA;
GO

-- 2. Crear usuario (opcional, si no usas 'sa')
CREATE LOGIN sigma_user WITH PASSWORD = 'TuContraseña123!';
GO

USE SIGMA;
CREATE USER sigma_user FOR LOGIN sigma_user;
ALTER ROLE db_owner ADD MEMBER sigma_user;
GO
```

### Paso 3: Configurar variables locales

**Para esa nueva máquina, crear `.env.local` con SUS credenciales:**

```bash
# .env.local en la raíz del proyecto
DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;
DATASOURCE_USERNAME=sigma_user
DATASOURCE_PASSWORD=TuContraseña123
```

**O usar variables de entorno:**

```powershell
# PowerShell
$env:DATASOURCE_PASSWORD = "TuContraseña123"
```

### Paso 4: Instalar y ejecutar

```bash
# Instalar dependencias
./mvnw clean install

# Ejecutar con variables
./mvnw spring-boot:run
```

### ✅ LISTO - No requiere cambiar nada en el código

---

## 📊 Comparación de Métodos

| Método | Seguridad | Facilidad | Recomendación |
|--------|-----------|----------|---------------|
| **Variables de Entorno** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | **✅ MEJOR** |
| **application-local.properties** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Buena |
| **Hardcoded en código** | ❌ Peligro | ⭐⭐ | NUNCA |

---

## 🗄️ ¿Qué hacer con la Base de Datos?

### ❌ NO SUBIR

- Archivos `.bak` (backups)
- Archivos `.mdf` (data files)
- Archivos `.ldf` (log files)
- Dumps completos de datos

### ✅ SÍ SUBIR

Scripts SQL para inicializar:

```sql
-- seed-data.sql (o crear en carpeta scripts/)
CREATE DATABASE SIGMA;
GO

USE SIGMA;

-- Las tablas se crean automáticamente con JPA/Hibernate
-- Pero puedes crear un script opcional para datos iniciales

INSERT INTO roles (nombre) VALUES ('ROL_PACIENTE');
INSERT INTO roles (nombre) VALUES ('ROL_OBSTETRA');
INSERT INTO roles (nombre) VALUES ('ROL_ADMIN');
```

**Almacenar en:**
```
sigma-backend/
├── scripts/
│   ├── create-database.sql
│   ├── seed-data.sql
│   └── backups/
└── README.md
```

---

## 📁 Estructura de Archivos (Archivos Sensibles)

```
sigma-backend/
├── .env.local              ❌ NO a Git (credenciales locales)
├── .env.example            ✅ SÍ a Git (template vacío)
├── application-local.properties   ❌ NO a Git (credenciales)
├── application.properties  ✅ SÍ a Git (con variables ${})
│
├── scripts/
│   ├── create-database.sql     ✅ SÍ a Git
│   └── seed-data.sql           ✅ SÍ a Git
│
└── .gitignore             ✅ SÍ a Git (ya contiene .env*)
```

---

## 🔑 Variables de Entorno Disponibles

```properties
# Base de Datos
DATASOURCE_URL              Base de datos
DATASOURCE_USERNAME         Usuario
DATASOURCE_PASSWORD         Contraseña

# Servidor
SERVER_PORT                 Puerto (default: 8080)

# JWT (futuro)
JWT_SECRET                  Clave secreta
JWT_EXPIRATION              Expiración en ms (default: 86400000)

# Logging
LOGGING_LEVEL_ROOT          Nivel de logs
LOGGING_LEVEL_COM_SIGMA     Logs específicos

# Application
SPRING_PROFILES_ACTIVE      Perfil activo (dev, prod, test)
```

---

## 🚀 Ejemplo Completo: De 0 a 100

### Máquina 1 (Tu máquina - DESARROLLO)

```bash
# 1. Clonar proyecto
git clone https://github.com/tu-usuario/sigma-backend.git

# 2. Entrar a la carpeta
cd sigma-backend

# 3. Crear .env.local CON TUS credenciales (NUNCA se sube)
# Contenido:
# DATASOURCE_PASSWORD=admin123

# 4. Ejecutar
./mvnw spring-boot:run

# 5. Commit (NO incluye .env.local)
git add .
git commit -m "feat: Nueva característica"
git push origin main
```

### Máquina 2 (Otra computadora o servidor)

```bash
# 1. Clonar (mismo repositorio)
git clone https://github.com/tu-usuario/sigma-backend.git

# 2. Entrar
cd sigma-backend

# 3. Crear base de datos en SQL Server (DIFERENTE a la máquina 1)
# Ejecutar: CREATE DATABASE SIGMA;

# 4. Crear .env.local CON LAS CREDENCIALES DE ESTA MÁQUINA
# Contenido (diferentes credenciales):
# DATASOURCE_PASSWORD=otraContraseña123

# 5. Ejecutar
./mvnw spring-boot:run

# Los cambios de Máquina 1 se traen con:
git pull origin main
```

---

## 🛡️ Checklist de Seguridad

Antes de hacer `git push`:

- [ ] `.env.local` NO está commiteado
- [ ] `application-local.properties` NO está commiteado
- [ ] No hay contraseñas hardeadas en el código
- [ ] `.gitignore` contiene estos archivos
- [ ] Variables en `application.properties` usan `${}
- [ ] Los ejemplos tienen valores seguros

**Verificar:**
```bash
git status  # No debe mostrar .env.local
git diff HEAD~1  # No debe tener credenciales
```

---

## 📱 Para Equipos / Colaboradores

### 1. En GitHub

```markdown
## Setup Local

1. Clonar repositorio
2. Crear `.env.local` (ver `.env.example`)
3. Configurar variables:
   - DATASOURCE_PASSWORD
   - DATASOURCE_USERNAME (si es diferente)
4. Ejecutar: ./mvnw spring-boot:run
```

### 2. Cada colaborador hace:

```bash
# Cada persona en su máquina
cp .env.example .env.local
# Editar .env.local con SUS credenciales locales
nano .env.local

# Nunca subir a Git
git add -n .  # Ver qué se añadiría
git status    # Verificar que .env.local no aparece
```

---

## 🎯 Resumen

**Lo importante:**

✅ Variables de entorno para credenciales  
✅ `.env.local` nunca a Git  
✅ `.env.example` sí a Git (sin valores)  
✅ `application.properties` con `${}` placeholders  
✅ Cada máquina crea su propio `.env.local`  

**Beneficios:**

- 🔐 Seguridad (credenciales no en Git)
- 🔄 Colaboración (cada quién sus credenciales)
- 🚀 Deployment (fácil en producción)
- 👥 Equipos (sin conflictos de credenciales)

---

## ❓ Preguntas Comunes

**P: ¿Qué pasa si accidentalmente subo `.env.local`?**

```bash
# Borrar del historio de Git
git rm --cached .env.local
git commit -m "Remove .env.local from git history"

# Cambiar credenciales en BD (pueden estar expuestas)
# Crear nuevas contraseñas
```

**P: ¿Puedo usar la misma contraseña en todas las máquinas?**

No es recomendable. Mejor tener credenciales diferentes por entorno.

**P: ¿Se crea la BD automáticamente?**

No. Debes crear la base de datos `SIGMA` manualmente.  
Las tablas se crean automáticamente con JPA.

---

Documento creado: 22 de febrero de 2026
