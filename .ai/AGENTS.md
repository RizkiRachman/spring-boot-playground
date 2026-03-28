# AGENTS.md for spring-boot-playground

## Project Overview
This is a Spring Boot playground project for experimenting with Spring Boot features. The project uses shared libraries (common-utils-java and common-exception-java) to avoid code duplication across microservices.

## Architecture
- **Main Application**: `src/main/java/.../Application.java` annotated with `@SpringBootApplication`
- **Controllers**: REST endpoints in `src/main/java/.../controller/` using `@RestController`
- **Services**: Business logic in `src/main/java/.../service/` 
- **Repositories**: Data access in `src/main/java/.../repository/` extending `JpaRepository`
- **Entities**: JPA entities in `src/main/java/.../entity/`
- **Configuration**: Properties in `src/main/resources/application.properties`
- **Shared Libraries**: Uses `common-utils-java` and `common-exception-java` from local Maven repository

## Build System
Uses Maven with `pom.xml` in project root. Currently using Spring Boot 4.0.0.

## Key Workflows
- **Run Application**: `mvn spring-boot:run`
- **Run Tests**: `mvn test`
- **Build JAR**: `mvn clean package`
- **Debug**: Run in IDE with breakpoints, or use `mvn spring-boot:run` with JVM debug options

## Pre-PR Requirements (CRITICAL)

Before creating any Pull Request, AI MUST:

1. **Clean Compile**
   ```bash
   mvn clean compile -q
   ```
   - Must complete without errors
   - No compilation warnings (fix or document if unavoidable)

2. **Run Tests**
   ```bash
   mvn test
   ```
   - All tests must pass
   - Minimum 90% code coverage required
   - Generate coverage report: `mvn jacoco:report`
   - View report: `target/site/jacoco/index.html`

3. **Start Application Locally**
   ```bash
   mvn spring-boot:run
   ```
   - Verify app starts without errors
   - Check logs for exceptions
   - Confirm port 8080 is accessible

4. **Manual Testing**
   - Test all modified endpoints
   - Verify rate limiting works
   - Check error response format
   - Test both success and failure cases

5. **Verify No Breaking Changes**
   - Check existing functionality still works
   - Verify API contracts are maintained

**⚠️ DO NOT CREATE PR if any step fails. Fix issues first!**

## Testing Standards

### Coverage Requirements
- **Minimum**: 90% line coverage
- **Minimum**: 90% branch coverage
- **Target**: 100% for business logic
- Check coverage: `mvn clean test jacoco:report`

### Test Organization
```
src/test/java/com/example/springbootplayground/
├── config/
│   ├── RateLimiterTest.java
│   └── RateLimiterPropertiesTest.java
├── controller/
│   └── HelloControllerTest.java
├── service/
│   └── RateLimiterServiceTest.java
├── util/
│   └── RequestUtilsTest.java
├── constant/
│   └── ErrorMessagesTest.java
└── exception/
    └── GlobalExceptionHandlerTest.java
```

**Note**: StringUtils tests are in the `common-utils-java` library. RateLimitExceededException tests are in the `common-exception-java` library. This project has **50 tests** for application-specific code.

### Test Naming Conventions
- Class name: `{ClassName}Test`
- Method name: `should{ExpectedBehavior}When{Condition}`
- Use @DisplayName for human-readable descriptions

### Required Test Types
- ✅ Unit tests for all public methods
- ✅ Edge case testing (null, empty, boundary values)
- ✅ Exception testing
- ✅ Happy path testing

## Conventions
- Package structure: `com.example.{projectname}.{layer}`
- Use Lombok for reducing boilerplate code (@Data, @AllArgsConstructor, @NoArgsConstructor)
- Exception handling via `@ControllerAdvice` classes
- Configuration via `@ConfigurationProperties` beans
- Logging with SLF4J (logback)
- Database: H2 in-memory for development, configurable via profiles

## Dependencies

### Spring Boot Starters
- `spring-boot-starter-web`: For REST APIs
- `spring-boot-starter-data-jpa`: For JPA repositories
- `spring-boot-starter-test`: For testing
- `spring-boot-starter-actuator`: For monitoring
- `lombok`: For code generation

### Shared Libraries (Local Maven Repository)
These libraries must be installed locally before building this project:

**common-utils-java** (com.dev:common-utils-java:1.0.0-SNAPSHOT)
- Utility classes: StringUtils, pagination, validation
- Install: `cd /path/to/common-utils-java && mvn clean install`

**common-exception-java** (com.dev:common-exception-java:1.0.0-SNAPSHOT)
- Exceptions: RateLimitExceededException, BusinessException, ErrorResponse
- Install: `cd /path/to/common-exception-java && mvn clean install`

### Installation Order
```bash
# 1. Install common-utils-java first
cd /path/to/common-utils-java
mvn clean install

# 2. Install common-exception-java
cd /path/to/common-exception-java
mvn clean install

# 3. Then build this project
cd /path/to/spring-boot-playground
mvn clean compile
```

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
