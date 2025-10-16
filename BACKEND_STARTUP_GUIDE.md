# MediWay Backend - Startup Instructions

## Problem Summary
The backend needs to be restarted with the updated `AppointmentController.java` that has the correct endpoint mapping:
- **Fixed Mapping**: `/appointments` (instead of `/api/appointments`)
- **Reason**: `application.properties` already adds `/api` prefix via `server.servlet.context-path=/api`
- **Result**: Endpoints will be at `http://localhost:8080/api/appointments/*`

## Current Issues
1. ✅ **FIXED**: AppointmentController mapping corrected in source code
2. ❌ **PENDING**: Backend needs restart to load updated controller class
3. ❌ **PENDING**: Appointments table schema still needs SQL fix (BINARY → VARCHAR)

## Step-by-Step Fix

### Option 1: Using IntelliJ IDEA / Eclipse
1. Open the MediWay backend project in your IDE
2. Find `MediWayBackendApplication.java`
3. Right-click → **Run** or **Debug**
4. Wait for "Started MediWayBackendApplication" message in console
5. Proceed to testing

### Option 2: Using Command Line (Recommended)
1. **Open a NEW PowerShell window** (important!)
2. Run these commands:

```powershell
cd F:\MediWay\backend
.\mvnw.cmd clean spring-boot:run
```

3. **Wait** for this message:
   ```
   Started MediWayBackendApplication in X.XXX seconds
   ```

4. **Keep this window open** - don't close it!

### Option 3: Using Command Prompt
If PowerShell has issues, use Command Prompt (cmd.exe):

```cmd
cd F:\MediWay\backend
mvnw.cmd spring-boot:run
```

## Verify Backend is Running

Open a **different** PowerShell/Terminal and test:

```powershell
# Test 1: Health check
Invoke-WebRequest -Uri "http://localhost:8080/api/actuator/health" -UseBasicParsing

# Test 2: Doctors endpoint (requires authentication but should return 401, not 404)
Invoke-WebRequest -Uri "http://localhost:8080/api/appointments/doctors"
```

**Expected Results:**
- Test 1: Should return `200 OK` with `{"status":"UP"}`
- Test 2: Should return `401 Unauthorized` or `403 Forbidden` (NOT 404!)
  - 404 = endpoint not found (BAD)
  - 401/403 = endpoint found but needs auth (GOOD)

## After Backend Starts Successfully

### 1. Fix Appointments Table Schema
Run the SQL script in MySQL Workbench:
```
F:\MediWay\backend\scripts\fix-appointments-table.sql
```

This changes columns from BINARY(16) to VARCHAR(36).

### 2. Test Frontend
1. Open browser to your frontend (usually http://localhost:5173)
2. **Hard refresh**: Press `Ctrl + Shift + R`
3. Login with your test account
4. Navigate to **Book Appointment** page
5. Open browser console (F12)
6. Check for these requests:
   - ✅ `GET /api/appointments/doctors` → **200 OK** (should see 5 doctors)
   - ✅ `POST /api/appointments` → **201 Created** (when you submit booking)
   - ✅ `GET /api/appointments/my` → **200 OK** (your appointments list)

### 3. Test Booking an Appointment
1. Select a doctor from dropdown
2. Choose date and time
3. Enter appointment reason
4. Click **Book Appointment**
5. Should see success message
6. Check "My Appointments" page - new appointment should appear

## Troubleshooting

### If you see "404 Not Found" on `/api/appointments`:
- Backend is running OLD code without the fix
- Solution: Stop backend (Ctrl+C) and restart using Option 2 above

### If you see "500 Internal Server Error":
- Appointments table still has BINARY columns
- Solution: Run `fix-appointments-table.sql` in MySQL Workbench

### If Maven wrapper doesn't work:
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract to C:\maven
3. Add C:\maven\bin to your PATH
4. Use `mvn spring-boot:run` instead of `mvnw.cmd spring-boot:run`

## Files Changed
- ✅ `backend/src/main/java/com/mediway/backend/controller/AppointmentController.java`
  - Changed `@RequestMapping("/api/appointments")` to `@RequestMapping("/appointments")`
- ✅ `backend/scripts/fix-appointments-table.sql` (ready to run)

## What's Working vs What's Not

### ✅ Working:
- Doctor entity and table (VARCHAR(36) UUIDs)
- Doctors endpoint: `GET /api/appointments/doctors`
- User registration and login
- JWT authentication

### ❌ Not Working (until fix applied):
- Creating appointments (404 error)
- Listing appointments (404 error)
- Appointments table has BINARY columns (incompatible with Hibernate)

## Next Steps
1. **START BACKEND** using one of the options above
2. **VERIFY** backend is responding (health check returns 200)
3. **RUN SQL SCRIPT** to fix appointments table
4. **TEST FRONTEND** appointment booking flow
5. **REPORT RESULTS** - share any error messages you see
