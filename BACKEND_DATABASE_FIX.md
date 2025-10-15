# Backend-Database Connection Fix

## Problem
Login is not working because the backend is using **H2 in-memory database** instead of **MySQL**. This means:
- User data is stored in memory only
- Data is lost when backend restarts
- Users you see in MySQL Workbench are NOT being used by the backend

## Root Cause
The `application.properties` file was configured to use H2 by default:
```properties
# OLD (Problem)
spring.datasource.url=jdbc:h2:mem:mediwaydb
spring.jpa.hibernate.ddl-auto=create-drop
```

## Solution Applied

### 1. Updated `application.properties`
Changed the default database from H2 to MySQL:

```properties
# NEW (Fixed)
spring.datasource.url=jdbc:mysql://localhost:3306/mediwaydb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=mediway_user
spring.datasource.password=admin
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
```

**Key Changes:**
- ✅ Database: H2 → MySQL
- ✅ DDL mode: `create-drop` → `update` (preserves data on restart)
- ✅ Dialect: H2 → MySQL8
- ✅ Username/Password: sa/password → mediway_user/admin

### 2. Created Helper Scripts

**`start-with-mysql-fixed.ps1`** - Starts backend with MySQL
- Checks MySQL is running
- Verifies database exists
- Stops any existing backend on port 8080
- Starts backend with correct configuration

**`test-mysql-connection.ps1`** - Tests the connection
- Registers a test user
- Verifies user appears in MySQL
- Tests login
- Verifies persistence after restart

## How to Fix and Test

### Step 1: Stop Backend (if running)
Press `Ctrl+C` in the backend terminal window

### Step 2: Start Backend with MySQL
```powershell
cd F:\MediWay\backend
.\start-with-mysql-fixed.ps1
```

**Watch for these log messages:**
```
✓ HikariPool-1 - Starting...
✓ HikariPool-1 - Start completed
✓ Started MediWayBackendApplication in X seconds
```

If you see these messages, MySQL connection is successful!

### Step 3: Test the Connection
In a **new PowerShell window**:
```powershell
cd F:\MediWay\backend
.\test-mysql-connection.ps1
```

This will:
1. Register a test user
2. Verify the user appears in MySQL Workbench
3. Test login
4. Give you SQL queries to check data

### Step 4: Test Frontend Login

1. Start frontend (if not running):
```powershell
cd F:\MediWay\frontend
npm run dev
```

2. Open http://localhost:5174

3. Try logging in with one of these users from MySQL Workbench:
   - Email: `test.patient@mediway.com`
   - Password: (the one you registered with)

4. OR register a NEW user and it should work immediately

## Verification Checklist

### Backend Logs
- [ ] See "HikariPool-1 - Start completed" (MySQL connected)
- [ ] See "Started MediWayBackendApplication" (Backend ready)
- [ ] NO errors about database connection
- [ ] NO mentions of "H2" in logs

### MySQL Workbench
- [ ] Can see `mediwaydb` database
- [ ] Can see `users` table
- [ ] Users table has the test user you registered
- [ ] Password is encrypted (bcrypt hash)

### Frontend Login
- [ ] Can register a new user
- [ ] Can login with registered user
- [ ] Token is stored in localStorage
- [ ] Redirected to appropriate dashboard

### Persistence Test
- [ ] Register a user
- [ ] Stop backend (Ctrl+C)
- [ ] Restart backend with `.\start-with-mysql-fixed.ps1`
- [ ] Can still login with same user (proves MySQL persistence)

## Common Issues and Solutions

### Issue 1: "Communications link failure"
**Symptom**: Backend logs show `CommunicationsException: Communications link failure`

**Solution**:
1. Verify MySQL is running (port 3306)
2. Check MySQL Workbench can connect
3. Verify database `mediwaydb` exists
4. Check username/password is correct

### Issue 2: "Access denied for user 'mediway_user'"
**Symptom**: `Access denied for user 'mediway_user'@'localhost'`

