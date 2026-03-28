# Spring Boot Playground

A playground project for experimenting with Spring Boot features and best practices, featuring production-ready rate limiting, exception handling, and shared library integrations.

## 🚀 Features

- ✨ **REST API** with Spring Boot 4.0.0
- 🛡️ **Dual-Layer Rate Limiting** (Bucket4j + Filter/Service)
- ⚡ **Exception Handling** with @ControllerAdvice
- 📦 **Shared Libraries** - Uses common-utils-java and common-exception-java
- 📝 **Configuration Properties** for flexible rate limiting
- 🔧 **Constructor Injection** for thread safety
- 📊 **Global Exception Handling** with standardized JSON responses
- 🏗️ **Lombok** for boilerplate reduction
- ☕ **Java 26** with preview features enabled

## 🏗️ Architecture

```
src/main/java/com/example/springbootplayground/
├── config/                    # Configuration classes
│   ├── AppConfig.java
│   ├── RateLimiter.java        # Filter layer rate limiter (Bucket4j)
│   ├── RateLimiterConfig.java
│   ├── RateLimiterProperties.java
│   └── RateLimitingFilter.java
├── controller/                # REST controllers
│   └── HelloController.java
├── service/                   # Business logic
│   └── RateLimiterService.java
├── dto/                      # Data transfer objects (Lombok)
│   └── ErrorResponse.java
├── exception/               # Exception handlers
│   └── GlobalExceptionHandler.java
├── util/                   # Utility classes
│   └── RequestUtils.java
└── constant/
    └── ErrorMessages.java

External Libraries (from Maven local repository):
├── common-utils-java/       # StringUtils, utilities
└── common-exception-java/   # RateLimitExceededException, ErrorResponse
```

## 🚀 Quick Start

```bash
# Compile the application
mvn clean compile

# Run the application
mvn spring-boot:run

# Test the basic endpoint
curl http://localhost:8080/hello
# Response: Hello, World!

# Test filter layer rate limiting (10 req/min)
for i in {1..12}; do curl -s http://localhost:8080/hello; done
# Requests 1-10: HTTP 200
# Requests 11+: HTTP 429 (Too Many Requests)

# Test external API with service layer
curl "http://localhost:8080/api/external?apiName=default"

# Test premium API
curl http://localhost:8080/api/premium
```

## 🛡️ Rate Limiting Architecture

### Filter Layer (Global Protection)
- **Location**: `RateLimitingFilter`
- **Limit**: 10 requests per minute (configurable)
- **Scope**: All endpoints
- **Purpose**: DDoS protection

### Service Layer (Per-Endpoint)
- **Location**: `RateLimiterService`
- **Configurable limits** via `application.properties`:
  - `hello`: 20 req/min
  - `external-default`: 20 req/min
  - `external-slow`: 5 req/min
  - `external-fast`: 50 req/min
  - `premium`: 100 req/min
- **Client-specific overrides** supported

### Error Response Format
```json
{
  "id": "7d8921e9-b7fd-49d8-a394-a59d668916df",
  "errorCode": 429,
  "errorMessage": "Too Many Requests",
  "detailMessage": "Rate limit exceeded. Try again later.",
  "timestamp": "2026-03-27T07:40:18.368396Z"
}
```

## 📚 Documentation

- **AI Agent Documentation**: See [.ai/README.md](.ai/README.md)
  - Project conventions
  - Coding standards
  - Pre-PR requirements
- **PR Guidelines**: See [.github/PULL_REQUEST_TEMPLATE.md](.github/PULL_REQUEST_TEMPLATE.md)

## 🔧 Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 4.0.0 | Web framework |
| Java | 26 (Preview) | Language |
| Bucket4j | 8.17.0 | Rate limiting |
| Lombok | Latest | Boilerplate reduction |
| Maven | 3.9+ | Build tool |
| Jackson | Latest | JSON serialization |

## 📦 Dependencies

### Shared Libraries

This project uses shared libraries to avoid code duplication across microservices:

#### common-utils-java
- **Purpose**: Utility classes (StringUtils, pagination, validation)
- **Location**: Local Maven repository (`~/.m2/repository/com/dev/`)
- **Classes Used**: `StringUtils`
- **Repository**: https://github.com/RizkiRachman/common-utils-java

#### common-exception-java
- **Purpose**: Standardized exceptions and error responses
- **Location**: Local Maven repository (`~/.m2/repository/com/dev/`)
- **Classes Used**: `RateLimitExceededException`, `ErrorResponse`
- **Repository**: https://github.com/RizkiRachman/common-exception-java

### Installation

```bash
# Install common-utils-java to local Maven repo
cd /path/to/common-utils-java
mvn clean install

# Install common-exception-java to local Maven repo
cd /path/to/common-exception-java
mvn clean install

# Then build this project
mvn clean compile
```

## 🎯 Java 26 Features

- ✅ **Records** (Stable) - Used in ErrorResponse
- ✅ **Switch Expressions** (Stable) - Used in configuration
- 🔍 **Preview Features Enabled**:
  - String Templates (when stable)
  - Unnamed Variables
  - Pattern Matching enhancements

## 🧪 Testing

We have **50 comprehensive unit tests** covering application components. Shared libraries (common-utils-java and common-exception-java) have their own test suites.

### Quick Start

```bash
# Run all tests
mvn clean test

# Generate beautiful HTML report (auto-generated after tests)
open target/test-report/index.html
```

### Test Summary

| Component | Tests | Coverage | Status |
|-----------|-------|----------|--------|
| RateLimiterProperties | 13 | 100% | ✅ |
| RateLimiterService | 13 | 100% | ✅ |
| HelloController | 5 | 100% | ✅ |
| RequestUtils | 6 | 100% | ✅ |
| RateLimiter | 5 | 100% | ✅ |
| ErrorMessages | 5 | 100% | ✅ |
| GlobalExceptionHandler | 3 | 100% | ✅ |
| **TOTAL** | **50** | **100%** | **✅** |

### Shared Library Tests

| Library | Tests | Coverage | Status |
|-----------|-------|----------|--------|
| common-utils-java | 155+ | 90%+ | ✅ |
| common-exception-java | 43 | 90%+ | ✅ |

### HTML Test Report

Beautiful HTML report is **automatically generated** after every test run!

**Features:**
- Summary cards (Total, Passed, Failed, Errors, Time)
- Visual progress bar with pass rate
- Detailed test class breakdown
- Color-coded status indicators
- Execution time statistics

```bash
# Run tests - report generates automatically!
mvn clean test

# View report
open target/test-report/index.html
```

**Note:** Report generation is integrated into Maven build via Exec Plugin. No manual steps needed!

**Note**: JaCoCo coverage is temporarily disabled due to Java 26 preview incompatibility.

📖 **Complete testing documentation**: See [docs/testing/README.md](docs/testing/README.md)

## 🤝 Contributing

Please follow our [PR template](.github/PULL_REQUEST_TEMPLATE.md) when submitting changes.

### Pre-PR Checklist
- [ ] Clean compile (`mvn clean compile -q`)
- [ ] Run tests (`mvn test`)
- [ ] All 50 tests must pass
- [ ] Start application locally (`mvn spring-boot:run`)
- [ ] Manual testing completed
- [ ] No breaking changes

### Test Requirements
- **Minimum**: 90% code coverage (when JaCoCo enabled)
- **Target**: 100% for business logic
- **Required**: Unit tests for all public methods
- **Required**: Edge case testing
- **Required**: Exception scenario testing

---

*Built with ❤️ using Spring Boot, Java 26, and Shared Libraries*
*50 tests passing ✅*
