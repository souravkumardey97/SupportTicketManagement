# Java & Spring Boot Guidelines

- Use Java 21 features where they improve readability and maintainability.
- Follow clean code, SOLID principles, and standard Spring Boot conventions.
- Prefer constructor-based dependency injection; avoid field injection.
- Keep controllers thin; put business logic in services.
- Use repositories only for data-access operations.
- Use DTOs for API requests/responses; do not expose entities directly.
- Use meaningful names and keep classes/methods focused on a single responsibility.
- Use `@RestControllerAdvice` for centralized exception handling.
- Use `@Transactional` only where transaction boundaries are required.
- Validate inputs using Jakarta Bean Validation.
- Use `application.yml`/properties and environment variables for configuration.
- Use appropriate logging; never log passwords, tokens, secrets, or sensitive data.
- Write simple, readable, maintainable code and avoid unnecessary abstractions.
- Consider performance, security, and scalability when implementing features.