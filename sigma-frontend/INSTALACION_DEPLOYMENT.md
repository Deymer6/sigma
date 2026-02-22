# INSTALACION_DEPLOYMENT.md - Guía de Instalación y Deployment

## Tabla de Contenidos

1. [Requisitos del Sistema](#requisitos-del-sistema)
2. [Instalación Local](#instalación-local)
3. [Configuración del Entorno](#configuración-del-entorno)
4. [Ejecución en Desarrollo](#ejecución-en-desarrollo)
5. [Build para Producción](#build-para-producción)
6. [Deployment](#deployment)
7. [Docker (Opcional)](#docker-opcional)
8. [Troubleshooting](#troubleshooting)

---

## Requisitos del Sistema

### Software Obligatorio

| Componente | Versión Mínima | Recomendada |
|-----------|---------------|-----------| 
| Node.js | 18.x | 20.x |
| npm | 9.x | 10.x |
| Angular CLI | 20.x | 20.2.2 |
| Git | 2.30+ | Latest |

### Sistema Operativo

- **Windows:** 10 o superior (con PowerShell 5.1+)
- **macOS:** 10.15 o superior
- **Linux:** Ubuntu 20.04+ o equivalente

### Hardware Recomendado

- **RAM:** 8 GB mínimo (16 GB recomendado)
- **Disco:** 5 GB libres
- **Procesador:** Quad-core 2.5 GHz

### Componentes Externos

- **Backend Sigma:** Ejecutándose en `http://localhost:8080`
- **Navegador Moderno:** Chrome 90+, Firefox 88+, Safari 14+, Edge 90+

---

## Instalación Local

### Paso 1: Clonar el Repositorio

**SSH (Recomendado):**
```bash
git clone git@github.com:Deymer6/sigma.git
cd sigma/sigma-frontend
```

**HTTPS:**
```bash
git clone https://github.com/Deymer6/sigma.git
cd sigma/sigma-frontend
```

### Paso 2: Verificar Versiones Instaladas

```bash
# Verificar Node.js
node --version
# Esperado: v20.x.x

# Verificar npm
npm --version
# Esperado: 10.x.x

# Verificar Angular CLI (si está instalado globalmente)
ng version
```

### Paso 3: Instalar Dependencias

```bash
# Limpiar caché de npm (si hay problemas previos)
npm cache clean --force

# Instalar todas las dependencias
npm install
```

**Tiempo esperado:** 2-5 minutos según conexión

### Paso 4: Verify Instalación

```bash
# Verificar que Angular CLI funciona
ng version

# Esperado: Angular CLI version 20.2.2
```

---

## Configuración del Entorno

### Paso 1: Crear Archivo de Configuración

```bash
# Copiar archivo de ejemplo
cp src/environments/environment.example.ts src/environments/environment.ts
```

Si no existe `environment.example.ts`, crear uno:

```bash
cat > src/environments/environment.ts << 'EOF'
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  jwtTokenName: 'auth_token',
  jwtRefreshTokenName: 'refresh_token'
};
EOF
```

### Paso 2: Configurar Variables de Entorno

Editar `src/environments/environment.ts`:

```typescript
export const environment = {
  // Modo de producción
  production: false,

  // URL del backend
  apiUrl: 'http://localhost:8080/api',

  // Nombres de tokens en localStorage
  jwtTokenName: 'auth_token',
  jwtRefreshTokenName: 'refresh_token',

  // Configuración de sesión (en milisegundos)
  tokenExpiration: 3600000, // 1 hora
  refreshTokenExpiration: 86400000, // 24 horas

  // URLs adicionales
  uploadUrl: 'http://localhost:8080/api/upload',
  
  // Timeouts
  httpTimeout: 30000, // 30 segundos
};
```

### Paso 3: Archivo de Producción

Crear `src/environments/environment.prod.ts`:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.sigma.com/api', // URL de producción
  jwtTokenName: 'auth_token',
  jwtRefreshTokenName: 'refresh_token',
  tokenExpiration: 3600000,
  refreshTokenExpiration: 86400000,
  uploadUrl: 'https://api.sigma.com/api/upload',
  httpTimeout: 30000,
};
```

### Paso 4: Variables de Sistema (Opcional)

Crear `.env` en la raíz (NO commitear):

```bash
NODE_ENV=development
ANGULAR_ENV=development
API_URL=http://localhost:8080/api
PORT=4200
```

Usar en scripts:

```bash
# Instalar dotenv
npm install dotenv

# En angular.json o scripts
node -r dotenv/config
```

---

## Ejecución en Desarrollo

### Iniciar Servidor de Desarrollo

```bash
# Opción 1: Comando npm
npm start

# Opción 2: Comando Angular CLI directo
ng serve

# Opción 3: Con opciones personalizadas
ng serve --port 4201 --disable-host-check --watch
```

**Salida esperada:**
```
✔ Compiled successfully.
✔ Compiled successfully.

Application bundle generation complete. 2.34 MB (2.02 MB estimated gzip).

Watch mode enabled. Watching for file changes...

Local:   http://localhost:4200/
Browser: http://localhost:4200/
```

### Acceder a la Aplicación

Abrir en navegador:
```
http://localhost:4200/
```

### Servidor Está en Vivo

- Los cambios se reflejan automáticamente
- Presionar Ctrl+C para detener el servidor
- Cambiar puertos si 4200 está ocupado: `ng serve --port 4201`

### Debug en Navegador

1. **Abrir DevTools:** F12 o Ctrl+Shift+I
2. **Pestaña Sources:** Ver código TypeScript compilado
3. **Console:** Ver logs y errores
4. **Network:** Monitorear peticiones HTTP

---

## Build para Producción

### Build Normal

```bash
# Build optimization para producción
npm run build

# Archivos generados en: dist/sigma-frontend/
```

### Build con SSR (Server-Side Rendering)

```bash
# Build con SSR
npm run build -- --configuration production

# Archivos generados en:
# - dist/sigma-frontend/browser/    (Cliente)
# - dist/sigma-frontend/server/     (Servidor)
```

### Servir Localmente (Producción)

```bash
# Antes necesitas hacer build
npm run build

# Servir con http-server
npm install -g http-server
http-server dist/sigma-frontend/browser -p 8000

# O con Python (si lo tienes)
python -m http.server 8000 --directory dist/sigma-frontend/browser
```

### Verificar Build

```bash
# Ver tamaño de bundles
npm run build -- --stats-json

# Analizar bundles (necesita webpack-bundle-analyzer)
npm install webpack-bundle-analyzer
npx webpack-bundle-analyzer dist/sigma-frontend/browser/stats.json
```

---

## Deployment

### Pre-Deployment Checklist

- ✅ Tests pasan: `npm test`
- ✅ Build exitoso: `npm run build`
- ✅ Variables de entorno configuradas
- ✅ Backend disponible y funcionando
- ✅ Base de datos actualizada
- ✅ Certificados SSL (si aplica)
- ✅ Backups de BD realizados
- ✅ Plan de rollback preparado

### Opción 1: Deployment en Servidor Linux con Nginx

#### 1.1 Preparar Servidor

```bash
# En el servidor Linux
sudo apt update && sudo apt upgrade -y

# Instalar Node.js (si usas SSR)
curl -sL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install nodejs -y

# Instalar Nginx
sudo apt install nginx -y

# Crear carpeta para la app
sudo mkdir -p /var/www/sigma-frontend
sudo chown -R $USER:$USER /var/www/sigma-frontend
```

#### 1.2 Subir Archivos

```bash
# Opción A: Usando SCP
scp -r dist/sigma-frontend/browser/* user@server:/var/www/sigma-frontend/

# Opción B: Usando Git
ssh user@server
cd /var/www/sigma-frontend
git clone https://github.com/tuusuario/sigma.git .
cd sigma-frontend
npm install
npm run build
```

#### 1.3 Configurar Nginx

Crear `/etc/nginx/sites-available/sigma-frontend`:

```nginx
server {
    listen 80;
    server_name sigma.com www.sigma.com;

    # Redirigir HTTP a HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name sigma.com www.sigma.com;

    # Certificados SSL
    ssl_certificate /etc/letsencrypt/live/sigma.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/sigma.com/privkey.pem;

    # Configuración SSL
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css text/javascript application/json;
    gzip_min_length 1000;

    root /var/www/sigma-frontend;
    index index.html;

    # Rutas Angular (SPA)
    location / {
        try_files $uri $uri/ /index.html;
        expires -1;
        add_header Cache-Control "no-cache, no-store, no-transform, public, must-revalidate";
    }

    # Assets estáticos (caché)
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # API proxy (al backend)
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    # Logs
    access_log /var/log/nginx/sigma-frontend.access.log;
    error_log /var/log/nginx/sigma-frontend.error.log;
}
```

Habilitar sitio:

```bash
sudo ln -s /etc/nginx/sites-available/sigma-frontend /etc/nginx/sites-enabled/
sudo systemctl restart nginx
```

### Opción 2: Deployment en Vercel

**Vercel.json:**

```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist/sigma-frontend/browser",
  "rewrites": [
    {
      "source": "/:path((?!api).*)*",
      "destination": "/index.html"
    }
  ],
  "env": {
    "ANGULAR_ENV": "production",
    "API_URL": "https://api.sigma.com/api"
  }
}
```

Comando:

```bash
npm i -g vercel
vercel --prod
```

### Opción 3: Deployment en Docker

**Dockerfile:**

```dockerfile
# Build stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Serve stage
FROM node:20-alpine
WORKDIR /app
RUN npm install -g serve
COPY --from=build /app/dist/sigma-frontend/browser ./dist
EXPOSE 3000
CMD ["serve", "-s", "dist", "-l", "3000"]
```

**Build y ejecución:**

```bash
# Build imagen
docker build -t sigma-frontend:1.0.0 .

# Ejecutar contenedor
docker run -p 3000:3000 -e API_URL=http://backend:8080/api sigma-frontend:1.0.0
```

---

## Docker (Opcional)

### docker-compose.yml

```yaml
version: '3.8'

services:
  frontend:
    build:
      context: ./sigma-frontend
      dockerfile: Dockerfile
    ports:
      - "3000:3000"
    environment:
      - API_URL=http://backend:8080/api
    depends_on:
      - backend
    networks:
      - sigma-network

  backend:
    build:
      context: ./sigma-backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:sqlserver://db:1433;databaseName=sigma
      - SPRING_DATASOURCE_USERNAME=sa
      - SPRING_DATASOURCE_PASSWORD=SecurePassword123!
    depends_on:
      - db
    networks:
      - sigma-network

  db:
    image: mcr.microsoft.com/mssql/server:2019-latest
    ports:
      - "1433:1433"
    environment:
      - SA_PASSWORD=SecurePassword123!
      - ACCEPT_EULA=Y
    volumes:
      - db_data:/var/opt/mssql
    networks:
      - sigma-network

volumes:
  db_data:

networks:
  sigma-network:
    driver: bridge
```

Ejecutar:

```bash
docker-compose up -d
```

---

## Troubleshooting

### Problema: "Port 4200 is already in use"

```bash
# Opción 1: Cambiar puerto
ng serve --port 4201

# Opción 2: Matar proceso que usa el puerto
# En Windows (PowerShell admin):
netstat -ano | findstr :4200
taskkill /PID <PID> /F

# En Linux/Mac:
lsof -i :4200
kill -9 <PID>
```

### Problema: "Module not found"

```bash
# Limpiar node_modules
rm -rf node_modules package-lock.json

# Reinstalar
npm install
```

### Problema: "404 on refresh"

Ocurre con SPA. Soluciones:

**En desarrollo:**
```typescript
// Respuesta de serve ya lo maneja
// Si usas http-server:
http-server dist/sigma-frontend/browser -p 8000 --spa
```

**En producción (Nginx):**
```nginx
location / {
    try_files $uri $uri/ /index.html;
}
```

### Problema: "CORS error"

Error en consola: `Access to XMLHttpRequest blocked by CORS policy`

**Solución 1 (Backend):**
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true);
    }
}
```

**Solución 2 (Development proxy):**
```bash
# proxy.conf.json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "pathRewrite": {"^/api": ""}
  }
}

# ng serve --proxy-config proxy.conf.json
```

### Problema: "Build fails with memory error"

```bash
# Aumentar memoria Node
export NODE_OPTIONS=--max-old-space-size=4096
npm run build

# O en Windows (PowerShell):
$env:NODE_OPTIONS = "--max-old-space-size=4096"
npm run build
```

### Problema: "Token not persisting"

El token se pierde al refrescar la página.

**Solución:**
```typescript
// En auth.service.ts
private loadUser(): void {
  const token = localStorage.getItem(environment.jwtTokenName);
  if (token) {
    // Validar token y configurar usuario actual
  }
}
```

---

## Checklist de Producción

### Antes del Deploy

- ✅ Todos los tests pasan (`npm test`)
- ✅ Build exitoso (`npm run build`)
- ✅ No hay warnings en la consola
- ✅ Variables de entorno configuradas correctamente
- ✅ SSL/HTTPS habilitado
- ✅ CORS configurado en backend
- ✅ Rate limiting en backend
- ✅ Logs configurados
- ✅ Monitoring configurado
- ✅ Backup de BD realizado

### Después del Deploy

- ✅ Aplicación carga correctamente
- ✅ Login funciona
- ✅ Peticiones HTTP exitosas
- ✅ Base de datos accessible
- ✅ Errores se loguean correctamente
- ✅ Monitoreo activo

---

## Rollback Procedure

Si surge un problema después del deployment:

```bash
# 1. Frenar tráfico (si es posible)
# Redirigir a página de mantenimiento

# 2. Revertar a versión anterior
git revert <commit-hash>
npm install
npm run build

# 3. Redeploy
# (ver opciones de deployment arriba)

# 4. Validar
# Pruebas manuales y automáticas

# 5. Comunicar
# Notificar al equipo y usuarios
```

---

**Última actualización:** 22 de febrero de 2026  
**Versión:** 1.0.0
