# Quick Start Guide - Doctor Dashboard Testing

## Prerequisites
- Backend server running on port 8080
- Frontend server running on port 5173 (or your configured port)
- MySQL database with patient data

## Step 1: Start the Backend Server

Open a PowerShell terminal and run:
```powershell
cd F:\MediWay\backend
.\mvnw.cmd spring-boot:run
```

Wait for the message: `Started MediWayBackendApplication in X seconds`

## Step 2: Verify Backend Endpoints

Open another PowerShell terminal and test the endpoints:

```powershell
# Test single patient endpoint
curl http://localhost:8080/api/patients/1

# Test batch patient endpoint
curl "http://localhost:8080/api/patients/batch?ids=1,2,3"
```

Both should return `StatusCode: 200` with patient data.

## Step 3: Start the Frontend Server

Open a PowerShell terminal and run:
```powershell
cd F:\MediWay\frontend
npm run dev
```

## Step 4: Test the Doctor Dashboard

1. Open your browser and navigate to: `http://localhost:5173` (or your configured port)

2. Log in as a doctor (use your doctor credentials)

3. Navigate to the Doctor Dashboard

4. **Verify Appointments Section:**
   - Check that patient names are displayed (not "Patient 1")
   - Verify patient IDs are shown
   - Check that dates, times, and statuses are correct
   - Look for patient photos (if available in database)

5. **Verify Medical Records Section:**
   - Check that patient names appear in the table (not "Patient 1")
   - Try searching for a patient name
   - Test the filter dropdown
   - Verify all patient data is correct

## Expected Results

### ✅ Appointments Section Should Show:
```
Patient Name: Tester1
ID: 1
Date: 2025-10-23
Time: 13:21:00
Status: COMPLETED (with green badge)
```

### ✅ Medical Records Section Should Show:
```
Patient Column: 
  - Patient photo or initial circle
  - Patient name (e.g., "Tester1")
  - ID: 1
```

## Troubleshooting

### Issue: Still seeing "Patient 1"
**Solution:**
1. Check browser console (F12) for errors
2. Verify backend is returning data:
   ```powershell
   curl http://localhost:8080/api/patients/1
   ```
3. Clear browser cache and reload (Ctrl+Shift+R)
4. Check Network tab in DevTools to see API responses

### Issue: Backend returns 500 errors
**Solution:**
1. Check that MySQL is running
2. Verify database credentials in `application.properties`
3. Check backend console for error messages
4. Ensure `data.sql` executed successfully

### Issue: CORS errors in browser console
**Solution:**
1. Verify that the backend has CORS configured
2. Check that frontend is making requests to the correct backend URL
3. Restart both frontend and backend servers

## Database Check

To verify patient data exists in the database:

```sql
-- Connect to MySQL
mysql -u root -p

-- Use the database
USE mediwaydb;

-- Check patient data
SELECT id, name, email, phone, role FROM users WHERE role = 'PATIENT';
```

Expected output:
```
+----+-------------------+--------------------------+--------------+---------+
| id | name              | email                    | phone        | role    |
+----+-------------------+--------------------------+--------------+---------+
|  1 | Tester1           | tester1@gmail.com        | 0771052042   | PATIENT |
|  2 | John Smith        | john.smith@example.com   | 0771234567   | PATIENT |
|  3 | Emily Johnson     | emily.johnson@...        | 0772345678   | PATIENT |
|  4 | Shiransha Fernando| shiransha@example.com    | 0774567890   | PATIENT |
+----+-------------------+--------------------------+--------------+---------+
```

## Success Criteria

✅ **All tests pass when:**
1. Backend endpoints return patient data (Status 200)
2. Doctor dashboard displays real patient names
3. Patient IDs are shown correctly
4. Appointments show date, time, and status
5. Medical records show patient info in table
6. No 500 errors in browser console
7. No "Patient 1" fallback text appears

## Next Steps After Testing

1. Test with real appointment data
2. Verify medical record creation with patient selection
3. Test search and filter functionality
4. Verify export report functionality
5. Test responsive design on mobile devices

---

**Status:** Ready for testing
**Last Updated:** October 24, 2025
