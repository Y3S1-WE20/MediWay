# MediWay Hospital Management System

A comprehensive full-stack hospital management system built with Spring Boot and React, featuring appointment scheduling, patient records, payment processing, and administrative reporting with comprehensive unit testing coverage.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technical Architecture](#technical-architecture)
- [Unit Testing](#unit-testing)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [SOLID Principles](#solid-principles)
- [Team Information](#team-information)

---

## 🎯 Project Overview

MediWay is a modern healthcare management platform that digitizes hospital operations, providing seamless integration between patients, doctors, and administrators. The system ensures secure, efficient healthcare service delivery with real-time appointment management, comprehensive medical records, and integrated payment processing.

**Key Capabilities:**
- **Appointment Management**: Intelligent scheduling with concurrency control
- **Patient Records**: Complete medical history with secure access controls
- **Payment Integration**: PayPal sandbox integration with receipt generation
- **Administrative Dashboard**: Real-time analytics and reporting
- **Role-Based Security**: Multi-level access control for different user types

---

## ✨ Features

### 🏥 Appointment Scheduling System
- **Doctor Availability Management**: Real-time schedule tracking and updates
- **Intelligent Booking**: Date/time validation with conflict prevention
- **Concurrency Control**: Prevents double-booking through optimistic locking
- **Status Tracking**: SCHEDULED → COMPLETED → CANCELLED workflow
- **Real-time Updates**: Instant status synchronization across the platform

### 👥 Patient Management
- **Comprehensive Medical Records**: CRUD operations for diagnoses and treatments
- **Patient Profiles**: Emergency contacts, medical history, and personal information
- **QR Code Integration**: Digital patient identification and quick access
- **Secure Access**: Role-based permissions for patient data

### 💳 Payment Processing
- **PayPal Integration**: Sandbox environment for secure transactions
- **Receipt Generation**: PDF receipts with QR codes and transaction details
- **Payment Tracking**: PENDING → COMPLETED → FAILED status management
- **Appointment Linking**: Automatic status updates upon payment completion

### 📊 Administrative Dashboard
- **Statistical Reports**: Monthly revenue, appointment analytics, and performance metrics
- **Export Capabilities**: PDF and CSV report generation
- **User Management**: CRUD operations for doctors and patients
- **System Monitoring**: Real-time dashboard with key performance indicators

---

## 🏗 Technical Architecture

### Backend Architecture

#### **Framework & Runtime**
- **Spring Boot 3.4.10**: Enterprise-grade Java framework
- **Java 17**: Modern JVM with enhanced performance and security
- **Maven**: Dependency management and build automation

#### **Data Layer**
- **MySQL 8.0**: Relational database with ACID compliance
- **Spring Data JPA**: Object-relational mapping with Hibernate
- **Database Schema**: Normalized design with foreign key relationships

#### **Security & Authentication**
- **Spring Security**: Comprehensive security framework
- **JWT Tokens**: Stateless authentication with role-based access control
- **BCrypt**: Password hashing for secure credential storage

#### **Payment Integration**
- **PayPal REST SDK**: Official PayPal API integration
- **Dual Mode Support**: Production and sandbox environments
- **Webhook Handling**: Real-time payment status updates

#### **Additional Libraries**
- **ZXing**: QR code generation and scanning
- **iText 7**: PDF document generation
- **Jackson**: JSON processing and serialization
- **Mockito**: Testing framework for unit and integration tests

### Frontend Architecture

#### **Framework & Build Tools**
- **React 18**: Modern component-based UI framework
- **Vite**: Fast build tool with hot module replacement
- **Material-UI**: Consistent design system components

#### **State Management**
- **React Context**: Global state management for user sessions
- **Axios**: HTTP client for API communication
- **React Router**: Client-side routing and navigation

### DevOps & Deployment

#### **Containerization**
- **Docker**: Multi-stage builds for optimized images
- **Docker Compose**: Orchestration for multi-service deployments

#### **Development Tools**
- **Ngrok**: Secure tunneling for webhook development
- **GitHub Actions**: CI/CD pipeline automation
- **JaCoCo**: Code coverage analysis and reporting

### Database Schema

```sql
-- Core Entities
Users (id, email, password, role, profile_data)
Doctors (id, user_id, specialization, availability)
Patients (id, user_id, health_id, medical_history)
Appointments (id, patient_id, doctor_id, date_time, status)
Medical_Records (id, patient_id, doctor_id, diagnosis, treatment)
Payments (id, appointment_id, amount, status, paypal_payment_id)
Receipts (id, payment_id, receipt_number, pdf_content)
```

---

## 🧪 Unit Testing

### Test Coverage Overview

| Component | Coverage | Test Count | Status |
|-----------|----------|------------|--------|
| **Overall** | **82%** | **472+** | ✅ |
| Controllers | 85% | 230+ | ✅ |
| Services | 88% | 120+ | ✅ |
| Entities | 81% | 65+ | ✅ |
| Repositories | 95% | 35+ | ✅ |
| Security | 90% | 25+ | ✅ |

### Testing Framework

#### **JUnit 5**
- **@Test**: Unit test annotations
- **@DisplayName**: Descriptive test names
- **@BeforeEach/@AfterEach**: Test lifecycle management
- **Assertions**: Comprehensive assertion library

#### **Mockito**
- **@Mock**: Dependency mocking
- **@InjectMocks**: Service injection for testing
- **when().thenReturn()**: Method stubbing
- **verify()**: Interaction verification

#### **Spring Boot Test**
- **@SpringBootTest**: Integration testing
- **@WebMvcTest**: Controller testing
- **@DataJpaTest**: Repository testing
- **MockMvc**: HTTP endpoint testing

### Test Categories

#### **Positive Tests** (60%)
- Valid input scenarios
- Successful operations
- Expected behavior validation

#### **Negative Tests** (25%)
- Invalid input handling
- Error condition management
- Exception scenarios

#### **Edge Cases** (15%)
- Boundary conditions
- Null/empty values
- Concurrency scenarios

### Running Tests

#### **All Tests**
```bash
cd backend
./mvnw clean test
```

#### **Feature-Specific Tests**

**Appointment System:**
```bash
./mvnw test -Dtest="AppointmentServiceTest,SimpleAppointmentControllerTest"
# 15 service tests + 26 controller tests = 41 tests
```

**Admin & Reports:**
```bash
./mvnw test -Dtest="AdminServiceTest,AdminControllerTest,SimpleReportsControllerTest,ReportsServiceTest"
# 21 + 10 + 35 + 20 = 86 tests
```

**Medical Records:**
```bash
./mvnw test -Dtest="MedicalRecordServiceTest,MedicalRecordControllerTest,SimpleMedicalRecordControllerTest,SimpleProfileControllerTest,QRCodeServiceTest"
# 20 + 28 + 15 + 30 + 6 = 99 tests
```

**Payment System:**
```bash
./mvnw test -Dtest="SimplePayPalControllerTest,ReceiptControllerTest"
# 29 + 30 = 59 tests
```

#### **Coverage Report Generation**
```bash
./mvnw clean test jacoco:report
# View: backend/target/site/jacoco/index.html
```

### Test Structure Example

```java
@SpringBootTest
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    @DisplayName("Positive: Successfully create appointment with available doctor")
    void testCreateAppointment_Success() {
        // Given
        when(appointmentRepository.save(any())).thenReturn(testAppointment);

        // When
        Appointment result = appointmentService.createAppointment(request);

        // Then
        assertNotNull(result);
        assertEquals(Appointment.Status.SCHEDULED, result.getStatus());
        verify(appointmentRepository).save(any());
    }
}
```

### Key Test Scenarios

#### **Concurrency Control**
```java
@Test
@DisplayName("Edge: Prevent double booking with concurrent requests")
void testCreateAppointment_ConcurrencyControl() {
    // Simulates race condition scenario
    // Ensures only one appointment is created
}
```

#### **Payment Integration**
```java
@Test
@DisplayName("Positive: Create PayPal payment successfully")
void testCreatePayment_Success() {
    // Tests PayPal SDK integration
    // Verifies payment record creation
    // Validates approval URL generation
}
```

#### **Security Testing**
```java
@Test
@DisplayName("Negative: Access denied for unauthorized user")
void testAccessControl_UnauthorizedAccess() {
    // Tests role-based access control
    // Verifies proper exception handling
}
```

---

## 📦 Prerequisites

- **Java Development Kit**: JDK 17 or higher
- **Node.js**: Version 18+ with npm
- **MySQL**: Version 8.0 or higher
- **Maven**: Version 3.8+ (included wrapper available)
- **Git**: Version control system
- **Ngrok**: For PayPal webhook tunneling
- **Docker**: Optional, for containerized deployment

---

## 🚀 Installation & Setup

### Backend Configuration

1. **Database Setup**
   ```sql
   CREATE DATABASE mediwaydb;
   CREATE USER 'mediway_user'@'localhost' IDENTIFIED BY 'mediway_password';
   GRANT ALL PRIVILEGES ON mediwaydb.* TO 'mediway_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Application Properties**
   ```properties
   # Database
   spring.datasource.url=jdbc:mysql://localhost:3306/mediwaydb
   spring.datasource.username=mediway_user
   spring.datasource.password=mediway_password

   # JPA
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   # Server
   server.port=8080

   # PayPal (Sandbox)
   paypal.mode=sandbox
   paypal.client.id=YOUR_SANDBOX_CLIENT_ID
   paypal.client.secret=YOUR_SANDBOX_CLIENT_SECRET
   paypal.use-simulated-checkout=true
   ```

3. **Database Initialization**
   ```bash
   cd backend
   mysql -u mediway_user -p mediwaydb < scripts/complete-setup.sql
   mysql -u mediway_user -p mediwaydb < scripts/setup_admin_doctor_accounts.sql
   ```

### Frontend Configuration

1. **Environment Setup**
   ```bash
   cd frontend
   npm install
   ```

2. **Environment Variables**
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   VITE_PAYPAL_CLIENT_ID=YOUR_SANDBOX_CLIENT_ID
   ```

### Ngrok Configuration

```bash
# Install ngrok and start tunnel
ngrok http 5173

# Update PayPal return URLs in application.properties
paypal.return.url=https://your-ngrok-id.ngrok.io/paypal-success
paypal.cancel.url=https://your-ngrok-id.ngrok.io/paypal-cancel
```

---

## 🏃 Running the Application

### Development Mode

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
# Access: http://localhost:8080
```

**Frontend:**
```bash
cd frontend
npm run dev
# Access: http://localhost:5173
```

**Ngrok (for PayPal):**
```bash
ngrok http 5173
# Public URL: https://your-ngrok-id.ngrok.io
```

### Production Build

**Backend:**
```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/mediway-1.0.0.jar
```

**Frontend:**
```bash
cd frontend
npm run build
# Serve static files from dist/
```

### Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up --build
```

---

## 📚 API Documentation

### Authentication Endpoints
```http
POST /api/auth/register          # User registration
POST /api/auth/login            # User authentication
POST /api/auth/logout           # Session termination
```

### Appointment Management
```http
GET    /api/appointments               # List appointments
POST   /api/appointments               # Create appointment
GET    /api/appointments/{id}          # Get appointment details
PUT    /api/appointments/{id}          # Update appointment
DELETE /api/appointments/{id}          # Cancel appointment
GET    /api/appointments/doctor/{id}   # Doctor's appointments
```

### Medical Records
```http
GET    /api/medical-records/patient/{id}  # Patient records
POST   /api/medical-records               # Create record
PUT    /api/medical-records/{id}          # Update record
DELETE /api/medical-records/{id}          # Delete record
```

### Payment Processing
```http
POST   /paypal/create                    # Initiate payment
POST   /paypal/execute                   # Complete payment
POST   /paypal/execute-token             # Token-based execution
GET    /paypal/my-payments               # User payments
GET    /paypal/receipts/my-receipts      # User receipts
```

### Administrative Operations
```http
GET    /admin/users                      # User management
GET    /admin/doctors                    # Doctor management
GET    /admin/appointments               # Appointment oversight
GET    /api/reports/dashboard            # System analytics
GET    /api/reports/monthly-revenue/pdf # Revenue reports
```

### Response Format
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed",
  "timestamp": "2025-10-25T10:00:00Z"
}
```

---

## 🏗 SOLID Principles Implementation

### 1. Single Responsibility Principle (SRP)
Each class has one primary responsibility and reason to change.

**Examples:**
- `AppointmentService`: Handles only appointment business logic
- `QRCodeService`: Manages only QR code generation
- `ReportsService`: Focuses solely on report generation

### 2. Open/Closed Principle (OCP)
Software entities should be open for extension but closed for modification.

**Examples:**
- `AppointmentStatus` enum extensible without code changes
- Payment method interfaces allow new providers
- User roles can be extended via configuration

### 3. Liskov Substitution Principle (LSP)
Subtypes must be substitutable for their base types.

**Examples:**
- All repository implementations work with `JpaRepository` interface
- Service implementations interchangeable through dependency injection
- Mock repositories in tests behave identically to real ones

### 4. Interface Segregation Principle (ISP)
Clients should not be forced to depend on interfaces they don't use.

**Examples:**
- Separate `CrudOperations` and `ReportGenerator` interfaces
- Focused repository interfaces per entity
- Service-specific interfaces instead of monolithic contracts

### 5. Dependency Inversion Principle (DIP)
High-level modules should not depend on low-level modules.

**Examples:**
- Controllers depend on service interfaces, not implementations
- Services depend on repository abstractions
- Dependency injection enables loose coupling

---

## 👥 Team Information

**Course**: Web Engineering (WE20)  
**Year**: Year 3, Semester 1  
**Institution**: University of Colombo School of Computing  
**Repository**: https://github.com/Y3S1-WE20/MediWay

### Team Members & Responsibilities

**Member 1 - Appointment Scheduling**
- Appointment booking system with concurrency control
- Real-time availability management
- Status tracking and validation

**Member 2 - Admin & Reporting**
- User and doctor management
- Statistical dashboard and analytics
- PDF/CSV report generation

**Member 3 - Medical Records**
- Patient profile management
- Medical history tracking
- QR code integration

**Member 4 - Payment Processing**
- PayPal integration and sandbox testing
- Receipt generation and management
- Transaction status tracking

---

## 📊 System Metrics

- **Total Test Cases**: 472+
- **Code Coverage**: 82% branch coverage
- **API Endpoints**: 35+ REST endpoints
- **Database Tables**: 12 core entities
- **User Roles**: Admin, Doctor, Patient
- **Payment Methods**: PayPal (Sandbox + Production)

---

## 🔧 Troubleshooting

### Common Issues

**Database Connection:**
```bash
# Verify MySQL service
mysql --version
mysql -u root -p -e "SHOW DATABASES;"
```

**Port Conflicts:**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

**Build Issues:**
```bash
# Clear Maven cache
./mvnw dependency:purge-local-repository
./mvnw clean install -U
```

---

**Last Updated**: October 25, 2025  
**Version**: 1.0.0  
**Status**: Production Ready
