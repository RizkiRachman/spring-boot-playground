# RULES.md for spring-boot-playground

## AI Agent Rules and Guidelines

This document contains important rules and guidelines that AI coding assistants must follow when working on this Spring Boot project.

### Security and Privacy Rules

#### Credential Handling
- **NEVER read or display** any files containing credentials, API keys, passwords, or sensitive information
- **AVOID accessing** files with names like:
  - `application-secret.properties`
  - `application-prod.properties`
  - `.env`
  - `secrets.yml`
  - `credentials.json`
  - Any file containing "secret", "key", "password", "token", or "credential" in the name
- **DO NOT suggest** or implement code that hardcodes credentials
- **ALWAYS use** environment variables or external configuration for sensitive data
- **NEVER commit** or suggest committing credential files to version control

#### Data Protection
- **AVOID processing** or displaying user data, personal information, or sensitive business data
- **DO NOT access** database files, logs, or data dumps that may contain sensitive information
- **RESPECT privacy** by not reading or analyzing files that may contain user data

### Code Quality Rules

#### Best Practices
- **ALWAYS follow** the patterns and conventions outlined in `AGENTS.md`
- **USE Lombok** appropriately for reducing boilerplate code
- **IMPLEMENT proper exception handling** using `@ControllerAdvice`
- **CONFIGURE properties** via `application.properties` with sensible defaults
- **LOG appropriately** using SLF4J without excessive logging

#### Architecture Compliance
- **MAINTAIN layered architecture**: Controller → Service → Repository
- **USE Spring Data JPA** for data access with proper repository interfaces
- **IMPLEMENT rate limiting** as configured in the application properties
- **FOLLOW package structure**: `com.example.{projectname}.{layer}`

### Development Workflow Rules

#### File Management
- **ONLY edit** files that are part of the Spring Boot application
- **DO NOT modify** build files (`pom.xml`) unless explicitly requested
- **AVOID creating** unnecessary files or directories
- **USE appropriate file extensions** and naming conventions

#### Testing
- **ALWAYS write** unit tests for new functionality
- **ENSURE tests pass** before suggesting code changes
- **USE Spring Boot Test** for integration testing
- **MOCK external dependencies** appropriately

### Communication Rules

#### Response Guidelines
- **BE concise** but informative in responses
- **EXPLAIN complex changes** with clear reasoning
- **PROVIDE examples** when introducing new concepts
- **ASK for clarification** only when absolutely necessary

#### Error Handling
- **REPORT compilation errors** immediately with suggested fixes
- **SUGGEST alternatives** when encountering blocking issues
- **DOCUMENT workarounds** for known limitations

### Ethical Guidelines

#### Responsible AI Use
- **DO NOT generate** harmful, offensive, or inappropriate content
- **RESPECT intellectual property** and avoid copyright violations
- **PROMOTE best practices** and security-conscious development
- **CONTRIBUTE positively** to the codebase and team productivity

#### Transparency
- **BE honest** about capabilities and limitations
- **ADMIT uncertainty** when not confident about a solution
- **SUGGEST human review** for critical changes or complex decisions

### Project-Specific Rules

#### Spring Boot Playground
- **EXPERIMENT safely** without affecting production systems
- **DOCUMENT new features** in `AGENTS.md` when implemented
- **MAINTAIN compatibility** with existing rate limiting implementation
- **USE H2** for development database unless specified otherwise

#### Version Control
- **COMMIT logically** with clear, descriptive messages
- **DO NOT commit** generated files, dependencies, or sensitive data
- **FOLLOW conventional commits** format when possible

### Emergency Rules

#### When in Doubt
- **STOP and ask** if a requested action might violate security rules
- **CONSULT documentation** before making significant changes
- **PREFER conservative approaches** to avoid breaking existing functionality
- **SUGGEST testing** for any non-trivial changes

#### Recovery
- **DOCUMENT issues** encountered during development
- **PROVIDE rollback instructions** for major changes
- **MAINTAIN backups** of critical configuration files
