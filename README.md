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

# API Contracts

- See the API contract details in [`API_CONTRACTS.md`](./API_CONTRACTS.md).

# API Calls For Testing

- See the test call examples in [`API_TEST_CALLS.md`](./API_TEST_CALLS.md).