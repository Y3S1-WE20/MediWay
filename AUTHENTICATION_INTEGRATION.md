# MediWay Authentication Integration Guide

## Overview
This document explains the complete authentication integration between the MediWay backend (Spring Boot) and frontend (React).

## Backend Authentication (Spring Boot)

### Technology Stack
- **Spring Boot 3.4.10**
- **Spring Security 6.x**
- **JWT (JSON Web Tokens)** using jjwt 0.11.5
- **MySQL 8.0.42** database
- **BCrypt** password encryption

### Key Components

#### 1. User Entity (`User.java`)
- UUID-based primary key
- Three roles: `ADMIN`, `DOCTOR`, `PATIENT`
- BCrypt password hashing
- Email uniqueness constraint
- Timestamps for created/updated

#### 2. Authentication Endpoints
Base URL: `http://localhost:8080/api`

**POST /auth/register**
```json
Request:
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "1234567890",
  "role": "PATIENT"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": "3dbbcb8d-2e15-40c7-bc7d-a0e9f2e87ad2",
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "PATIENT",
  "expiresAt": "2025-10-16T21:42:00"
}
```

**POST /auth/login**
```json
Request:
{
  "email": "john@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": "3dbbcb8d-2e15-40c7-bc7d-a0e9f2e87ad2",
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "PATIENT",
  "expiresAt": "2025-10-16T21:42:00"
}
```

**GET /auth/health**
- Returns: "Auth service is running"
- Used for health checks

#### 3. Security Configuration
- JWT-based stateless authentication
- Role-based access control (RBAC)
- Public endpoints: `/auth/**`, `/actuator/health`
- Protected endpoints require authentication
- Role-specific endpoints:
  - `/admin/**` - Requires ADMIN role
  - `/doctors/**` - Requires DOCTOR or ADMIN role
  - `/patients/**` - Requires PATIENT, DOCTOR, or ADMIN role

#### 4. JWT Token Details
- **Algorithm**: HS256
- **Expiration**: 24 hours (86400000 ms)
- **Claims**: username (email), role
- **Header format**: `Authorization: Bearer <token>`

## Frontend Authentication (React)

### Technology Stack
- **React 18**
- **React Router v6**
- **Axios** for HTTP requests
- **Framer Motion** for animations
- **Tailwind CSS** for styling

### Key Components

#### 1. AuthContext (`context/AuthContext.jsx`)
Provides global authentication state:
```javascript
{
  user,           // User object with id, name, email, role
  token,          // JWT token
  login,          // Login function
  logout,         // Logout function
  updateUser,     // Update user data
  isAuthenticated,// Boolean
  isAdmin,        // Function to check if user is ADMIN
  isDoctor,       // Function to check if user is DOCTOR
  isPatient,      // Function to check if user is PATIENT
  loading         // Loading state
}
```

#### 2. API Configuration (`api/api.js`)
- Base URL: `http://localhost:8080/api`
- Axios interceptors:
  - **Request**: Automatically adds `Authorization: Bearer <token>` header
  - **Response**: Handles 401 errors and redirects to login

#### 3. Login Page (`pages/Login.jsx`)
Features:
- **Dual mode**: Patient/Doctor login and Admin login
- Toggle buttons to switch between modes
- Real backend API integration
- Form validation
- Error handling
- Auto-redirect based on role:
  - ADMIN → `/admin/dashboard`
  - DOCTOR → `/doctor/dashboard`
  - PATIENT → `/appointments`

#### 4. Register Page (`pages/Register.jsx`)
Features:
- Role selection (PATIENT or DOCTOR)
- Real backend API integration
- Form validation
- Success screen with user details
- Redirect to login after registration

#### 5. Protected Routes
All authenticated routes are wrapped with `<ProtectedRoute>`:
- Checks for valid token
- Redirects to login if not authenticated
- Maintains redirect URL for post-login navigation

## Running the Application

### Backend Setup

1. **Start MySQL Server**
   ```bash
   # Make sure MySQL is running on port 3306
   # Database: mediwaydb
   # User: mediway_user
   # Password: admin
   ```

2. **Start Backend**
   ```bash
   cd F:\MediWay\backend
   $env:SPRING_PROFILES_ACTIVE='mysql'
   $env:MYSQL_PASSWORD='admin'
   .\mvnw.cmd spring-boot:run
   ```

   Backend will start on: `http://localhost:8080`

