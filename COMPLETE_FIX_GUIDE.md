# Complete Fix Guide for Appointment Booking 500 Errors

## Problem Summary

Your appointment booking system had **two critical UUID format mismatches**:

1. **Doctors table**: Had BINARY(16) UUIDs, but Hibernate expected VARCHAR(36) string UUIDs
2. **Appointments table**: Had BINARY(16) UUIDs for `appointment_id`, `patient_id`, `doctor_id` columns
3. **Backend routing**: Old backend process was running without the updated `AppointmentController` code

### Error Messages Explained

- `"No static resource appointments/my."` → Spring Boot wasn't routing to controller (old backend process)
- `"Data too long for column 'doctor_id'"` → BINARY(16) column can't hold UUID() string values
- BLOB display in MySQL Workbench → Confirms BINARY data type instead of VARCHAR

---

## Complete Fix Steps (In Order)

### Step 1: Fix Doctors Table ✅ (Already Done)

You already ran `fix-doctors-table.sql` which:
- Changed `doctor_id` to VARCHAR(36)
- Deleted old BINARY doctors
- Inserted 5 new doctors with string UUIDs

**Verification**: Your doctors table now has 5 rows with proper string UUIDs.

---

### Step 2: Fix Appointments Table Schema

**Run this SQL script in MySQL Workbench:**

File: `F:\MediWay\backend\scripts\fix-appointments-table.sql`

```sql
USE mediwaydb;

SET @OLD_SQL_SAFE_UPDATES = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

-- Delete BINARY appointments (can't be read by Hibernate)
DELETE FROM appointments WHERE 1=1;

SET SQL_SAFE_UPDATES = @OLD_SQL_SAFE_UPDATES;

-- Fix UUID columns to VARCHAR(36)
ALTER TABLE appointments MODIFY COLUMN appointment_id VARCHAR(36) NOT NULL;
ALTER TABLE appointments MODIFY COLUMN patient_id VARCHAR(36) NOT NULL;
ALTER TABLE appointments MODIFY COLUMN doctor_id VARCHAR(36) NOT NULL;

-- Verify
SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'mediwaydb' AND TABLE_NAME = 'appointments'
AND COLUMN_NAME IN ('appointment_id', 'patient_id', 'doctor_id');
```

**Expected output**: All three columns should show `DATA_TYPE = varchar`, `COLUMN_TYPE = varchar(36)`.

---

### Step 3: Restart Backend with Updated Code

The backend needs to restart to load the `AppointmentController` fix (UserRepository injection for email→UUID lookup).

**In PowerShell** (in the directory where backend is running):

1. **Stop current backend** (if running):
   - Press `Ctrl+C` in the terminal running `mvnw.cmd spring-boot:run`
   - Or kill the Java process from Task Manager

2. **Start fresh**:
   ```powershell
   cd F:\MediWay\backend
   .\mvnw.cmd spring-boot:run
   ```

3. **Wait for startup** — look for this line:
   ```
   Started MediWayBackendApplication in X.XXX seconds
   ```

4. **Keep the terminal open** — don't close it or the backend stops.

---

### Step 4: Test Frontend

1. **Hard refresh frontend** in browser: `Ctrl + Shift + R` (or `Ctrl + F5`)

2. **Test doctors endpoint**:
   - Navigate to **Book Appointment** page
   - Open browser console (F12)
   - You should see: `GET http://localhost:8080/api/appointments/doctors 200 OK`
   - Doctors dropdown should populate with 5 doctors

3. **Book an appointment**:
   - Select a doctor, date, time, reason
   - Click "Book Appointment"
   - Should succeed and redirect to Appointments page

4. **Test appointments endpoint**:
   - On **Appointments** page
   - Should see: `GET http://localhost:8080/api/appointments/my 200 OK`
   - Your booked appointment should display

---

## What Changed in Backend Code

### `AppointmentController.java` — Fixed Authentication

**Before** (caused 500 error):
```java
UUID patientId = UUID.fromString(authentication.getName());
// Tried to parse email as UUID → IllegalArgumentException!
```

