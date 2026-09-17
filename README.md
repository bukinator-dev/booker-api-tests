# booker-api-tests

An OOP-driven REST API test automation framework for the
[restful-booker](https://restful-booker.herokuapp.com) demo API, built with
Java 25, REST Assured, JUnit 5, Lombok, Jackson and Allure.

## Running the tests

```bash
./gradlew test
```

Config (base URI, auth credentials, timeout) lives in
`src/test/resources/config.properties` and can be overridden per-run with
matching `-D` system properties, e.g.:

```bash
./gradlew test -Dbase.uri=https://staging.example.com
```

## Viewing the Allure report

```bash
./gradlew test allureReport
open build/reports/allure-report/allureReport/index.html
```

Or, to launch a local server serving the report directly:

```bash
./gradlew allureServe
```

Every HTTP call is captured by the `AllureRestAssured` filter, so each test
step in the report has the full request/response (headers, body, status)
attached.

## Architecture

```
src/test/java/com/booker/api/
├── config/    typed, externalized environment config (Singleton)
├── client/    HTTP transport + resource clients (Composition)
├── auth/      auth token acquisition/caching
├── model/     request/response POJOs (Builder)
├── data/      test data factories (Factory)
├── base/      shared JUnit lifecycle (Inheritance)
└── tests/     the actual test classes, grouped by resource
```

### Why it's built this way

- **`ApiClient` (encapsulation)** — the only class that touches REST Assured's
  `given()`. It owns the shared `RequestSpecification` (base URI, JSON content
  type, a `LocalDate`-aware Jackson mapper, the Allure reporting filter) and
  exposes plain `get/post/put/patch/delete` methods returning `Response`.
- **`BookingApiClient` / `AuthApiClient` (composition over inheritance)** —
  each *has-a* `ApiClient` field rather than extending a shared base client.
  Their only job is mapping domain operations (`createBooking`,
  `deleteBooking`, ...) onto HTTP calls. There is no generic base-client
  class to force an artificial `is-a` relationship where none exists.
- **`BaseTest` (inheritance, used deliberately)** — the one genuine `is-a`
  relationship in the framework. Every test class extends it to share a
  single `@BeforeAll` that wires the whole client graph, avoiding boilerplate
  duplication across test classes.
- **`Booking` / `BookingDates` / `AuthRequest` (builder pattern)** — Lombok
  `@Builder @Jacksonized` models replace hand-written JSON text blocks with
  fluent, typed construction, e.g.
  `Booking.builder().firstname("Jim")...build()`.
- **`BookingDataFactory` (factory pattern)** — centralizes what a *valid* vs.
  *invalid* booking payload means for this suite (`validBooking()`,
  `missingFirstnameBooking()`, `negativeTotalPriceBooking()`), built on top of
  the model builders. The factory owns intent; the builder owns assembly.
- **`ConfigProvider` (singleton)** — loads `config.properties` once behind a
  private constructor and static holder, exposing an immutable
  `EnvironmentConfig` record. Nothing else in the framework parses
  properties/env vars directly.
- **`AuthTokenManager`** — lazily fetches and caches the `/auth` token for the
  whole suite, since every authenticated write (PUT/PATCH/DELETE) can reuse
  it instead of re-authenticating per test.

### Client return type

All client methods return the raw REST Assured `Response`, not deserialized
POJOs — deserialization happens in the test itself (`response.as(Booking
Response.class)`) for positive cases, while negative cases assert directly on
`response.getStatusCode()` without any deserialization exception getting in
the way.

## Test coverage

| Class | Covers |
|---|---|
| `PingTest` | `GET /ping` liveness check |
| `BookingRetrievalTest` | list ids, filter by name, get by id, invalid id → 404 |
| `BookingLifecycleTest` | full create → get → PUT → PATCH → delete → verify-gone flow |
| `BookingCreationValidationTest` | valid create; missing required field; negative price |
| `BookingAuthorizationTest` | PUT/PATCH/DELETE rejected (403) without/with an invalid token |

Negative-path expectations (e.g. a missing `firstname` returning `500`, a
negative `totalprice` being accepted rather than rejected) were verified
against the live API before being asserted, rather than assumed.

Every test class that creates bookings deletes them again in `@AfterEach`, to
keep the shared public demo API's data tidy across runs.

## CI

`.github/workflows/ci.yml` runs `./gradlew test` on every push/PR and uploads
the Allure results as a build artifact.
