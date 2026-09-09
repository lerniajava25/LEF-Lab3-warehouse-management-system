# Enterprise Warehouse Management System



Laboration 3 — a Spring Boot REST API for managing a product warehouse, built with modern
Java (targeting **Java 26**) and **Spring Boot 4.1**. All data is held in memory and
processed with the Java Collections Framework and the Java Streams API. No external
database is used.

---

## How to run

Requirements: JDK 26 and Maven.

```bash
# run the application (starts on http://localhost:8080)
mvn spring-boot:run

# run all tests (JUnit 5 + Mockito)
mvn test
```

## Architecture

The application follows a clean, layered structure. Each layer has one job and depends
only on the layer beneath it:

```
Controller  ->  Service  ->  Repository  ->  in-memory store
(HTTP/JSON)     (business    (storage        (ConcurrentHashMap)
                 logic +      abstraction)
                 streams)
```

- **`controller`** — maps HTTP requests to service calls and returns the correct status
  codes. No business logic.
- **`service` (`WarehouseService`)** — all business logic: CRUD, validation, and every
  search / filter / aggregation / sort operation, implemented with Java Streams.
- **`repository`** — a `ProductRepository` interface with an `InMemoryProductRepository`
  implementation. Keeping storage behind an interface lets us mock it in the service
  tests.
- **`domain` (`Product`)** — the domain model, an immutable `record`.
- **`dto`** — request payloads with Bean Validation, plus small response records.
- **`exception`** — custom exceptions and a `@RestControllerAdvice` that turns them into
  clean HTTP error responses.

### Thread safety

Because the service runs on a web server, many request threads can touch the data at once.
Two decisions keep this safe:

1. Products are stored in a **`ConcurrentHashMap`**, not an `ArrayList`. A plain
   `ArrayList` is not thread-safe and could produce corrupted state or
   `ConcurrentModificationException`s under concurrent access.
2. `Product` is an **immutable record**. A value that never changes after construction
   cannot be seen in a half-updated state by another thread. "Updates" create a new
   `Product` and replace the old one in the map.

## API endpoints

| Method | Path | Purpose | Success status |
|--------|------|---------|----------------|
| POST | `/api/products` | Create a product | 201 Created |
| GET | `/api/products` | List all products | 200 OK |
| GET | `/api/products/{id}` | Get one product | 200 OK |
| PUT | `/api/products/{id}` | Update a product | 200 OK |
| DELETE | `/api/products/{id}` | Delete a product | 204 No Content |
| GET | `/api/products/category/{category}` | Filter by category | 200 OK |
| GET | `/api/products/low-stock?threshold=10` | Products below a stock level | 200 OK |
| GET | `/api/products/analytics/total-value` | Total value of all stock | 200 OK |
| GET | `/api/products/analytics/average-price` | Average price per category | 200 OK |
| GET | `/api/products/top?n=5&by=price` | Top N by `price` or `popularity` | 200 OK |

Error responses: `404 Not Found` for a missing product id, `400 Bad Request` for invalid
input (validation failures or bad parameters).

### Example: create a product

```bash
$body = @{
    name       = "Laptop"
    category   = "Electronics"
    price      = 15000.00
    quantity   = 5
    expiryDate = "2030-01-01"
    unitsSold  = 50
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -ContentType "application/json" -Body $body
```

## How the assignment requirements are met

| Requirement | Where |
|-------------|-------|
| Spring Boot REST API with CRUD + correct HTTP verbs/status codes | `ProductController` |
| Products stored in appropriate Collections | `InMemoryProductRepository` (`ConcurrentHashMap`) |
| Search & Filter with Streams (by category, low stock) | `WarehouseService.findByCategory`, `findLowStock` |
| Analysis & Aggregation with Streams (total value, avg price/category) | `WarehouseService.totalInventoryValue`, `averagePricePerCategory` |
| Sorting with `Stream.sorted()` (top N) | `WarehouseService.topNByPrice`, `topNByPopularity` |
| Unit tests (JUnit 5 + Mockito), happy path + edge cases | `WarehouseServiceTest`, `ProductControllerTest` |

## Note on money

Prices use `BigDecimal` rather than `double`. `BigDecimal` is the correct type for money:
it avoids floating-point rounding errors, and averages are rounded explicitly with
`RoundingMode.HALF_UP`.

---

### Division of labour

The three Stream-based feature groups are split one per person (as the teacher suggested),
each owning the feature end to end — the service methods, the matching endpoints, the
unit tests, and the corresponding note in this report.

| Member | Owns |
|--------|------|
| Elena  | Domain model + repository + CRUD endpoints; **Search & Filter** feature and its tests |
| Latifa | **Analysis & Aggregation** feature (total value, average price) and its tests |
| Filip      | **Sorting** feature (top N) and its tests; report section 2 (language comparison) |


