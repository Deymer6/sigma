# 🏥 Sistema SIGMA - Gestión Clínica Obstétrica

![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203.x-brightgreen)
![Angular](https://img.shields.io/badge/Frontend-Angular%2017-red)
![SQL Server](https://img.shields.io/badge/Database-SQL%20Server-blue)

**SIGMA** es una plataforma integral diseñada para la gestión de servicios clínicos, con un enfoque robusto en el control de acceso basado en roles (RBAC) y la optimización de procesos operativos.

---

## 📂 Estructura del Proyecto

El repositorio se divide en dos módulos principales:

| Módulo | Descripción | Tecnología Principal |
| :--- | :--- | :--- |
| **`sigma-backend`** | API REST encargada de la lógica de negocio y persistencia. | Java 17 / Spring Boot |
| **`sigma-frontend`** | Interfaz de usuario interactiva y responsiva. | TypeScript / Angular |

---

## 🛠️ Tecnologías Utilizadas

### **Backend**
* **Lenguaje:** Java 17+
* **Framework:** Spring Boot 3.x
* **Persistencia:** Spring Data JPA
* **Seguridad:** JSON Web Token (JWT) para autenticación sin estado.

### **Frontend**
* **Framework:** Angular
* **Estilos:** CSS3 / Bootstrap 5
* **Entorno:** `src/environments/environment.ts` para gestión de APIs.

---

## 🚀 Configuración y Ejecución

### **1. Requisitos Previos**
* ✅ Java JDK 17 o superior.
* ✅ Node.js (v18+) y npm.
* ✅ Microsoft SQL Server activo.

### **2. Base de Datos**
El sistema requiere una base de datos llamada `SIGMA`.
* **Host:** `localhost:1433`
* **Credenciales:** Configura tu `user` y `password` en el archivo `application.properties`.

```properties
# Ejemplo de conexión
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=SIGMA;encrypt=false;
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA
