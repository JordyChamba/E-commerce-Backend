# API de Comercio Electrónico en Docker

Este proyecto es una API REST construida con Spring Boot para una aplicación sencilla de comercio electrónico. Permite autenticación mediante JWT, gestión de roles de usuario, manejo de productos y categorías, procesamiento de pedidos y utiliza PostgreSQL para la persistencia. La documentación de la API está disponible a través de Swagger UI.

## 🚀 Tecnologías

- Java 21
- Spring Boot 3.2.3
- Spring Security con JWT
- Spring Data JPA
- PostgreSQL
- Swagger (springdoc-openapi)
- Lombok
- Maven

## 📁 Estructura del proyecto

```
src/main/java/com/docker/backend/
  ├─ config/         # Configuraciones (seguridad, filtro JWT, Swagger)
  ├─ controller/     # Controladores REST (Auth, Category, Order, Product)
  ├─ dto/            # Objetos de transferencia (peticiones y respuestas)
  ├─ exception/      # Excepciones personalizadas y manejador global
  ├─ model/          # Entidades JPA y enums
  ├─ repository/     # Repositorios Spring Data JPA
  ├─ service/        # Lógica de negocio
  └─ util/           # Utilitarios (ej. JWT)

src/main/resources/application.properties  # Configuración general

test/java/com/docker/backend/               # Pruebas unitarias e integración
```

## 🧠 Cómo funciona el proyecto

1. **Inicio de la aplicación**: Se levanta un servidor embebido (Tomcat) y se cargan los componentes de Spring.
2. **Seguridad**: `SecurityConfig` define las rutas públicas y privadas. Se usa `JwtAuthenticationFilter` para capturar el token JWT en cada petición.
3. **Autenticación/Registro**: El controlador `AuthController` maneja los endpoints `/api/auth/register` y `/api/auth/login`. Al iniciar sesión se genera un JWT con datos del usuario y roles.
4. **Gestión de datos**: Los controladores de categoría, producto y pedido (`CategoryController`, `ProductController`, `OrderController`) exponen CRUD y operaciones específicas. Cada uno delega en un servicio que contiene la lógica de negocio.
5. **Persistencia**: Las entidades JPA están en el paquete `model`; los repositorios (`repository`) extienden de `JpaRepository` para consultas básicas.
6. **DTOs**: Se utilizan DTOs para evitar exponer las entidades directamente y para validar datos de entrada.
7. **Manejo de errores**: `exception` incluye excepciones personalizadas y un `GlobalExceptionHandler` para respuestas consistentes.
8. **Configuración**: Los parámetros de conexión a la base de datos y JWT se establecen en `application.properties`.

## ⚙️ Configuración

Edita `src/main/resources/application.properties` para indicar los datos de tu base de datos y ajustes del JWT:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tu_basedatos
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

jwt.secret=tuClaveSecreta
jwt.expiration=3600000
```

## 🛠️ Compilar y ejecutar

Usa el wrapper de Maven o tu propia instalación de Maven:

```bash
# compilar
./mvnw clean package

# ejecutar
./mvnw spring-boot:run
```

La aplicación iniciará en `http://localhost:8080` por defecto.

## 📌 Documentación de la API

Swagger UI está disponible en: `http://localhost:8080/swagger-ui/index.html` donde puedes probar los endpoints.

## 🔑 Autenticación

Los endpoints bajo `/api/auth` gestionan el registro y el inicio de sesión. Después de autenticarte, debes incluir el token en el encabezado `Authorization` como `Bearer <token>` para acceder a rutas protegidas.

## 📦 Dockerear la aplicación

A pesar del nombre del proyecto, actualmente no hay un `Dockerfile`. Para contenerizar la aplicación puedes crear uno y construir la imagen con:

```bash
docker build -t docker-backend .
```

## 📘 Pruebas

Ejecuta las pruebas unitarias e integración con:

```bash
./mvnw test
```

## 📝 Notas adicionales

- Se utiliza Lombok en varias clases; se recomienda tener el plugin de Lombok en tu IDE.
- Ajusta la configuración de CORS o seguridad en `SecurityConfig` si accedes desde un frontend.

---

Explora el código fuente y adapta la API según tus necesidades. ¡Disfruta construyendo tu plataforma de e-commerce!✨
