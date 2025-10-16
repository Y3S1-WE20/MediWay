# 🚨 URGENT FIX REQUIRED - Appointment Booking Errors

## Current Problems

### 1. Doctor ID Error ❌
```
Doctor not found with ID: 35313439-3139-6532-2d61-6134612d33313131
```
This is a **HEX-ENCODED UUID** - proves database is still using BINARY format!

### 2. 404 Error on POST /api/appointments ❌
Backend is either:
- Not running, OR
- Running old code without controller fix

### 3. Root Cause
- **Doctors table**: May still have BINARY(16) doctor_id (causing hex-encoded IDs)
- **Appointments table**: Definitely has BINARY(16) columns (confirmed by previous errors)
- **Backend**: Not running with updated controller code

---

## 🔧 COMPLETE FIX (Do These in Order!)

### Step 1: Fix Database Schema (5 minutes)

#### Option A: Run Complete Fix Script (RECOMMENDED)
1. **Open MySQL Workbench**
2. **Connect to `mediwaydb`**
3. **File → Open SQL Script**
4. **Navigate to**: `F:\MediWay\backend\scripts\complete-uuid-fix.sql`
5. **Click ⚡ Execute** (lightning bolt icon)
6. **Check output** - should show:
   - BEFORE schema (may show BINARY)
   - Doctors with readable UUIDs
   - AFTER schema (should all be VARCHAR(36))
   - "✓ Schema fix complete!"

#### Option B: Run Just Appointments Fix
If doctors table is already correct (VARCHAR), run:
`F:\MediWay\backend\scripts\fix-appointments-table.sql`

### Step 2: Start Backend (2 minutes)

#### Method 1: Using Batch File (EASIEST)
1. **Double-click**: `F:\MediWay\backend\start-backend.bat`
2. **Wait** for: `Started MediWayBackendApplication in X.XXX seconds`
3. **Keep window open!**

#### Method 2: Using Command Prompt
1. **Open Command Prompt** (Win+R → `cmd`)
2. Run:
```cmd
cd F:\MediWay\backend
mvnw.cmd spring-boot:run
```
3. **Wait** for startup message
4. **Keep window open!**

#### Method 3: Using PowerShell (if above fail)
1. **Open NEW PowerShell window**
2. Run:
```powershell
cd F:\MediWay\backend
.\mvnw.cmd spring-boot:run
```

### Step 3: Verify Backend is Running

Open a **different** terminal and run:
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/actuator/health"
```

**Expected**: 
```
StatusCode: 200
Content: {"status":"UP"}
```

**If you get error**: Backend not running - go back to Step 2

### Step 4: Test Endpoints

In browser console (F12), check these URLs:
```javascript
// Test 1: Doctors endpoint
fetch('http://localhost:8080/api/appointments/doctors')
  .then(r => r.json())
  .then(d => console.log('Doctors:', d))

// Test 2: My appointments (requires login)
fetch('http://localhost:8080/api/appointments/my')
  .then(r => r.json())
  .then(d => console.log('My appointments:', d))
```

**Expected:**
- Test 1: Array of 5 doctors with readable UUIDs (e.g., `"514919e2-aa4a-11f0-8da8-089798c3ec81"`)
- Test 2: Array of appointments (empty if none created yet)

**NOT hex IDs like**: `35313439-3139-6532-2d61-6134612d33313131` ❌

### Step 5: Test Booking Appointment

1. **Hard refresh** frontend: `Ctrl + Shift + R`
2. **Navigate** to Book Appointment page
3. **Select** a doctor (should show readable name, not errors)
4. **Fill** date, time, reason
5. **Click** "Book Appointment"
6. **Check** console for:
   - ✅ `POST http://localhost:8080/api/appointments` → **201 Created**
   - ❌ NOT 404 or 500

---

## ✅ Success Criteria

After completing all steps, you should see:

### In MySQL Workbench:
```sql
-- Run this to verify:
SELECT doctor_id, name FROM doctors LIMIT 1;
```
**Should show**: `514919e2-aa4a-11f0-8da8-089798c3ec81` (readable UUID)
**NOT**: `0x35313439...` (binary/hex)

### In Backend Console:
```
Started MediWayBackendApplication in 6.xxx seconds
Tomcat started on port 8080 (http) with context path '/api'
```

### In Browser Console:
```javascript
// No errors like:
// ❌ "Doctor not found with ID: 35313439..."
// ❌ "404 Not Found"

// Instead:
// ✅ 200 OK responses
// ✅ Readable doctor names and IDs
```

---

## 🆘 Troubleshooting

### "Doctor not found with ID: 353134..." (Hex ID)
→ **Database still has BINARY columns**
→ **Solution**: Re-run `complete-uuid-fix.sql`

### "404 Not Found" on `/api/appointments`
→ **Backend running old code**
→ **Solution**: Stop backend (Ctrl+C), run Step 2 again

### "500 Internal Server Error"
→ **Appointments table schema mismatch**
→ **Solution**: Run SQL script from Step 1

### Backend won't start / crashes
→ **Check backend console for error messages**
→ **Common issues:**
  - Port 8080 already in use (kill other Java processes)
  - MySQL not running
  - Database connection error

### Maven wrapper error: "not recognized"
→ **Use Method 2** (Command Prompt with `mvnw.cmd`)
→ **Or install Maven** from https://maven.apache.org/

---

## 📊 What Each File Does

| File | Purpose |
|------|---------|
| `complete-uuid-fix.sql` | Fixes BOTH doctors AND appointments tables to VARCHAR(36) |
| `fix-appointments-table.sql` | Fixes ONLY appointments table (if doctors already correct) |
| `start-backend.bat` | Easy double-click backend starter |
| `AppointmentController.java` | Fixed endpoint mapping (already updated) |

---

## 🎯 After Everything Works

Once booking works, you should be able to:
1. ✅ See all 5 doctors in dropdown (readable names)
2. ✅ Book an appointment
3. ✅ See appointment in "My Appointments" page
4. ✅ See appointment status (PENDING)
5. ✅ See "Pay Now" button for payment

---

## 📝 Quick Checklist

- [ ] Run `complete-uuid-fix.sql` in MySQL Workbench
- [ ] Verify doctors show readable UUIDs (not hex)
- [ ] Double-click `start-backend.bat`
- [ ] Wait for "Started MediWayBackendApplication"
- [ ] Test health endpoint (should return 200)
- [ ] Hard refresh frontend (Ctrl+Shift+R)
- [ ] Try booking appointment
- [ ] Check for 201 Created (not 404 or 500)
- [ ] Verify appointment appears in "My Appointments"

---

**Start with Step 1 (SQL script) RIGHT NOW, then proceed to Step 2!** 🚀