### Frontend Setup

1. **Install Dependencies**
   ```bash
   cd F:\MediWay\frontend
   npm install
   ```

2. **Start Frontend**
   ```bash
   npm run dev
   ```

   Frontend will start on: `http://localhost:5173`

## Testing the Integration

### 1. Register a New User
- Navigate to `http://localhost:5173/register`
- Fill in the form:
  - Full Name: John Doe
  - Email: john@example.com
  - Phone: 1234567890
  - Role: PATIENT
  - Password: password123
- Submit and verify success message

### 2. Login as Patient
- Navigate to `http://localhost:5173/login`
- Ensure "Patient/Doctor Login" mode is selected
- Enter credentials:
  - Email: john@example.com
  - Password: password123
- Should redirect to `/appointments`

### 3. Register and Login as Doctor
- Register with role: DOCTOR
- Login should redirect to `/doctor/dashboard`

### 4. Admin Login
- Create an admin user in database manually or via SQL:
  ```sql
  INSERT INTO users (user_id, full_name, email, password_hash, phone, role, is_active, created_at, updated_at)
  VALUES (UUID(), 'Admin User', 'admin@mediway.com', '$2a$10$...', '0000000000', 'ADMIN', true, NOW(), NOW());
  ```
- Use "Admin Login" button
- Should redirect to `/admin/dashboard`

## Role-Based Features

### ADMIN
- Access to admin dashboard
- Manage all users
- View system statistics
- Full system access

### DOCTOR
- Access to doctor dashboard
- View assigned patients
- Manage appointments
- Create medical reports

### PATIENT
- View/book appointments
- View medical reports
- Make payments
- Update profile

## Security Features

### Frontend
- Token stored in localStorage
- Automatic token injection in API requests
- Auto-logout on 401 responses
- Protected routes with authentication checks

### Backend
- BCrypt password hashing (strength: 10)
- JWT token validation on every request
- Role-based endpoint protection
- CORS enabled for frontend origin
- Stateless session management

## Error Handling

### Common Errors

**401 Unauthorized**
- Invalid or expired token
- Frontend automatically redirects to login

**403 Forbidden**
- User doesn't have required role
- Check endpoint permissions

**400 Bad Request**
- Invalid request data
- Check request payload format

**500 Internal Server Error**
- Backend issue
- Check backend logs

## Development Tips

1. **Backend Logs**: Check console for authentication events
2. **Frontend DevTools**: Inspect network requests and localStorage
3. **Database**: Use MySQL Workbench to view users table
4. **JWT Decoder**: Use jwt.io to decode and inspect tokens

## File Structure

```
MediWay/
├── backend/
│   ├── src/main/java/com/mediway/backend/
│   │   ├── entity/User.java
│   │   ├── dto/
│   │   │   ├── request/LoginRequest.java
│   │   │   ├── request/RegisterRequest.java
│   │   │   └── response/AuthResponse.java
│   │   ├── security/
│   │   │   ├── JwtUtil.java
│   │   │   └── UserDetailsServiceImpl.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── CorsConfig.java
│   │   ├── repository/UserRepository.java
│   │   ├── service/AuthService.java
│   │   └── controller/AuthController.java
│   └── src/main/resources/
│       ├── application.properties
│       └── application-mysql.properties
│
└── frontend/
    ├── src/
    │   ├── api/
    │   │   ├── api.js
    │   │   └── endpoints.js
    │   ├── context/
    │   │   └── AuthContext.jsx
    │   ├── pages/
    │   │   ├── Login.jsx
    │   │   ├── Register.jsx
    │   │   ├── AdminDashboard.jsx
    │   │   └── DoctorDashboard.jsx
    │   ├── components/
    │   │   ├── ProtectedRoute.jsx
    │   │   └── Navbar.jsx
    │   └── App.jsx
    └── package.json
```

## Next Steps

1. ✅ Backend authentication implemented
2. ✅ Frontend authentication integrated
3. ✅ Role-based routing
4. 🔄 Implement refresh tokens
5. 🔄 Add forgot password feature
6. 🔄 Implement email verification
7. 🔄 Add two-factor authentication
8. 🔄 Create admin user management panel

## Support

For issues or questions:
- Check backend logs: `F:\MediWay\backend\logs`
- Check browser console for frontend errors
- Verify MySQL connection and credentials
- Ensure both servers are running on correct ports
