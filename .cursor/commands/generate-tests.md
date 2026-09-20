# Generate Tests

Generate tests for the selected code following the project testing guidelines.

Process:

1. Understand the class responsibility.
2. Identify public behavior.
3. Identify dependencies.
4. Identify happy paths.
5. Identify failure scenarios.
6. Identify edge cases.
7. Generate appropriate tests.

Use:

- JUnit 5 for testing
- Mockito for unit tests
- `@WebMvcTest` for controller tests
- `@SpringBootTest` for integration tests when required

Requirements:

- Follow Arrange-Act-Assert.
- Use descriptive test names.
- Test behavior rather than implementation details.
- Cover positive, negative, and important edge cases.
- Keep tests independent and deterministic.
- Do not modify production code unless explicitly requested.
- Do not create meaningless tests only to increase coverage.