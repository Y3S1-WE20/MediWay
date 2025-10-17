# Quick Test Commands Reference Card

## 🚀 Individual Test Commands (Copy & Paste)

### 1️⃣ Appointment Scheduling Tests (15 tests)
```powershell
.\mvnw test "-Dtest=com.mediway.backend.service.AppointmentServiceTest"
```

### 2️⃣ Doctor/Admin Management Tests (15 tests)
```powershell
.\mvnw test "-Dtest=com.mediway.backend.service.DoctorServiceTest"
```

### 3️⃣ Statistical Reports Tests (20 tests)
```powershell
.\mvnw test "-Dtest=com.mediway.backend.service.ReportsServiceTest"
```

### 4️⃣ Medical Records Tests (20 tests)
```powershell
.\mvnw test "-Dtest=com.mediway.backend.service.MedicalRecordServiceTest"
```

### 5️⃣ Payment Handling Tests (4 tests)
```powershell
.\mvnw test "-Dtest=com.mediway.backend.controller.SimplePayPalControllerTest"
```

---

## 🎯 Run All Main Tests Together
```powershell
.\mvnw test "-Dtest=com.mediway.backend.service.AppointmentServiceTest,com.mediway.backend.service.DoctorServiceTest,com.mediway.backend.service.ReportsServiceTest,com.mediway.backend.service.MedicalRecordServiceTest,com.mediway.backend.controller.SimplePayPalControllerTest"
```

---

## 🔥 Run ALL Tests
```powershell
.\mvnw test
```

---

## 📋 Before Running Tests

1. Open PowerShell
2. Navigate to backend directory:
   ```powershell
   cd F:\MediWay\backend
   ```
3. Run your desired test command from above

---

## ✅ Expected Results

Each test should show:
- `Tests run: X, Failures: 0, Errors: 0, Skipped: 0`
- `BUILD SUCCESS`

**Total Tests Available:** 85 tests
**Status:** All tests passing ✅

---

## 🐛 Troubleshooting

If test fails:
1. Make sure you're in the `backend` directory
2. **IMPORTANT:** Always use quotes around the `-Dtest=...` parameter
3. Copy the commands exactly as shown (with quotes)
4. Check that MySQL is running (for integration tests)
5. Verify Java 17 is installed: `java -version`

**Common Issue:** If you see error about "Unknown lifecycle phase", you forgot the quotes!
- ❌ Wrong: `.\mvnw test -Dtest=ClassName`
- ✅ Correct: `.\mvnw test "-Dtest=ClassName"`

---

## 📖 More Details

See `TESTING.md` for detailed documentation.
See `UNIT_TESTING_SUMMARY.md` for comprehensive test coverage report.
