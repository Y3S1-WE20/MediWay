# Payment: SOLID Principles & How to Run Payment Tests

This document explains how SOLID principles are applied to the payment-related code in this project and gives practical instructions for running payment-related tests (backend) from the repo.

---

## Quick summary
- Location of payment implementation (backend): `backend/src/main/java/com/mediway/backend/`
  - Key files:
    - `controller/SimplePayPalController.java` — main controller handling PayPal create/execute/cancel and receipt endpoints.
    - `entity/Payment.java` — JPA entity representing a payment.
    - `repository/PaymentRepository.java` — Spring Data JPA repository for Payment persistence.
    - `repository/AppointmentRepository.java` — used to update appointment status when payments complete.
- Tests are under `backend/src/test/java/com/mediway/backend/`.
  - Relevant tests for payment flows include (non-exhaustive):
    - `SimplePayPalControllerTest` (controller unit tests using MockMvc & Mockito)
    - `ReceiptControllerTest` (receipt-related flows)
    - `PaymentEntityTest` (entity-level unit tests)
    - `ReportsServiceTest` (if reports rely on payment data)

---

## SOLID principles (mapped to payment code)
Below are the SOLID principles with a short explanation and how they map to the payment-related files in this codebase.

1) Single Responsibility Principle (SRP)
- What: A class should have one reason to change; it should do one job.
- How it appears here:
  - `Payment` (entity) is only responsible for modeling payment data (fields, lifecycle hooks like `@PrePersist`).
  - `PaymentRepository` is only responsible for database access for Payment (query methods like `findByUserId`).
  - `SimplePayPalController` focuses on HTTP request handling for payment flows (create/execute/cancel, get payments/receipts). It contains some payment orchestration logic — in a larger codebase you might extract business logic into a `PaymentService` to further improve SRP.

2) Open/Closed Principle (OCP)
- What: Classes should be open for extension but closed for modification.
- How it appears here:
  - `PaymentRepository` uses Spring Data JPA; query methods can be added or extended without modifying existing consumers.
  - `SimplePayPalController` branches to simulated checkout when PayPal configuration is missing (via flags). A next step to better support OCP is to extract a `PaymentProvider` interface (PayPalProvider, SimulatedProvider) so new providers can be added without modifying controller logic.

3) Liskov Substitution Principle (LSP)
- What: Subtypes should be replaceable for their base types without breaking behavior.
- How it appears here:
  - The code uses Spring components and repositories (repositories are interfaces). If/when a subclassed provider or alternative repository implementation is used, the code expects repository contracts (`JpaRepository`) to behave consistently.

4) Interface Segregation Principle (ISP)
- What: Clients should not be forced to depend on methods they do not use.
- How it appears here:
  - Repositories expose limited query methods (`findByUserId`, `findByStatus`) used by callers. For complicated business logic you would add narrower service interfaces to prevent callers depending on unnecessary methods.

5) Dependency Inversion Principle (DIP)
- What: High-level modules should not depend on low-level modules; both should depend on abstractions.
- How it appears here:
  - The controller depends on `PaymentRepository` and `AppointmentRepository` (both are interfaces provided by Spring Data). This is an example of depending on abstractions rather than concrete repositories.
  - A future improvement would be extracting a `PaymentService` interface and injecting that into the controller; tests can then replace the service with a mock easily.

Notes / Practical suggestions:
- The codebase follows pragmatic separation already (entities, repositories, controllers). For larger scale maintainability, consider adding a `service` layer (e.g., `PaymentService`) to handle orchestration and business rules. This improves SRP and makes controller tests lighter (controller unit tests focus on HTTP mapping while business logic is covered in service unit tests).

---

## Payment-related tests: what they cover
- `SimplePayPalControllerTest` covers many edge-cases for the `SimplePayPalController` controller:
  - Creating payment with different input types (String/Integer for appointmentId / amount)
  - Handling missing user header
  - Handling existing/completed payments
  - Executing payments (numeric internal id, PayPal id, token-based flows)
  - Cancelling payments and confirming status changes
  - Getting 'my payments' and 'my receipts' flows
