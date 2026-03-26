# AGENTS.md for spring-boot-playground

## Project Overview
This is a Spring Boot playground project for experimenting with Spring Boot features. The project follows standard Spring Boot conventions.

## Architecture
- **Main Application**: `src/main/java/.../Application.java` annotated with `@SpringBootApplication`
- **Controllers**: REST endpoints in `src/main/java/.../controller/` using `@RestController`
- **Services**: Business logic in `src/main/java/.../service/` 
- **Repositories**: Data access in `src/main/java/.../repository/` extending `JpaRepository`
- **Entities**: JPA entities in `src/main/java/.../entity/`
- **Configuration**: Properties in `src/main/resources/application.properties`

## Build System
Uses Maven with `pom.xml` in project root. Currently using Spring Boot 4.0.0.

## Key Workflows
- **Run Application**: `mvn spring-boot:run`
- **Run Tests**: `mvn test`
- **Build JAR**: `mvn clean package`
- **Debug**: Run in IDE with breakpoints, or use `mvn spring-boot:run` with JVM debug options

## Conventions
- Package structure: `com.example.{projectname}.{layer}`
- Use Lombok for reducing boilerplate code (@Data, @AllArgsConstructor, @NoArgsConstructor)
- Exception handling via `@ControllerAdvice` classes
- Configuration via `@ConfigurationProperties` beans
- Logging with SLF4J (logback)
- Database: H2 in-memory for development, configurable via profiles

## Dependencies
Common starters:
- `spring-boot-starter-web`: For REST APIs
- `spring-boot-starter-data-jpa`: For JPA repositories
- `spring-boot-starter-test`: For testing
- `spring-boot-starter-actuator`: For monitoring
- `lombok`: For code generation

## Integration Points
- External APIs: Use `RestTemplate` or `WebClient`
- Databases: Configure via `application.properties` (e.g., `spring.datasource.url`)
- Messaging: Spring Integration or AMQP starters if needed

## Patterns
- Repository pattern for data access
- Service layer for business logic
- DTOs for API responses (in `dto` package)
- Validation with Bean Validation (@Valid, @NotNull)
- Rate limiting: Implemented via `RateLimitingFilter` using in-memory tracking per client IP, configurable via `rate.limiter.requests.per.minute` in `application.properties`

## Debugging
- Use Spring Boot DevTools for hot reload
- Enable SQL logging: `spring.jpa.show-sql=true`
- Actuator endpoints: `/actuator/health`, `/actuator/info`
