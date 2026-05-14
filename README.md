# Project Summary

- This project is a Spring Boot REST API for managing devices.
- Each device includes an id, name, brand, state, and creation time.
- The API enforces the main domain rules of the challenge, such as preventing updates or deletion when a device is `IN_USE`.
- The API ensures that the creation_time field will never be updated
- The application uses PostgreSQL for persistence and can be run locally with Docker Compose.

# Run the application

- Make sure Docker is running.
- Open a terminal in the project root.
- Start the application and database with:

```bash
docker compose up --build
```

- Wait until the containers are up and the Spring Boot app has started.
- The application will be available at:

```text
http://localhost:8080
```

- To stop the application:

```bash
docker compose down
```

# Tests

- Basic automated tests were added for both the service layer and the API endpoints.
- The service tests cover key business rules such as default device state, not-found handling, paginated listing, partial updates (including name-only or brand-only), invalid partial payloads, and conflict cases.
- The controller tests cover the main endpoint responses, including success, validation, not-found, bad-request, and conflict scenarios.
- To run the tests locally:

```bash
mvn clean test
```

# API Contracts

- See the API contract details in [`API_CONTRACTS.md`](./API_CONTRACTS.md).

# API Calls For Testing

- See the test call examples in [`API_TEST_CALLS.md`](./API_TEST_CALLS.md).

# Future work

Some future work I would add to the project if it was going on production would be the following:

- **Logging**: structured logs, sensible log levels, and any other important info that will help trace a call through the stack.
- **Metrics and health**: **Micrometer** metrics (latency, error rates) and dashboards (**Grafana** or your cloud vendor), plus explicit **readiness/liveness** probes if this runs on Kubernetes.
- **API discoverability**: **OpenAPI** (Swagger UI) generated from the code or maintained alongside `API_CONTRACTS.md`, if consumers need a machine-readable contract.
- **Integration tests**: **Testcontainers** (or similar) for tests against a real PostgreSQL instance, complementing the current unit/MockMvc tests.
- **Security**: I would secure this API with **authentication and authorization** using a **client-credentials** style flow: each client would authenticate with a **client id and secret**, obtain an access token, and send **`Authorization: Bearer <token>`** on **every** request so the server can verify identity and enforce access rules.