
# MediWay Backend: Individual Unit Test Execution Guide

Each core function has a dedicated unit test class. Every team member can run the test for their assigned function independently for demonstration/viva.

# Navigate to backend directory first:
cd F:\MediWay\backend

# 1. Appointment Scheduling
.\mvnw test -Dtest=com.mediway.backend.service.AppointmentServiceTest

# 2. Admin/Doctor Management
.\mvnw test -Dtest=com.mediway.backend.service.DoctorServiceTest

# 3. Statistical Reports
.\mvnw test -Dtest=com.mediway.backend.service.ReportsServiceTest

# 4. Medical Records
.\mvnw test -Dtest=com.mediway.backend.service.MedicalRecordServiceTest

# 5. Payment Handling
.\mvnw test -Dtest=com.mediway.backend.controller.SimplePayPalControllerTest

# Or run ALL tests:
.\mvnw test

## 1. Appointment Scheduling (Doctor scheduling, booking, concurrency)
- **Test Class:** `com.mediway.backend.service.AppointmentServiceTest`
- **How to Run:**

```powershell
# Run with fully qualified class name (RECOMMENDED):
.\mvnw test -Dtest=com.mediway.backend.service.AppointmentServiceTest

# Or run all tests:
.\mvnw test
```

---

## 2. Admin Management (Users/Doctors CRUD) & Statistical Reports (PDF/CSV analytics)
- **Test Class (Admin):** `com.mediway.backend.service.DoctorServiceTest`
- **Test Class (Reports):** `com.mediway.backend.service.ReportsServiceTest`
- **How to Run:**

```powershell
# Run Doctor/Admin tests:
.\mvnw test -Dtest=com.mediway.backend.service.DoctorServiceTest

# Run Reports tests:
.\mvnw test -Dtest=com.mediway.backend.service.ReportsServiceTest

# Run both together:
.\mvnw test -Dtest=com.mediway.backend.service.DoctorServiceTest,com.mediway.backend.service.ReportsServiceTest
```

---

## 3. Patient Medical Records (CRUD: diagnoses, treatments, prescriptions)
- **Test Class:** `com.mediway.backend.service.MedicalRecordServiceTest`
- **How to Run:**

```powershell
# Run Medical Records tests:
.\mvnw test -Dtest=com.mediway.backend.service.MedicalRecordServiceTest
```

---

## 4. Payment Handling (Gateway integration, transactions, receipts)
- **Test Class:** `com.mediway.backend.controller.SimplePayPalControllerTest`
- **How to Run:**

```powershell
# Run Payment/PayPal tests:
.\mvnw test -Dtest=com.mediway.backend.controller.SimplePayPalControllerTest
```

---

## Run All Unit Tests

```powershell
# Run all 4 test suites together:
.\mvnw test -Dtest=com.mediway.backend.service.AppointmentServiceTest,com.mediway.backend.service.DoctorServiceTest,com.mediway.backend.service.ReportsServiceTest,com.mediway.backend.service.MedicalRecordServiceTest,com.mediway.backend.controller.SimplePayPalControllerTest

# Or simply run all tests in the project:
.\mvnw test
```

---

## Troubleshooting
- **Always use the fully qualified class name** with `com.mediway.backend` prefix
- The correct format is: `.\mvnw test -Dtest=FullyQualifiedClassName`
- **DO NOT use spaces** around the `-Dtest=` parameter
- If you see `No tests matching pattern ... were executed!`, verify:
  - The test file exists in `src/test/java/com/mediway/backend/`
  - You're using the correct fully qualified name
  - The test class has `@Test` annotated methods
- To list all available test classes:
  ```powershell
  Get-ChildItem -Recurse -Filter *Test.class .\target\test-classes\
  ```
- For full test suite: `.\mvnw test`
- To see detailed test output: `.\mvnw test -X`
- To skip tests during build: `.\mvnw package -DskipTests`

---

## Test Coverage Summary

Each test file contains 15-20 comprehensive test cases covering:
- ✅ **Positive test cases** - Normal operation scenarios
- ✅ **Negative test cases** - Error handling and edge cases
- ✅ **Edge cases** - Empty data, null values, boundary conditions
- ✅ **CRUD operations** - Create, Read, Update, Delete
- ✅ **Business logic** - Status transitions, validations, calculations

---

## Notes
- All test classes are in `src/test/java/com/mediway/backend/`
- Tests use Mockito for mocking dependencies
- Tests use JUnit 5 for test framework
- Each test is annotated with `@DisplayName` for clear description
- Use the exact command for your assigned function to run only your tests

---

**Contact your dev lead for troubleshooting or advanced test configuration.**