- `ReceiptControllerTest`, `PaymentEntityTest`, `ReportsServiceTest` cover receipt downloads, the Payment entity behavior and reports that depend on payments respectively.

---

## How to run payment-related tests (PowerShell / Windows)
Below are ready-to-run commands you can paste in a Windows PowerShell terminal. They assume you are at the repo root (`F:\MediWay`).

1) Run the two main controller tests (quick focused run)
```powershell
cd backend; .\mvnw.cmd test -Dtest="SimplePayPalControllerTest,ReceiptControllerTest"
```
This will run only the two named test classes.

2) Run an expanded set of payment-related tests
```powershell
cd backend; .\mvnw.cmd test -Dtest="SimplePayPalControllerTest,ReceiptControllerTest,PaymentEntityTest,ReportsServiceTest"
```

3) Run a single test class
```powershell
cd backend; .\mvnw.cmd test -Dtest=SimplePayPalControllerTest
```

4) Run all backend tests (longer)
```powershell
cd backend; .\mvnw.cmd test
```

Notes:
- The project includes a Maven wrapper (`mvnw.cmd` on Windows). Using the wrapper ensures the same Maven version across environments.
- The controller tests are unit tests using MockMvc and Mockito. They do not require the full Spring Boot app to be running and they mock repositories, so they are fast and safe to run locally.

---

## Environment & configuration notes (important for payment tests / runtime)
- The PayPal controller reads config values using `@Value` properties:
  - `paypal.client.id`, `paypal.client.secret`, `paypal.mode` and `paypal.use-simulated-checkout`.
- Tests are designed to run with simulated checkout and use Mockito mocks; you typically don't need live PayPal credentials to run unit tests.
- If you intend to exercise the live PayPal SDK in an integration test or manual run, set the properties appropriately (e.g., via `application.properties` or environment variables) and ensure network access and valid PayPal credentials.

---

## Troubleshooting
- If tests fail with classpath or JDK errors:
  - Verify `JAVA_HOME` points to a JDK 11/17 (depending on project config). On Windows PowerShell:
    ```powershell
    java -version
    echo $env:JAVA_HOME
    ```
- If Maven wrapper fails to run, try using an installed Maven:
  ```powershell
  cd backend; mvn test -Dtest=SimplePayPalControllerTest
  ```
- If tests fail because of missing beans or configuration when running integration tests:
  - Check if the test is a unit test (MockMvc + Mockito) or an integration test (uses Spring context). For unit tests the stack trace should show mocks; for integration tests you may need a running DB or an in-memory profile.
- To get detailed test output:
  ```powershell
  cd backend; .\mvnw.cmd -X test -Dtest=SimplePayPalControllerTest
  ```

---

## Quick checklist before running tests
- [ ] Ensure repository is at desired branch (e.g., `dev`) and up to date.
- [ ] `cd backend` to run backend tests with Maven wrapper.
- [ ] Use the test filter syntax `-Dtest="ClassName1,ClassName2"` to run specific classes.

---

## Helpful references in code
- Controller: `backend/src/main/java/com/mediway/backend/controller/SimplePayPalController.java`
- Entity: `backend/src/main/java/com/mediway/backend/entity/Payment.java`
- Repository: `backend/src/main/java/com/mediway/backend/repository/PaymentRepository.java`
- Tests: `backend/src/test/java/com/mediway/backend/controller/SimplePayPalControllerTest.java`

---

If you want, I can also:
- Add a one-command script file in the repo (e.g., `backend/run-payment-tests.ps1`) that runs the common commands with better output.
- Extract payment logic into a `PaymentService` and update tests accordingly (small refactor to further improve SRP/OCP).

If you'd like the PS1 script created now, tell me and I'll add it and update this doc with how to use it.