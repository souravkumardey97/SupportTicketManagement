# REST API Standards

- Follow RESTful principles and use resource-oriented URLs.
- Use plural nouns for resources.
- Use API versioning when appropriate, e.g. `/api/v1/orders`.
- Use HTTP methods according to their intended semantics.
- Use appropriate HTTP status codes: 200, 201, 204, 400, 401, 403, 404, 409, 500.
- Use DTOs for request and response models.
- Validate incoming requests using Jakarta Bean Validation.
- Return consistent and meaningful error responses.
- Support pagination for potentially large collections.
- Use query parameters for filtering, sorting, and pagination.
- Never expose passwords, tokens, secrets, or internal implementation details.
- APIs should be documented with request, response, status codes, and error scenarios.
- Maintain backward compatibility when changing existing API contracts.