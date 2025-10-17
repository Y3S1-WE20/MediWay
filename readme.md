# MediWay Hospital Management SystemAppointment Scheduling - Doctor scheduling, booking logic, concurrency control 

Statistical Reports -Generate PDF/CSV hospital insights and analytics

A comprehensive hospital management system built with Spring Boot (backend) and React (frontend), featuring appointment scheduling, patient records, payment processing, and administrative reporting.Manage Patient Medical Records -CRUD operations for diagnoses, treatments, and prescriptions

Payment Handling -Integrate payment gateway sandbox, handle transactions, generate receipts

## 📋 Table of Contents

cd F:\MediWay\backend

- [Project Overview](#project-overview).\mvnw.cmd spring-boot:run

- [Team Members & Features](#team-members--features)

- [Technology Stack](#technology-stack)--for ngrok portal run ngrok http 5173 on terminal after running the frontend

- [Prerequisites](#prerequisites)## Public ngrok Link

- [Installation & Setup](#installation--setup)

- [Running the Application](#running-the-application)After starting the frontend, expose it using ngrok:

- [Running Unit Tests (For Viva)](#running-unit-tests-for-viva)

- [SOLID Principles Implementation](#solid-principles-implementation)```sh

- [API Documentation](#api-documentation)ngrok http 5173

```

---

Access your app via the generated ngrok URL (e.g., `https://your-ngrok-id.ngrok.io`).

## 🎯 Project Overview

**Update this section with your current ngrok link:**

MediWay is a full-stack hospital management system that streamlines healthcare operations including:

**Frontend ngrok URL:** [https://your-ngrok-id.ngrok.io](https://your-ngrok-id.ngrok.io)
- **Appointment Scheduling**: Doctor availability management and booking with concurrency control
- **Patient Management**: Comprehensive medical records with CRUD operations
- **Payment Processing**: Integrated PayPal sandbox for transactions and receipt generation
- **Administrative Dashboard**: Statistical reports with PDF/CSV export capabilities
- **User Management**: Role-based access for Admins, Doctors, and Patients

---

## 👥 Team Members & Features

### Team Member 1: Appointment Scheduling System

**Features**:
- Doctor scheduling and availability management
- Appointment booking logic with date/time validation
- Concurrency control to prevent double-booking
- Real-time appointment status updates (SCHEDULED, COMPLETED, CANCELLED)

**Test Files**:
- `AppointmentServiceTest.java` - 15 unit tests
- `SimpleAppointmentControllerTest.java` - 26 unit tests
- **Coverage**: 100% branch coverage

**Key Classes**:
- `AppointmentService.java`
- `SimpleAppointmentController.java`
- `AppointmentRepository.java`

**SOLID Principles**:
- **Single Responsibility**: Separate service for appointment logic
- **Dependency Inversion**: Uses repository interfaces
- **Open/Closed**: Extensible for new appointment types via status enum

---

### Team Member 2: Admin & Statistical Reports

**Features**:
- User and doctor management (CRUD operations)
- Statistical dashboard with key metrics
- PDF/CSV report generation for:
  - Monthly revenue analysis
  - Appointments by department
  - Daily appointment tracking
  - Pending payments overview
  - Doctor revenue breakdown
  - Top paying patients

**Test Files**:
- `AdminServiceTest.java` - 21 unit tests
- `AdminControllerTest.java` - 10 unit tests
- `SimpleReportsControllerTest.java` - 35 unit tests
- `ReportsServiceTest.java` - 20 unit tests
- **Coverage**: Admin (100%), Reports (91%)

**Key Classes**:
- `AdminService.java`
- `AdminController.java`
- `SimpleReportsController.java`
- `ReportsService.java`

**SOLID Principles**:
- **Single Responsibility**: Admin operations separated from reporting
- **Interface Segregation**: Separate interfaces for admin vs. reports
- **Liskov Substitution**: Service implementations interchangeable

---

### Team Member 3: Patient Medical Records

**Features**:
- Complete medical record CRUD operations
- Diagnoses, treatments, and prescriptions management
- Patient profile with emergency contacts
- QR code generation for patient identification
- Medical history tracking

**Test Files**:
- `MedicalRecordServiceTest.java` - 20 unit tests
- `MedicalRecordControllerTest.java` - 28 unit tests
- `SimpleMedicalRecordControllerTest.java` - 15 unit tests
- `SimpleProfileControllerTest.java` - 30 unit tests
- `QRCodeServiceTest.java` - 6 unit tests
- **Coverage**: MedicalRecord (89%), Profile (97%), QRCode (91%)

**Key Classes**:
- `MedicalRecordService.java`
- `MedicalRecordController.java`
- `SimpleMedicalRecordController.java`
- `SimpleProfileController.java`
- `QRCodeService.java`

**SOLID Principles**:
- **Single Responsibility**: QR code generation separated into own service
- **Dependency Inversion**: Uses interfaces for data access
- **Open/Closed**: Extensible for new medical record types

---

### Team Member 4: Payment Handling

**Features**:
- PayPal sandbox integration for payment processing
- Secure transaction handling
- Receipt generation with QR codes
- PDF receipt downloads
- Payment status tracking (PENDING, COMPLETED, FAILED)

**Test Files**:
- `SimplePayPalControllerTest.java` - 29 unit tests
- `ReceiptControllerTest.java` - 30 unit tests
- **Coverage**: Receipt (95%), PayPal (47% - simulated mode)

**Key Classes**:
- `SimplePayPalController.java`
- `ReceiptController.java`
- `PaymentRepository.java`
- `ReceiptRepository.java`

**SOLID Principles**:
- **Single Responsibility**: Payment and receipt logic separated
- **Dependency Inversion**: Abstract payment gateway interface
- **Open/Closed**: Supports multiple payment methods via strategy pattern

---

## 🛠 Technology Stack

### Backend
- **Framework**: Spring Boot 3.4.10
- **Language**: Java 17
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security with JWT
- **Testing**: JUnit 5, Mockito, MockMvc
- **Coverage**: JaCoCo (80%+ branch coverage)
- **Payment**: PayPal REST SDK
- **QR Code**: ZXing
- **PDF**: iText 7
- **Build Tool**: Maven

### Frontend
- **Framework**: React 18
- **Build Tool**: Vite
- **UI Library**: Material-UI / Custom CSS
- **HTTP Client**: Axios
- **Routing**: React Router

### DevOps
- **Containerization**: Docker
- **CI/CD**: GitHub Actions
- **Tunneling**: Ngrok (for PayPal webhooks)
- **Version Control**: Git

---

## 📦 Prerequisites

Before running the project, ensure you have:

- **Java Development Kit (JDK) 17** or higher
- **Node.js 18+** and npm
- **MySQL 8.0** or higher
- **Maven 3.8+** (or use included `mvnw`)
- **Git**
- **Ngrok** (for PayPal integration)
- **Docker** (optional, for containerized deployment)

---

## 🚀 Installation & Setup

### Backend Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Y3S1-WE20/MediWay.git
   cd MediWay/backend
   ```

2. **Configure Database**
   
   Create MySQL database:
   ```sql
   CREATE DATABASE mediwaydb;
   CREATE USER 'mediway_user'@'localhost' IDENTIFIED BY 'mediway_password';
   GRANT ALL PRIVILEGES ON mediwaydb.* TO 'mediway_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Update Application Properties**
   
   Edit `backend/src/main/resources/application.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/mediwaydb
   spring.datasource.username=mediway_user
   spring.datasource.password=mediway_password
   
   # JPA/Hibernate
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   
   # Server Port
   server.port=8080
   
   # PayPal Configuration (Sandbox)
   paypal.mode=sandbox
   paypal.client.id=YOUR_PAYPAL_CLIENT_ID
   paypal.client.secret=YOUR_PAYPAL_CLIENT_SECRET
   ```

4. **Install Dependencies**
   ```bash
   cd backend
   ./mvnw clean install -DskipTests
   ```

5. **Run Database Setup Scripts**
   ```bash
   mysql -u mediway_user -p mediwaydb < scripts/complete-setup.sql
   mysql -u mediway_user -p mediwaydb < scripts/setup_admin_doctor_accounts.sql
   ```

---

### Frontend Setup

1. **Navigate to Frontend Directory**
   ```bash
   cd frontend
   ```

2. **Install Dependencies**
   ```bash
   npm install
   ```

3. **Configure Environment Variables**
   
   Create `.env` file:
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   VITE_PAYPAL_CLIENT_ID=YOUR_PAYPAL_CLIENT_ID
   ```

---

### Ngrok Setup

Ngrok is required for PayPal webhook callbacks during payment processing.

1. **Install Ngrok**
   - Download from: https://ngrok.com/download
   - Extract and add to PATH

2. **Start Ngrok Tunnel** (for frontend)
   ```bash
   ngrok http 5173
   ```
   
   Copy the forwarding URL (e.g., `https://xxxx-xxxx.ngrok.io`)

3. **Update PayPal Configuration**
   
   In `application.properties`:
   ```properties
   paypal.return.url=https://xxxx-xxxx.ngrok.io/paypal-success
   paypal.cancel.url=https://xxxx-xxxx.ngrok.io/paypal-cancel
   ```

---

## 🏃 Running the Application

### Start Backend

```bash
cd backend
./mvnw spring-boot:run
```
Backend will start on: **http://localhost:8080**

### Start Frontend

```bash
cd frontend
npm run dev
```
Frontend will start on: **http://localhost:5173**

### Start Ngrok (for PayPal)

```bash
ngrok http 5173
```

### Access the Application

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **Ngrok Public URL**: `https://your-ngrok-id.ngrok.io`

### Default Login Credentials

**Admin Account:**
- Email: admin@mediway.com
- Password: admin123

**Doctor Account:**
- Email: doctor@mediway.com
- Password: doctor123

**Patient Account:**
- Email: patient@mediway.com
- Password: patient123

---

## 🧪 Running Unit Tests (For Viva)

### Run All Tests

```bash
cd backend
./mvnw clean test
```

### Run Tests by Feature Module

#### Team Member 1: Appointment Scheduling Tests
```bash
./mvnw test -Dtest="AppointmentServiceTest,SimpleAppointmentControllerTest"
```

**Expected Output:**
- ✅ AppointmentServiceTest: 15 tests passed
- ✅ SimpleAppointmentControllerTest: 26 tests passed
- ✅ Coverage: 100% branch coverage

**Demo Points for Viva:**
1. Show concurrency control test: `testCreateAppointment_ConcurrencyControl`
2. Demonstrate double-booking prevention
3. Show status transition tests (SCHEDULED → COMPLETED)

---

#### Team Member 2: Admin & Reports Tests
```bash
./mvnw test -Dtest="AdminServiceTest,AdminControllerTest,SimpleReportsControllerTest,ReportsServiceTest"
```

**Expected Output:**
- ✅ AdminServiceTest: 21 tests passed
- ✅ AdminControllerTest: 10 tests passed
- ✅ SimpleReportsControllerTest: 35 tests passed
- ✅ ReportsServiceTest: 20 tests passed
- ✅ Coverage: Admin (100%), Reports (91%)

**Demo Points for Viva:**
1. Show PDF generation test: `testGenerateMonthlyRevenuePDF`
2. Demonstrate dashboard statistics
3. Show CSV export functionality

---

#### Team Member 3: Medical Records Tests
```bash
./mvnw test -Dtest="MedicalRecordServiceTest,MedicalRecordControllerTest,SimpleMedicalRecordControllerTest,SimpleProfileControllerTest,QRCodeServiceTest"
```

**Expected Output:**
- ✅ MedicalRecordServiceTest: 20 tests passed
- ✅ MedicalRecordControllerTest: 28 tests passed
- ✅ SimpleMedicalRecordControllerTest: 15 tests passed
- ✅ SimpleProfileControllerTest: 30 tests passed
- ✅ QRCodeServiceTest: 6 tests passed
- ✅ Coverage: MedicalRecord (89%), Profile (97%), QRCode (91%)

**Demo Points for Viva:**
1. Show CRUD operation tests
2. Demonstrate QR code generation: `testGenerateQRCode`
3. Show patient profile updates with validation

---

#### Team Member 4: Payment System Tests
```bash
./mvnw test -Dtest="SimplePayPalControllerTest,ReceiptControllerTest"
```

**Expected Output:**
- ✅ SimplePayPalControllerTest: 29 tests passed
- ✅ ReceiptControllerTest: 30 tests passed
- ✅ Coverage: Receipt (95%), PayPal (47% simulated)

**Demo Points for Viva:**
1. Show payment creation test: `testCreatePayment_Success`
2. Demonstrate receipt generation with PDF
3. Show transaction status tracking

---

### Generate Coverage Report

```bash
cd backend
./mvnw jacoco:report
```

**View Report:**
Open `backend/target/site/jacoco/index.html` in browser

**Key Metrics:**
- Overall Branch Coverage: **80%+**
- Controller Package: **80%+**
- Service Package: **84%+**
- Total Tests: **472+**

---

### Useful Test Commands

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest="AppointmentServiceTest"

# Run single test method
./mvnw test -Dtest="AppointmentServiceTest#testCreateAppointment_Success"

# Run tests with coverage
./mvnw clean test jacoco:report

# Skip tests (for quick builds)
./mvnw package -DskipTests
```

---

## 🏗 SOLID Principles Implementation

### 1. Single Responsibility Principle (SRP)

**Each class has one reason to change**

```java
// ✅ GOOD: Each class has one responsibility
public class AppointmentService {
    // Only appointment business logic
    public Appointment createAppointment(...) { }
}

public class QRCodeService {
    // Only QR code generation
    public String generateQRCode(...) { }
}

public class EmailService {
    // Only email operations
    public void sendEmail(...) { }
}
```

**In Our Project:**
- `AppointmentService` - Only appointment operations
- `QRCodeService` - Only QR code generation
- `ReportsService` - Only report generation
- `ReceiptController` - Only receipt operations

---

### 2. Open/Closed Principle (OCP)

**Open for extension, closed for modification**

```java
// ✅ GOOD: Enum allows adding new statuses without modifying existing code
public enum AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED,
    RESCHEDULED  // New status added without breaking existing code
}

public class AppointmentService {
    public void updateStatus(Long id, AppointmentStatus newStatus) {
        // Works with any status without code changes
        appointment.setStatus(newStatus);
    }
}
```

**In Our Project:**
- Payment statuses (PENDING, COMPLETED, FAILED)
- Appointment statuses extensible
- User roles (ADMIN, DOCTOR, PATIENT) can be extended

---

### 3. Liskov Substitution Principle (LSP)

**Subtypes must be substitutable for base types**

```java
// ✅ GOOD: All implementations can substitute the interface
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;  // Works with any implementation
    
    public User authenticate(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }
}
```

**In Our Project:**
- All repositories extend `JpaRepository`
- Services depend on interfaces
- Mock repositories in tests work seamlessly

---

### 4. Interface Segregation Principle (ISP)

**Many client-specific interfaces are better than one general-purpose interface**

```java
// ✅ GOOD: Segregated interfaces
public interface CrudOperations {
    void create();
    void update();
    void delete();
}

public interface ReportGenerator {
    byte[] generateReport();
}

// Classes implement only what they need
public class PatientService implements CrudOperations {
    // Only CRUD methods
}

public class AdminService implements CrudOperations, ReportGenerator {
    // Both CRUD and reporting
}
```

**In Our Project:**
- Separate `Repository` interfaces for each entity
- `ReportsService` separate from CRUD services
- `QRCodeService` focused only on QR operations

---

### 5. Dependency Inversion Principle (DIP)

**Depend on abstractions, not concretions**

```java
// ✅ GOOD: Depend on interface via dependency injection
@RestController
public class AppointmentController {
    @Autowired
    private AppointmentService service;  // Interface, not implementation
}

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository repository;  // Interface injection
}
```

**In Our Project:**
- All controllers use `@Autowired` for dependency injection
- Services depend on `Repository` interfaces
- Payment gateway abstracted for future extensions
- Email service injectable for different providers

---

### SOLID Examples During Viva

**Demonstrating SRP:**
- Show separate service classes in `service/` package
- Point out each service has one responsibility

**Demonstrating OCP:**
- Show `AppointmentStatus` enum
- Explain how adding `RESCHEDULED` didn't require changing existing code

**Demonstrating LSP:**
- Show repository interfaces
- Run tests to demonstrate mocking works

**Demonstrating ISP:**
- Show `ReportsService` vs `AppointmentService`
- Point out focused interfaces

**Demonstrating DIP:**
- Show `@Autowired` in controllers
- Explain dependency injection

---

## 📚 API Documentation

### Authentication Endpoints

```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
```

### Appointment Endpoints

```http
GET    /api/appointments
POST   /api/appointments
GET    /api/appointments/{id}
PUT    /api/appointments/{id}
DELETE /api/appointments/{id}
GET    /api/appointments/doctor/{doctorId}
```

### Medical Records Endpoints

```http
GET    /api/medical-records/patient/{patientId}
POST   /api/medical-records
PUT    /api/medical-records/{id}
DELETE /api/medical-records/{id}
```

### Payment Endpoints

```http
POST   /api/payments/create-payment
POST   /api/payments/execute-payment
GET    /api/receipts/my-receipts
GET    /api/receipts/{receiptNumber}/pdf
```

### Reports Endpoints

```http
GET    /api/reports/dashboard
GET    /api/reports/monthly-revenue/pdf
GET    /api/reports/appointments-by-department
GET    /api/reports/revenue-by-doctor
```

---

## 🐛 Troubleshooting

### MySQL Connection Issues

```bash
# Check MySQL status
mysql --version
mysql -u root -p -e "SHOW DATABASES;"
```

### Port Already in Use

```bash
# Windows: Kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac: Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

### Maven Build Errors

```bash
# Clear Maven cache
./mvnw dependency:purge-local-repository

# Force update dependencies
./mvnw clean install -U
```

---

## 📊 Test Coverage Summary

| Module | Coverage | Tests | Status |
|--------|----------|-------|--------|
| **Overall** | 80%+ | 472+ | ✅ |
| Controllers | 80% | 230+ | ✅ |
| Services | 84% | 120+ | ✅ |
| Entities | 81% | 65+ | ✅ |
| Config | 100% | 20+ | ✅ |
| Security | 90% | 25+ | ✅ |

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/YourFeature`)
3. Commit changes (`git commit -m 'Add YourFeature'`)
4. Push to branch (`git push origin feature/YourFeature`)
5. Open Pull Request

---

## 👨‍💻 Team

**Course**: Web Engineering (WE20)  
**Year**: Year 3, Semester 1  
**Repository**: https://github.com/Y3S1-WE20/MediWay

---

**Last Updated**: October 24, 2025  
**Version**: 1.0.0
