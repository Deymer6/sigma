# Guía de Instalación y Deployment - Sigma Backend

## Tabla de Contenidos

1. [Instalación Local](#instalación-local)
2. [Configuración de Base de Datos](#configuración-de-base-de-datos)
3. [Ejecución de la Aplicación](#ejecución-de-la-aplicación)
4. [Deployment en Producción](#deployment-en-producción)
5. [Monitoreo y Logs](#monitoreo-y-logs)
6. [Troubleshooting](#troubleshooting)

---

## Instalación Local

### Requisitos Previos

- **Java 21+** - [Descargar JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
- **SQL Server 2019 o superior** - [Descargar SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server-2022)
- **Maven 3.8+** - Incluido en el proyecto (mvnw)
- **Git** - [Descargar Git](https://git-scm.com/)
- **Postman o Insomnia** - Para probar APIs (opcional)

### Verificar Instalación

```bash
# Verificar Java
java -version
# Debe mostrar Java version 21.x.x

# Verificar Maven (desde la carpeta del proyecto)
./mvnw --version
# En Windows: mvnw.cmd --version
```

### Pasos de Instalación

#### 1. Clonar o Descargar Proyecto

```bash
# Opción 1: Clonar desde GitHub
git clone https://github.com/tu-usuario/sigma-backend.git
cd sigma-backend

# Opción 2: Descargar manualmente
# Descargar ZIP desde GitHub y extraer
```

#### 2. Instalar Dependencias

```bash
# Windows
mvnw.cmd clean install

# macOS/Linux
./mvnw clean install
```

Este comando:
- Limpia compilaciones anteriores (`clean`)
- Descarga todas las dependencias (`install`)
- Compila el proyecto
- Ejecuta tests

#### 3. Crear Base de Datos

```sql
-- En SQL Server Management Studio o sqlcmd

CREATE DATABASE SIGMA;
GO

USE SIGMA;
GO

-- Las tablas se crearán automáticamente mediante JPA al iniciar la aplicación
```

#### 4. Configurar Credenciales

Editar `src/main/resources/application.properties`:

```properties
# Asegurar que coincidan con tu SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=tu_contraseña
```

#### 4.1 Configuración Automática de Tablas (JPA DDL)

Las tablas se crean automáticamente al iniciar la aplicación. Esto se controla mediante la propiedad `spring.jpa.hibernate.ddl-auto`:

**Opciones disponibles:**

| Valor | Descripción | Caso de Uso |
|-------|-------------|------------|
| `create` | Crea todas las tablas (elimina si existen) | ✅ Desarrollo inicial, testing |
| `create-drop` | Crea y elimina al cerrar | ⚠️ Testing únicamente |
| `update` | Actualiza schema sin perder datos | ✅ Producción, desarrollo |
| `validate` | Valida schema sin cambios | ⚠️ Producción segura |
| `none` | No hace nada | ⚠️ Control manual |

**Configuración actual (Desarrollo):**
```properties
spring.jpa.hibernate.ddl-auto=create
```

Esta configuración hará que:
1. Al iniciar la aplicación, todas las tablas se creen automáticamente
2. Las entidades de Java definen la estructura
3. No necesitas escribir SQL manualmente

**Para cambiar a Producción:**
```properties
spring.jpa.hibernate.ddl-auto=validate
```

Esto valida que el schema actual coincida con las entidades (sin hacer cambios).

#### 5. Ejecutar la Aplicación

```bash
# Opción 1: Usando Maven
mvnw.cmd spring-boot:run

# Opción 2: Ejecutar archivo JAR
mvnw.cmd package
java -jar target/sigma-backend-0.0.1-SNAPSHOT.jar

# Opción 3: Desde IDE (VS Code, IntelliJ, Eclipse)
# Click derecho en SigmaBackendApplication.java → Run
```

#### 6. Verificar Que Está Ejecutándose

```bash
# La aplicación debería estar accesible en:
http://localhost:8080

# Probar endpoint de login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'
```

---

## Configuración de Base de Datos

### SQL Server Local - Instalación Completa

#### Paso 1: Instalar SQL Server 2022

1. Descargar: https://www.microsoft.com/en-us/sql-server/sql-server-2022
2. Ejecutar instalador
3. Seleccionar "Custom" installation
4. Instalar Database Engine
5. Durante configuración:
   - Authentication mode: Mixed (SQL y Windows)
   - Default instance nombre: `SQLEXPRESS` o `MSSQLSERVER`
   - Puerto: 1433 (por defecto)

#### Paso 2: Crear Base de Datos y Usuario

```sql
-- Conectar con SQL Server Management Studio

-- Crear base de datos
CREATE DATABASE SIGMA;
GO

USE SIGMA;
GO

-- Crear login (usuario de SQL Server)
CREATE LOGIN sigma_user WITH PASSWORD = 'Sigma@123456';
GO

-- Crear usuario en la base de datos
CREATE USER sigma_user FOR LOGIN sigma_user;
GO

-- Asignar permisos
ALTER ROLE db_owner ADD MEMBER sigma_user;
GO
```

#### Paso 3: Actualizar application.properties

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;trustServerCertificate=true;
spring.datasource.username=sigma_user
spring.datasource.password=Sigma@123456
```

### Inicialización de Datos

El archivo `DataInitializer.java` crea automáticamente:
- Roles del sistema
- Usuario administrador por defecto
- Datos de prueba iniciales

---

## Ejecución de la Aplicación

### Desarrollo (con Hot Reload)

```bash
# Con Spring Boot DevTools habilitado automáticamente
mvnw.cmd spring-boot:run
```

Cambios en archivos se reflejan automáticamente sin reiniciar.

### Con Variables de Ambiente

```bash
# Windows - PowerShell
$env:SPRING_DATASOURCE_URL = "jdbc:sqlserver://localhost:1433;databaseName=SIGMA"
$env:SPRING_DATASOURCE_USERNAME = "sigma_user"
$env:SPRING_DATASOURCE_PASSWORD = "Sigma@123456"
mvnw.cmd spring-boot:run

# Windows - Command Prompt
set SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=SIGMA
set SPRING_DATASOURCE_USERNAME=sigma_user
set SPRING_DATASOURCE_PASSWORD=Sigma@123456
mvnw.cmd spring-boot:run

# Linux/macOS
export SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=SIGMA
export SPRING_DATASOURCE_USERNAME=sigma_user
export SPRING_DATASOURCE_PASSWORD=Sigma@123456
./mvnw spring-boot:run
```

### Rutas Disponibles

Después de iniciar, accede a:

```
API REST:
http://localhost:8080/api/auth/login
http://localhost:8080/api/pacientes/dashboard-info
http://localhost:8080/api/citas
... (ver DOCUMENTACION_TECNICA.md para lista completa)

Uploads:
http://localhost:8080/uploads/
```

---

## Deployment en Producción

### Preparación para Producción

#### 1. Crear Perfil de Producción

Crear `src/main/resources/application-prod.properties`:

```properties
# Base de Datos Producción (SQL Server en servidor)
spring.datasource.url=jdbc:sqlserver://prod-db-server.com:1433;databaseName=SIGMA;encrypt=true;trustServerCertificate=true;
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

# Logging
logging.level.root=INFO
logging.level.com.sigma=INFO
logging.file.name=/var/log/sigma/application.log

# Security
server.servlet.session.timeout=30m
spring.security.require-https=false

# Serialización JSON
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=America/New_York
```

#### 2. Agregar a .gitignore

```
# Credenciales de producción
application-prod.properties
.env
.env.local
```

#### 3. Construir para Producción

```bash
# Compilar con perfil producción
mvnw.cmd clean package -Pprod

# Verificar JAR generado
ls target/*.jar
```

### Deployment en Servidor Windows

#### 1. Preparar Servidor

- Instalar Java 21 en el servidor
- Instalar SQL Server (si no está instalado)
- Crear carpeta: `C:\sigma-backend`
- Crear carpeta para logs: `C:\sigma-backend\logs`

#### 2. Copiar Aplicación

```bash
# Desde tu máquina local
scp target/sigma-backend-0.0.1-SNAPSHOT.jar usuario@servidor:C:\sigma-backend\

# O manualmente
# Copiar archivo JAR a C:\sigma-backend\
```

#### 3. Configurar como Servicio Windows

Crear archivo `sigma-backend.bat`:

```batch
@echo off
REM Sigma Backend Service

set JAVA_HOME=C:\Program Files\Java\jdk-21
set APP_HOME=C:\sigma-backend
set JAR_FILE=%APP_HOME%\sigma-backend-0.0.1-SNAPSHOT.jar

REM Variables de Ambiente
set SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=SIGMA
set SPRING_DATASOURCE_USERNAME=sigma_user
set SPRING_DATASOURCE_PASSWORD=your_password
set SPRING_PROFILES_ACTIVE=prod

REM Ejecutar
%JAVA_HOME%\bin\java ^
  -Dspring.config.location=classpath:/,file:%APP_HOME%/config/ ^
  -Dlogging.file.name=%APP_HOME%\logs\sigma.log ^
  -jar %JAR_FILE%
```

#### 4. Instalar como Servicio (usando NSSM)

```bash
# Descargar NSSM desde: https://nssm.cc/download
# Descomprimir en C:\nssm

# Instalar servicio
C:\nssm\nssm.exe install SigmaBackend C:\sigma-backend\sigma-backend.bat

# Iniciar servicio
C:\nssm\nssm.exe start SigmaBackend

# Ver estado
C:\nssm\nssm.exe status SigmaBackend

# Detener servicio
C:\nssm\nssm.exe stop SigmaBackend
```

### Deployment en Linux/Docker

#### 1. Dockerfile

```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/sigma-backend-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_DATASOURCE_URL=jdbc:sqlserver://db-server:1433;databaseName=SIGMA
ENV SPRING_DATASOURCE_USERNAME=sigma_user
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 2. Compilar Imagen Docker

```bash
# Compilar proyecto
mvnw.cmd package

# Compilar imagen Docker
docker build -t sigma-backend:latest .

# Ejecutar contenedor
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:sqlserver://db-server:1433;databaseName=SIGMA \
  -e SPRING_DATASOURCE_USERNAME=sigma_user \
  -e SPRING_DATASOURCE_PASSWORD=password \
  --name sigma-backend \
  sigma-backend:latest
```

#### 3. Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  sigma-backend:
    image: sigma-backend:latest
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:sqlserver://sqlserver:1433;databaseName=SIGMA
      SPRING_DATASOURCE_USERNAME: sigma_user
      SPRING_DATASOURCE_PASSWORD: password
      SPRING_PROFILES_ACTIVE: prod
    depends_on:
      - sqlserver
    networks:
      - sigma-network

  sqlserver:
    image: mcr.microsoft.com/mssql/server:2022-latest
    environment:
      SA_PASSWORD: password
      ACCEPT_EULA: Y
    ports:
      - "1433:1433"
    volumes:
      - mssql_data:/var/opt/mssql
    networks:
      - sigma-network

volumes:
  mssql_data:

networks:
  sigma-network:
    driver: bridge
```

Ejecutar:
```bash
docker-compose up -d
```

---

## Monitoreo y Logs

### Ver Logs en Tiempo Real

```bash
# Windows PowerShell
Get-Content -Path "C:\sigma-backend\logs\application.log" -Wait

# Linux
tail -f /var/log/sigma/application.log
```

### Configurar Logging Avanzado

Crear `src/main/resources/logback-spring.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="LOG_FILE" value="logs/sigma.log"/>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/sigma-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="com.sigma" level="DEBUG"/>
    <logger name="org.springframework" level="INFO"/>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

### Monitoreo de Recursos

```bash
# Ver uso de memoria (Windows)
wmic OS get TotalVisibleMemorySize,FreePhysicalMemory

# Process info (Linux)
ps aux | grep java

# Ver puertos en uso
netstat -ano | findstr :8080
```

---

## Troubleshooting

### Error: Connection Refused

```
Error: Connection refused - Can't connect to localhost:1433
```

**Solución:**
```bash
# Verificar que SQL Server está ejecutándose
Start-Service MSSQLSERVER

# O para SQL Server Express
Start-Service MSSQL$SQLEXPRESS

# Verificar puerto
netstat -ano | findstr 1433
```

### Error: Login Failed

```
Login failed for user 'sigma_user'
```

**Solución:**
```sql
-- Verificar usuario existe
USE master;
SELECT name FROM sys.sql_logins WHERE name = 'sigma_user';

-- Resetear contraseña
ALTER LOGIN sigma_user WITH PASSWORD = 'NuevaContraseña123!';

-- Verificar permisos
USE SIGMA;
SELECT * FROM sys.database_principals WHERE name = 'sigma_user';
```

### Error: Port 8080 Already in Use

```
Port 8080 is already in use
```

**Solución:**
```bash
# Windows: Matar proceso en puerto 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux: Matar proceso en puerto 8080
lsof -i :8080
kill -9 <PID>

# O usar puerto diferente en application.properties:
# server.port=8081
```

### Error: Java Version Mismatch

```
java.lang.UnsupportedClassVersionError
```

**Solución:**
```bash
# Verificar versión de Java
java -version

# Debe ser Java 21 o superior
# Actualizar JAVA_HOME si es necesario
```

### Error: Database Doesn't Exist

```
Cannot open database "SIGMA" requested by the login
```

**Solución:**
```sql
-- Crear base de datos
CREATE DATABASE SIGMA;
GO
```

### Error: JWT Token Expired

```
JWT token expired: xxx
```

**Solución:**
- El token JWT expira cada 24 horas
- Implementar endpoint de refresh token
- O hacer login nuevamente

---

## Checklist de Deployment

- [ ] Java 21 instalado y verificado
- [ ] SQL Server instalado y ejecutándose
- [ ] Base de datos SIGMA creada
- [ ] Usuario y credenciales configurados
- [ ] `application.properties` actualizado correctamente
- [ ] `.gitignore` contiene archivos sensibles
- [ ] Proyecto compilado sin errores: `mvnw.cmd package`
- [ ] Tests pasan: `mvnw.cmd test`
- [ ] Aplicación inicia sin errores
- [ ] Endpoints pueden ser accedidos
- [ ] Logs están siendo generados
- [ ] Backups de base de datos configurados
- [ ] Firewall permite puerto 8080 (y 1433 para remoto)
- [ ] Monitoreo configurado

---

## Performance y Optimización

### Heap Memory

```bash
# Aumentar heap memory si es necesario
java -Xmx2g -Xms1g -jar sigma-backend-0.0.1-SNAPSHOT.jar
```

### Connection Pool (en application.properties)

```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

### Caché (si lo implementas)

```properties
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=10m
```

---

Documento actualizado: 22 de febrero de 2026
Versión: 1.0
