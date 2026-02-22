Sistema SIGMA - Gestión Clínica Obstétrica
📝 Descripción del Proyecto
SIGMA es una plataforma integral diseñada para la gestión de servicios clínicos, enfocada en el control de acceso basado en roles (RBAC) y la eficiencia operativa. Este repositorio contiene tanto el núcleo del servidor (Backend) como la interfaz de usuario (Frontend).

📂 Estructura del Repositorio
sigma-backend/: API REST desarrollada con Spring Boot y SQL Server.

sigma-frontend/: Aplicación web desarrollada con Angular/React (según tu framework).

🛠️ Tecnologías Utilizadas
Backend
Lenguaje: Java 17+

Framework: Spring Boot 3.x

Base de Datos: Microsoft SQL Server

Seguridad: JSON Web Token (JWT)

Frontend
Framework: Angular (o el que estés usando según tu environment.ts)

Estilos: CSS/Bootstrap

🚀 Configuración Rápida
1. Requisitos Previos
Java JDK 17 instalado.

Node.js y npm instalados.

Instancia de SQL Server activa.

2. Base de Datos
Debes crear una base de datos llamada SIGMA. El sistema está configurado para conectarse en localhost:1433.

Nota: Por seguridad, asegúrate de configurar tus propias credenciales en el archivo application.properties del backend antes de iniciar.

3. Ejecución del Proyecto
Levantar el Backend:

Bash
cd sigma-backend
./mvnw spring-boot:run
Levantar el Frontend:

Bash
cd sigma-frontend
npm install
npm start
🔐 Roles y Autorización
El sistema implementa un control de acceso basado en roles (RBAC):

Administrador: Acceso total a la configuración y usuarios.

Especialista: Gestión de registros clínicos.

Recepción: Gestión de citas y datos básicos.