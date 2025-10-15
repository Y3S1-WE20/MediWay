# MediWay Backend Authentication Module

## Overview
This module implements JWT-based authentication for the MediWay Health Management System with role-based access control (RBAC).

## Features
- ✅ JWT token generation and validation
- ✅ User registration with role assignment
- ✅ Secure login with BCrypt password hashing
- ✅ Role-based access control (ADMIN, DOCTOR, PATIENT)
- ✅ Stateless session management
- ✅ Global exception handling
- ✅ CORS configuration for frontend integration

## Technology Stack
- Spring Boot 3.4.10
- Spring Security 6.x
- JWT (JSON Web Tokens) via jjwt 0.11.5
- Spring Data JPA
- MySQL 8.x
- Lombok
- Jakarta Validation

## Architecture

### Entity Layer
- **User.java**: Core user entity with UUID primary key, email-based authentication, and role enum

### DTO Layer
- **LoginRequest**: Email and password for authentication
- **RegisterRequest**: User registration details with role
- **AuthResponse**: JWT token with user details and expiration

### Security Layer
- **JwtUtil**: Token generation, validation, and claims extraction
- **UserDetailsServiceImpl**: Spring Security user loading
- **JwtAuthenticationFilter**: Request filter for JWT validation
- **SecurityConfig**: Security configuration with role-based endpoints

### Service Layer
- **AuthService**: Business logic for registration and login

### Controller Layer
- **AuthController**: REST endpoints for authentication

### Exception Handling
- **GlobalExceptionHandler**: Centralized error handling
- **ResourceNotFoundException**: Custom exception for missing resources
- **ErrorResponse**: Standardized error response DTO

## API Endpoints

### Public Endpoints (No Authentication Required)

#### 1. Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "phone": "+1234567890",
  "role": "PATIENT"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "role": "PATIENT",
  "expiresAt": "2025-10-16T20:00:00"
}
```

#### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "role": "PATIENT",
  "expiresAt": "2025-10-16T20:00:00"
}
```

#### 3. Health Check
```http
GET /api/auth/health
```

**Response (200 OK):**
```
Auth service is running
```

## Role-Based Access Control

### Available Roles
1. **ADMIN**: Full system access
2. **DOCTOR**: Access to doctor and patient endpoints
3. **PATIENT**: Access to patient endpoints only

### Protected Endpoint Patterns
- `/api/admin/**` - ADMIN only
- `/api/doctors/**` - DOCTOR and ADMIN
- `/api/patients/**` - PATIENT, DOCTOR, and ADMIN
- All other `/api/**` - Authenticated users

### Public Endpoints
- `/api/auth/**` - Authentication endpoints
- `/api/public/**` - Public resources
- `/actuator/health` - Health check
- `/h2-console/**` - H2 database console (dev only)
- `/swagger-ui/**` - API documentation

## Using JWT Tokens

### Making Authenticated Requests
Include the JWT token in the Authorization header:

```http
GET /api/patients/123e4567-e89b-12d3-a456-426614174000
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Expiration
- Default expiration: 24 hours (86400000 ms)
- Configured via `jwt.expiration` property
- Client should handle 401 Unauthorized and redirect to login

## Configuration

### application.properties
```properties
# JWT Configuration
jwt.secret=mySecretKey12345678901234567890123456789012345678901234567890
jwt.expiration=86400000

# Security
spring.security.user.name=admin
spring.security.user.password=admin
```

### Environment Variables (Production)
```bash
JWT_SECRET=<your-secure-secret-key>
JWT_EXPIRATION=86400000
MYSQL_USER=mediway_user
MYSQL_PASSWORD=<your-db-password>
SPRING_PROFILES_ACTIVE=mysql
```

## Database Schema

### users Table
```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
);
```

## Error Responses

### Validation Error (400 Bad Request)
```json
{
  "timestamp": "2025-10-15T20:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation error",
  "details": {
    "email": "Email must be valid",
    "password": "Password must be at least 6 characters"
  },
  "path": "/api/auth/register"
}
```

### Authentication Error (401 Unauthorized)
```json
{
  "timestamp": "2025-10-15T20:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "path": "/api/auth/login"
}
```

### User Already Exists (400 Bad Request)
```json
{
  "timestamp": "2025-10-15T20:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "User with email john.doe@example.com already exists",
  "path": "/api/auth/register"
}
```

## Testing with cURL

### Register a new patient
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Jane Smith",
    "email": "jane.smith@example.com",
    "password": "password123",
    "phone": "+1234567890",
    "role": "PATIENT"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane.smith@example.com",
    "password": "password123"
  }'
```

### Access protected endpoint
```bash
# Copy the token from login response
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8080/api/patients/me \
  -H "Authorization: Bearer $TOKEN"
```

## Testing with Postman

1. **Import Collection**: Create a new collection called "MediWay Auth"
2. **Add Environment Variables**:
   - `baseUrl`: `http://localhost:8080`
   - `token`: (will be set automatically)
3. **Register Request**: POST `{{baseUrl}}/api/auth/register`
4. **Login Request**: POST `{{baseUrl}}/api/auth/login`
   - Add test script to save token:
     ```javascript
     pm.environment.set("token", pm.response.json().token);
     ```
5. **Protected Requests**: Add header `Authorization: Bearer {{token}}`

## Security Considerations

### Production Deployment
1. **Change JWT Secret**: Use a strong, randomly generated secret key (min 256 bits)
2. **Use HTTPS**: Always use TLS/SSL in production
3. **Secure Database**: Use strong passwords and restrict network access
4. **Environment Variables**: Never commit secrets to version control
5. **Token Rotation**: Implement refresh token mechanism for long sessions
6. **Rate Limiting**: Add request throttling to prevent brute force
7. **Input Validation**: Already implemented via Jakarta Validation
8. **SQL Injection**: Protected via JPA/Hibernate parameterized queries

### Password Requirements
- Minimum 6 characters (increase to 8-12 in production)
- Consider adding: uppercase, lowercase, numbers, special characters
- Implement password strength meter on frontend

## Development Workflow

### Running Locally
```bash
# Set environment variables
$env:SPRING_PROFILES_ACTIVE='mysql'
$env:MYSQL_USER='mediway_user'
$env:MYSQL_PASSWORD='admin'

# Run the application
cd backend
.\mvnw.cmd spring-boot:run
```

### Building
```bash
# Clean and build
.\mvnw.cmd clean package -DskipTests

# Run tests
.\mvnw.cmd test
```

## Future Enhancements
- [ ] Refresh token mechanism
- [ ] Email verification
- [ ] Password reset functionality
- [ ] OAuth2/Social login integration
- [ ] Two-factor authentication (2FA)
- [ ] User profile management endpoints
- [ ] Audit logging for authentication events
- [ ] Rate limiting and brute force protection

## Troubleshooting

### Common Issues

**Issue**: 401 Unauthorized on protected endpoints
- **Solution**: Ensure token is included in Authorization header with "Bearer " prefix

**Issue**: Token validation fails
- **Solution**: Check JWT secret matches between token generation and validation

**Issue**: Database connection errors
- **Solution**: Verify MySQL is running and credentials are correct

**Issue**: CORS errors from frontend
- **Solution**: Update allowed origins in `CorsConfig.java`

## Support
For issues or questions, contact the development team or create an issue in the repository.