**Solution**: Grant permissions in MySQL:
```sql
CREATE USER IF NOT EXISTS 'mediway_user'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON mediwaydb.* TO 'mediway_user'@'localhost';
FLUSH PRIVILEGES;
```

### Issue 3: "Unknown database 'mediwaydb'"
**Symptom**: `Unknown database 'mediwaydb'`

**Solution**: Create database in MySQL:
```sql
CREATE DATABASE IF NOT EXISTS mediwaydb;
```

### Issue 4: Still seeing H2 logs
**Symptom**: Backend logs show "H2" or "jdbc:h2:mem"

**Solution**:
1. Make sure you're using the updated `application.properties`
2. Clean and rebuild:
```powershell
.\mvnw.cmd clean
.\mvnw.cmd spring-boot:run
```

### Issue 5: Login works but data not in MySQL
**Symptom**: Can login but don't see users in MySQL Workbench

**Solution**: Backend is still using H2. Check:
1. `application.properties` has MySQL config
2. Backend logs show "HikariPool-1" (not H2)
3. Restart backend with fresh terminal

## SQL Queries to Verify Data

### Check if users table exists
```sql
SHOW TABLES FROM mediwaydb;
```

### View all users
```sql
SELECT 
    user_id,
    email,
    full_name,
    role,
    is_active,
    created_at
FROM mediwaydb.users
ORDER BY created_at DESC;
```

### Count users
```sql
SELECT COUNT(*) as total_users FROM mediwaydb.users;
```

### Check specific user by email
```sql
SELECT * FROM mediwaydb.users WHERE email = 'test.patient@mediway.com';
```

### View all tables and row counts
```sql
SELECT 
    TABLE_NAME,
    TABLE_ROWS
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'mediwaydb';
```

## Expected Database Structure

After backend starts, MySQL should have these tables:

| Table Name | Purpose |
|------------|---------|
| `users` | User accounts (patients, doctors, admins) |
| `roles` | User roles (PATIENT, DOCTOR, ADMIN) |
| `payments` | PayPal payment records |
| `receipts` | Payment receipts |
| `appointments` | (if implemented) |

## Test Data

Use these credentials to test (after registering them):

**Patient Account:**
- Email: `patient@mediway.com`
- Password: `Patient@123`
- Role: PATIENT

**Doctor Account:**
- Email: `doctor@mediway.com`
- Password: `Doctor@123`
- Role: DOCTOR

**Admin Account:**
- Email: `admin@mediway.com`
- Password: `Admin@123`
- Role: ADMIN

## Success Indicators

✅ **Backend Connected to MySQL**:
- Logs show "HikariPool-1 - Start completed"
- No H2 messages in logs
- MySQL Workbench shows `users` table

✅ **Login Working**:
- Can register new users
- Users appear in MySQL immediately
- Can login with registered credentials
- Token stored in localStorage

✅ **Data Persisting**:
- Backend restart doesn't lose users
- Multiple registrations accumulate in database
- Password hashes visible in MySQL

## Troubleshooting Commands

### Check if backend is running
```powershell
Test-NetConnection -ComputerName localhost -Port 8080
```

### Check if MySQL is running
```powershell
Test-NetConnection -ComputerName localhost -Port 3306
```

### View backend logs
```powershell
cd F:\MediWay\backend
Get-Content target\spring-boot.log -Tail 50 -Wait
```

### Clean build
```powershell
cd F:\MediWay\backend
.\mvnw.cmd clean compile
```

### Force restart
```powershell
# Kill all Java processes
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force

# Restart backend
.\start-with-mysql-fixed.ps1
```

## Summary

The backend is now configured to use MySQL by default. Simply restart the backend with:

```powershell
cd F:\MediWay\backend
.\start-with-mysql-fixed.ps1
```

Then test login in the frontend at http://localhost:5174

All user data will now persist to MySQL and survive backend restarts!

---

**Last Updated**: January 15, 2025
**Status**: ✅ FIXED - Backend now uses MySQL by default
