# Spring Boot Playground

A playground project for experimenting with Spring Boot features and best practices.

## 🚀 Features

- ✨ REST API with Spring Boot
- 🛡️ Rate Limiting (Bucket4j)
- ⚡ Exception Handling (@ControllerAdvice)
- 📝 Configuration Properties
- 🔧 Constructor Injection
- 📊 Global Exception Handling

## 🏗️ Architecture

```
├── src/main/java/com/example/springbootplayground/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST controllers
│   ├── service/         # Business logic
│   ├── dto/            # Data transfer objects
│   ├── exception/      # Custom exceptions & handlers
│   └── util/           # Utility classes
├── src/main/resources/
│   └── application.properties
└── .github/
    └── PULL_REQUEST_TEMPLATE.md
```

## 🚀 Quick Start

```bash
# Run the application
mvn spring-boot:run

# Test the API
curl http://localhost:8080/hello

# Rate limiting test (10 req/min)
for i in {1..12}; do curl http://localhost:8080/hello; done
```

## 📚 Documentation

- **AI Agent Documentation**: See [.ai/README.md](.ai/README.md)
- **PR Guidelines**: See [.github/PULL_REQUEST_TEMPLATE.md](.github/PULL_REQUEST_TEMPLATE.md)

## 🔧 Technologies

- Spring Boot 4.0.0
- Java 17
- Bucket4j (Rate Limiting)
- Maven

## 🤝 Contributing

Please follow our [PR template](.github/PULL_REQUEST_TEMPLATE.md) when submitting changes.