**After** (correct):
```java
UUID patientId = getUserIdFromAuthentication(authentication);

private UUID getUserIdFromAuthentication(Authentication authentication) {
    String email = authentication.getName();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return user.getUserId();
}
```

**Why this works**:
- JWT token stores **email** as username (e.g., "tester1@gmail.com")
- We look up the User entity by email to get their UUID
- Then use that UUID for database queries

---

## Database Schema After Fix

### Doctors Table
```
doctor_id         VARCHAR(36)  PRIMARY KEY
name              VARCHAR(100)
specialization    VARCHAR(100)
email             VARCHAR(100)
phone             VARCHAR(20)
qualification     VARCHAR(200)
experience_years  INT
consultation_fee  DECIMAL(10,2)
available         BOOLEAN
created_at        TIMESTAMP
updated_at        TIMESTAMP
```

### Appointments Table
```
appointment_id    VARCHAR(36)  PRIMARY KEY
patient_id        VARCHAR(36)  (references users.user_id)
doctor_id         VARCHAR(36)  (references doctors.doctor_id)
appointment_date  DATE
appointment_time  TIME
status            ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED')
reason            VARCHAR(500)
notes             VARCHAR(1000)
consultation_fee  DECIMAL(10,2)
created_at        TIMESTAMP
updated_at        TIMESTAMP
```

**All UUID columns are now VARCHAR(36)** to match Hibernate's default UUID mapping.

---

## Troubleshooting

### "No static resource appointments/my" still appears
- **Cause**: Backend hasn't restarted or old process still running
- **Fix**: 
  1. Kill all Java processes: Task Manager → Details tab → find java.exe processes → End Task
  2. Restart backend: `cd F:\MediWay\backend; .\mvnw.cmd spring-boot:run`

### "Data too long for column" error when inserting
- **Cause**: Column is still BINARY(16) or too small
- **Fix**: Run `fix-appointments-table.sql` again to ALTER columns to VARCHAR(36)

### Appointments show as BLOB in MySQL Workbench
- **Cause**: UUIDs are stored as BINARY instead of VARCHAR
- **Fix**: Delete those rows and create new appointments via frontend UI

### "User not found with email" error
- **Cause**: Logged in user doesn't exist in database
- **Fix**: Register a new account via frontend

---

## Summary Checklist

- [x] Doctors table schema fixed (VARCHAR(36) for doctor_id)
- [x] 5 sample doctors inserted with string UUIDs
- [ ] **Appointments table schema fixed** (run `fix-appointments-table.sql`)
- [ ] **Old BINARY appointments deleted** (done by script above)
- [ ] **Backend restarted** with updated controller code
- [ ] **Frontend tested**: doctors load, booking works, appointments display

---

## Next Actions for You

1. **Run `fix-appointments-table.sql`** in MySQL Workbench
   - File location: `F:\MediWay\backend\scripts\fix-appointments-table.sql`
   - Verify output shows VARCHAR(36) for UUID columns

2. **Restart backend** (if not already running)
   ```powershell
   cd F:\MediWay\backend
   .\mvnw.cmd spring-boot:run
   ```

3. **Test in browser**:
   - Hard refresh (Ctrl+Shift+R)
   - Go to Book Appointment page
   - Select doctor and book
   - Check Appointments page

4. **Report results**: Let me know if you see any errors or if booking works!

---

## Why This Happened

**Root cause**: Mixing **BINARY(16) UUID storage** (via SQL scripts using `UNHEX()`) with **Hibernate's VARCHAR(36) UUID mapping** (default for `@GeneratedValue(strategy = GenerationType.UUID)`).

**Lesson learned**: When using Hibernate with MySQL:
- Let Hibernate manage UUID generation via `@GeneratedValue`
- If inserting via SQL, use `UUID()` function (returns string format)
- Never use `UNHEX(REPLACE(UUID(), '-', ''))` unless you add custom JPA converters
- Match your SQL data types to what Hibernate expects

**For future**: Create entities via backend REST APIs instead of manual SQL inserts — Hibernate will handle UUID format correctly.
