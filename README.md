# Project Summary

- This project is a Spring Boot REST API for managing devices.
- Each device includes an id, name, brand, state, and creation time.
- The API enforces the main domain rules of the challenge, such as preventing updates or deletion when a device is `IN_USE`.
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