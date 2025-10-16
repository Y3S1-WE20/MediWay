# Troubleshooting Guide: 500 Error on /api/appointments/doctors

## Error
```
Failed to load resource: the server responded with a status of 500
Error fetching doctors: AxiosError
```

## Root Cause
The backend endpoint `/api/appointments/doctors` is throwing a 500 error, most likely because:
1. **The `doctors` table is empty** (no doctors inserted yet)
2. **The `doctors` table doesn't exist** (backend not connected to MySQL or DDL not created)
3. **Database connection issue**

## Solution

### Step 1: Verify Backend is Running with MySQL
Check the backend console for:
```
HikariPool-1 - Start completed
```
This confirms MySQL connection is active.

### Step 2: Insert Sample Doctors
Run this SQL script in MySQL Workbench:

```powershell
# Open MySQL Workbench
# Connect to mediwaydb
# Open and execute: F:\MediWay\backend\scripts\insert-sample-doctors.sql
```

Or copy-paste this SQL directly into MySQL Workbench:

```sql
-- Insert sample doctors for testing
INSERT INTO doctors (
  doctor_id, name, specialization, email, phone, 
  qualification, experience_years, consultation_fee, 
  available, created_at, updated_at
) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Dr. Sarah Johnson', 'Cardiology', 
 'sarah.johnson@mediway.com', '+1-555-0101', 
 'MBBS, MD (Cardiology)', 15, 150.00, true, NOW(), NOW()),
 
(UNHEX(REPLACE(UUID(), '-', '')), 'Dr. Michael Chen', 'Pediatrics', 
 'michael.chen@mediway.com', '+1-555-0102', 
 'MBBS, DCH (Pediatrics)', 10, 120.00, true, NOW(), NOW()),
 
(UNHEX(REPLACE(UUID(), '-', '')), 'Dr. Emily Rodriguez', 'Dermatology', 
 'emily.rodriguez@mediway.com', '+1-555-0103', 
 'MBBS, MD (Dermatology)', 8, 130.00, true, NOW(), NOW()),
 
(UNHEX(REPLACE(UUID(), '-', '')), 'Dr. James Wilson', 'Orthopedics', 
 'james.wilson@mediway.com', '+1-555-0104', 
 'MBBS, MS (Orthopedics)', 12, 140.00, true, NOW(), NOW()),
 
(UNHEX(REPLACE(UUID(), '-', '')), 'Dr. Priya Patel', 'General Medicine', 
 'priya.patel@mediway.com', '+1-555-0105', 
 'MBBS, MD (General Medicine)', 7, 100.00, true, NOW(), NOW());

-- Verify doctors were inserted
SELECT BIN_TO_UUID(doctor_id) as doctor_id, name, specialization, consultation_fee
FROM doctors ORDER BY created_at DESC;
```

### Step 3: Verify Tables Exist
Run this in MySQL Workbench:

```sql
-- Check if tables exist
SHOW TABLES LIKE 'doctors';
SHOW TABLES LIKE 'appointments';

-- Check doctor count
SELECT COUNT(*) as doctor_count FROM doctors;

-- Check table structure
DESCRIBE doctors;
```

### Step 4: Restart Backend
After inserting doctors:
1. Stop the backend (Ctrl+C in the terminal)
2. Restart:
```powershell
cd F:\MediWay\backend
.\mvnw.cmd spring-boot:run
```

### Step 5: Refresh Frontend
After backend restarts:
1. Refresh the browser (F5)
2. Navigate to Book Appointment page
3. You should now see doctors in the dropdown

## Verify Fix Worked

### In Frontend Console (F12)
You should see:
```
(5) [{doctorId: '...', name: 'Dr. Sarah Johnson', ...}, ...]
```

### Test the Full Flow
1. **Book Appointment**: Select doctor → Choose date/time → Book
2. **View Appointments**: See your booking in the list
3. **Pay**: Click "Pay Now" button
4. **Complete Payment**: Approve in PayPal sandbox
5. **View Receipt**: Success page shows payment receipt

## Still Getting 500 Error?

### Check Backend Logs
Look for stack traces in the backend console. Common issues:
- `NullPointerException` - mapping issue in service/controller
- `SQLException` - database schema mismatch
- `HibernateException` - entity mapping problem

### Check Application Properties
Verify `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mediwaydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=mediway_user
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update
```

### Manual API Test
Test the endpoint directly:
```powershell
curl http://localhost:8080/api/appointments/doctors
```

Expected response:
```json
[
  {
    "doctorId": "...",
    "name": "Dr. Sarah Johnson",
    "specialization": "Cardiology",
    "consultationFee": 150.00,
    ...
  }
]
```

## Quick Commands

### Start Everything
```powershell
# Terminal 1: Backend
cd F:\MediWay\backend
.\mvnw.cmd spring-boot:run

# Terminal 2: Frontend
cd F:\MediWay\frontend
npm run dev
```

### Insert All Test Data
```sql
-- In MySQL Workbench connected to mediwaydb:

-- 1. Insert doctors (run insert-sample-doctors.sql)
-- 2. Insert test appointment (run insert-appointment.sql)  
-- 3. Insert pending payments (run insert-pending-payments.sql)
```

## Success Criteria
✅ Backend starts without errors  
✅ `doctors` table has 5 records  
✅ Frontend loads doctors dropdown  
✅ Can book an appointment  
✅ Can pay for appointment  
✅ Payment completes successfully  
