# MediWay Login Credentials & Authentication Fix

## 🔑 LOGIN CREDENTIALS

### Admin Account
- **Email**: `admin@mediway.com`
- **Password**: `admin123`
- **Role**: ADMIN
- **Dashboard**: `/admin/dashboard`

### Doctor Accounts
- **Email**: `doctor@mediway.com`
- **Password**: `doctor123`
- **Role**: DOCTOR
- **Dashboard**: `/doctor/dashboard`

- **Email**: `doctor1@gmail.com`  
- **Password**: `doctor123`
- **Role**: DOCTOR
- **Dashboard**: `/doctor/dashboard`

### Patient Account (for testing)
- **Email**: `patient@test.com`
- **Password**: `patient123`
- **Role**: PATIENT
- **Dashboard**: `/appointments`

## 🔧 FIXES APPLIED

### 1. Backend Login Response Format Fixed
The backend now returns the correct response format that the frontend expects:
```json
{
  "success": true,
  "token": "simple-token-123",
  "userId": 123,
  "fullName": "Admin User",
  "email": "admin@mediway.com",
  "role": "ADMIN",
  "tokenType": "Bearer"
}
```

### 2. Database Setup Script Created
Run the SQL script `setup_admin_doctor_accounts.sql` to create proper accounts with correct roles.

### 3. Frontend Login Debugging Added
The frontend now logs the role and navigation for debugging purposes.

## 📋 STEPS TO FIX

### Step 1: Update Database
1. Connect to your MySQL database
2. Run the SQL script: `backend/setup_admin_doctor_accounts.sql`
3. This will create admin and doctor accounts with proper roles

### Step 2: Start Backend
1. Navigate to backend directory: `cd F:\MediWay\backend`
2. Start the backend: `.\mvnw.cmd spring-boot:run`
3. Ensure it's running on port 8080

### Step 3: Test Login
1. Go to frontend: `http://localhost:5174/login`
2. Try admin login:
   - Email: `admin@mediway.com`
   - Password: `admin123`
   - Should redirect to `/admin/dashboard`

3. Try doctor login:
   - Email: `doctor@mediway.com`
   - Password: `doctor123`
   - Should redirect to `/doctor/dashboard`

## 🐛 DEBUGGING

### Check Browser Console
1. Open Developer Tools (F12)
2. Check Console tab for login debugging logs:
   - "User role from login: ADMIN"
   - "Redirecting to admin dashboard"

### Verify Database
```sql
SELECT id, name, email, role, created_at 
FROM users 
WHERE role IN ('ADMIN', 'DOCTOR') 
ORDER BY role, id;
```

### Check Backend Response
The login endpoint `/api/auth/login` should return:
- Correct field names (userId, fullName, role)
- Proper role values (ADMIN, DOCTOR, PATIENT)
- Valid token

## ⚠️ IMPORTANT NOTES

1. **Password Security**: Currently using plain text passwords for development. In production, implement proper bcrypt hashing.

2. **Role Validation**: The system now properly validates roles and redirects users to appropriate dashboards.

3. **Admin Functions**: Only admin can create doctor accounts through the admin dashboard.

4. **Doctor Registration**: Removed from public registration - doctors can only be created by admins.

## 🎯 EXPECTED BEHAVIOR

- **Admin Login** → Admin Dashboard with doctor management
- **Doctor Login** → Doctor Dashboard with medical records CRUD
- **Patient Login** → Appointments page with patient features
- **Registration** → Only allows patient registration

All role-based authentication and navigation should now work correctly!