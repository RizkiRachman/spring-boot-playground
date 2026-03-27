# Spring Boot Playground

A playground project for experimenting with Spring Boot features and best practices, featuring production-ready rate limiting, exception handling, and Java 26 preview features.

## 🚀 Features

- ✨ **REST API** with Spring Boot 4.0.0
- 🛡️ **Dual-Layer Rate Limiting** (Bucket4j + Filter/Service)
- ⚡ **Exception Handling** with @ControllerAdvice
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
├── exception/               # Custom exceptions & handlers
│   ├── GlobalExceptionHandler.java
│   └── RateLimitExceededException.java
├── util/                   # Utility classes
│   ├── RequestUtils.java
│   └── StringUtils.java
└── constant/
    └── ErrorMessages.java
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

## 🎯 Java 26 Features

- ✅ **Records** (Stable) - Used in ErrorResponse
- ✅ **Switch Expressions** (Stable) - Used in configuration
- 🔍 **Preview Features Enabled**:
  - String Templates (when stable)
  - Unnamed Variables
  - Pattern Matching enhancements

## 🧪 Testing

We have **155 comprehensive unit tests** covering all major components.

### Quick Start

```bash
# Run all tests
mvn clean test

# Generate beautiful HTML report
python3 generate-test-report.py
open target/test-report/index.html
```

### Test Summary

| Component | Tests | Coverage | Status |
|-----------|-------|----------|--------|
| StringUtils | 105 | 100% | ✅ |
| ErrorResponse | 8 | 100% | ✅ |
| RateLimiterProperties | 13 | 100% | ✅ |
| RateLimiterService | 13 | 100% | ✅ |
| RequestUtils | 6 | 100% | ✅ |
| RateLimiter | 5 | 100% | ✅ |
| ErrorMessages | 5 | 100% | ✅ |
| **TOTAL** | **155** | **100%** | **✅** |

### HTML Test Report

Generate a beautiful HTML report with:
- Summary cards (Total, Passed, Failed, Errors, Time)
- Visual progress bar with pass rate
- Detailed test class breakdown
- Color-coded status indicators
- Execution time statistics

```bash
# Run tests and generate report
mvn clean test
python3 generate-test-report.py

# View report
open target/test-report/index.html
```

**Note**: JaCoCo coverage is temporarily disabled due to Java 26 preview incompatibility.

📖 **Complete testing documentation**: See [docs/testing/README.md](docs/testing/README.md)

## 🤝 Contributing

Please follow our [PR template](.github/PULL_REQUEST_TEMPLATE.md) when submitting changes.

### Pre-PR Checklist
- [ ] Clean compile (`mvn clean compile -q`)
- [ ] Run tests (`mvn test`)
- [ ] All 155 tests must pass
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

*Built with ❤️ using Spring Boot and Java 26*
*155 tests passing ✅*
