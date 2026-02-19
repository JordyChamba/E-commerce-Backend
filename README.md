# Docker E-commerce API

This project is a Spring Boot-based RESTful API for a simple e-commerce application. It includes authentication via JWT, user roles, product and category management, order processing, and integration with PostgreSQL for persistence. Swagger UI is configured for API documentation.

## 🚀 Technologies

- Java 21
- Spring Boot 3.2.3
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Swagger (springdoc-openapi)
- Lombok
- Maven

## 📁 Project Structure

```
src/main/java/com/docker/backend/
  ├─ config/         # Security, JWT filter, Swagger
  ├─ controller/     # REST controllers (Auth, Category, Order, Product)
  ├─ dto/            # Request and response DTOs
  ├─ exception/      # Custom exceptions and global handler
  ├─ model/          # JPA entities and enums
  ├─ repository/     # Spring Data JPA repositories
  ├─ service/        # Business logic services
  └─ util/           # JWT utility

src/main/resources/application.properties  # Configuration

test/java/com/docker/backend/               # Integration/unit tests
```

## ⚙️ Configuration

Edit `src/main/resources/application.properties` to set your database connection and JWT settings: 

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb
spring.datasource.username=youruser
spring.datasource.password=yourpassword

jwt.secret=yourSecretKey
jwt.expiration=3600000
```

## 🛠️ Build & Run

Use Maven wrapper or your own Maven installation:

```bash
# build
./mvnw clean package

# run
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080` by default.

## 📌 API Documentation

Swagger UI is available at: `http://localhost:8080/swagger-ui/index.html`

## 🔑 Authentication

Endpoints under `/api/auth` handle registration and login. Once authenticated, include the token in the `Authorization` header as `Bearer <token>` for protected routes.

## 📦 Docker

Although the project is named "docker", there is currently no Dockerfile included. To containerize the app, you can add a `Dockerfile` and build an image using:

```bash
docker build -t docker-backend .
```

## 📘 Testing

Run unit/integration tests with:

```bash
./mvnw test
```

## 📝 Notes

- Lombok annotations are used extensively; IDE support for Lombok is recommended.
- Adjust CORS or security settings in `SecurityConfig` if accessing from a frontend.

---

Feel free to explore the source code and adapt the API to your needs. Enjoy building your e-commerce platform!✨
