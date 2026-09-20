#!/usr/bin/env bash
# Optional helper: set bootstrap env vars and restart the Spring Boot application.
# Primary admin creation is AdminUserBootstrapRunner on startup when no ADMIN exists.
set -euo pipefail

echo "Set these environment variables before starting the backend:"
echo "  APP_BOOTSTRAP_ADMIN_USERNAME (default: admin)"
echo "  APP_BOOTSTRAP_ADMIN_PASSWORD (required when database has no ADMIN)"
echo "  APP_JWT_SECRET (min 32 characters)"
echo ""
echo "Example:"
echo "  export APP_BOOTSTRAP_ADMIN_PASSWORD='your-local-password'"
echo "  export APP_JWT_SECRET='your-local-jwt-secret-at-least-32-chars'"
echo "  cd backend && ./mvnw spring-boot:run"
