# 🚀 QUICK FIX - 3 Simple Steps

## The Problem
- UUID format mismatch causing "Doctor not found" errors
- Database has hex strings instead of proper UUIDs
- Safe update mode blocking fixes

## The Solution (Choose ONE method)

---

## ⚡ METHOD 1: Automated PowerShell Script (EASIEST)

**Open PowerShell in F:\MediWay and run:**
```powershell
.\emergency-fix.ps1
```

That's it! The script will:
- Fix the database automatically
- Rebuild the backend
- Show you next steps

---

## 📝 METHOD 2: MySQL Workbench (MANUAL)

### Step 1: Open MySQL Workbench
1. Open MySQL Workbench
2. Connect to your database
3. File → Open SQL Script
4. Select: `F:\MediWay\backend\scripts\fix-everything.sql`
5. Click Execute (⚡ lightning icon)
6. Wait for "SUCCESS!" message

### Step 2: Rebuild Backend
```powershell
cd F:\MediWay\backend
.\mvnw.cmd clean install
```

### Step 3: Start Everything
```powershell
# Terminal 1 - Backend
cd F:\MediWay\backend
.\mvnw.cmd spring-boot:run

# Terminal 2 - Frontend
cd F:\MediWay\frontend
npm run dev
```

---

## ✅ How to Verify It Worked

After running the fix, check these 3 things:

### 1. Check Database (MySQL Workbench)
```sql
SELECT doctor_id, name FROM mediwaydb.doctors;
```
**Expected:** 3 doctors with UUIDs like `51492852-aa4a-11f0-...`

### 2. Check Backend Logs
Should see:
```
Started MediWayBackendApplication in X.XXX seconds
```

### 3. Check Frontend
- Go to http://localhost:5173
- Click "Book Appointment"
- **Should see 3 doctors listed**

---

## 🎯 What Got Fixed

| Issue | Solution |
|-------|----------|
| Doctor UUIDs in wrong format | Recreated doctors table with proper UUIDs |
| "Doctor not found" errors | All doctor_ids now in correct format |
| Safe update mode errors | Script disables it temporarily |
| Missing QR code column | Added to users table |
| Missing medical tables | Created medical_records, prescriptions, lab_results |
| 404 appointment errors | Backend context path verified |

---

## 🆘 Still Having Issues?

### Issue: "MySQL not found"
**Solution:** Run the SQL script manually in MySQL Workbench (Method 2 above)

### Issue: "Backend won't start"
**Check:**
```powershell
cd F:\MediWay\backend
cat src\main\resources\application.properties | Select-String "datasource"
```
Verify: username=mediway_user, password=admin, database=mediwaydb

### Issue: "Frontend 404 errors"
**Verify backend is running:**
- Open http://localhost:8080/api/appointments/doctors
- Should show JSON with doctors list

### Issue: "No QR code showing"
**Solution:**
1. Register a NEW patient account (old accounts don't have QR)
2. OR run in MySQL: `UPDATE mediwaydb.users SET qr_code = NULL;` (forces regeneration)

---

## 📞 Quick Reference

**Database:** mediwaydb  
**DB User:** mediway_user  
**DB Password:** admin  
**Backend:** http://localhost:8080/api  
**Frontend:** http://localhost:5173  

**Test Doctors Created:**
- Dr. Sarah Johnson (Cardiology) - $150
- Dr. Michael Chen (Pediatrics) - $120  
- Dr. Emily Rodriguez (Dermatology) - $130

---

**Run the fix now:** `.\emergency-fix.ps1` or open `fix-everything.sql` in MySQL Workbench! 🚀
