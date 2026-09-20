# Testing Guidelines

- Use JUnit 5 for testing.
- Use Mockito for unit-test mocking where appropriate.
- Follow Arrange-Act-Assert (AAA).
- Test behavior, not implementation details.
- Cover happy paths, negative scenarios, and important edge cases.
- Keep tests independent, deterministic, and readable.
- Use `@WebMvcTest` for controller-focused tests.
- Use unit tests for service/business logic.
- Use `@SpringBootTest` for integration scenarios that require the application context.
- Use Testcontainers when realistic database/infrastructure integration is required.
- Do not mock the class under test.
- Avoid unnecessary mocking and meaningless coverage.
- Use descriptive test names such as `shouldCreateOrderSuccessfully()`.
- Tests should be updated whenever application behavior changes.