# Appointment & Payment Testing Guide

## Quick Start

### 1. Start Backend
```powershell
cd F:\MediWay\backend
.\mvnw.cmd spring-boot:run
```
Wait for "HikariPool-1 - Start completed" to confirm MySQL connection.

### 2. Insert Sample Doctors
Open MySQL Workbench and run:
```powershell
cd F:\MediWay\backend\scripts
# Open insert-sample-doctors.sql in MySQL Workbench and execute it
```
Or run the SQL file directly in MySQL Workbench connected to `mediwaydb`.

### 3. Start Frontend
```powershell
cd F:\MediWay\frontend
npm run dev
```
Frontend will open at http://localhost:5174

### 4. Test the Complete Flow

#### Step 1: Register/Login
- Go to http://localhost:5174
- Register a new patient account or login with existing credentials
- Use email: `tester1@gmail.com` (or your test email)

#### Step 2: Book an Appointment
1. Navigate to **Appointments** page (http://localhost:5174/appointments)
2. Click **Book New Appointment**
3. Select a doctor from the dropdown (e.g., Dr. Sarah Johnson - Cardiology)
4. Choose a date (today or future)
5. Choose a time (any valid time like 10:00)
6. Add a reason (optional): "Regular checkup"
7. Click **Book Appointment**
8. You'll be redirected back to Appointments page

#### Step 3: Pay for Appointment
1. On the Appointments page, you'll see your new appointment with status **PENDING**
2. You'll see a **Pay Now** button with the consultation fee amount
3. Click **Pay Now**
4. You'll be redirected to PayPal sandbox
5. Login with your PayPal sandbox buyer account
6. Approve the payment
7. You'll be redirected back to **Payment Success** page
8. View the receipt

#### Step 4: Verify in Database
Open MySQL Workbench and run:
```sql
-- Check appointments
SELECT 
  BIN_TO_UUID(appointment_id) as appointment_id,
  BIN_TO_UUID(patient_id) as patient_id,
  BIN_TO_UUID(doctor_id) as doctor_id,
  appointment_date,
  appointment_time,
  status,
  reason,
  consultation_fee
FROM appointments
ORDER BY created_at DESC;

-- Check payments
SELECT 
  BIN_TO_UUID(payment_id) as payment_id,
  BIN_TO_UUID(user_id) as user_id,
  amount,
  currency,
  status,
  payment_method,
  description
FROM payments
ORDER BY created_at DESC;
```

## API Endpoints Created

### Appointments
- `POST /api/appointments` - Create new appointment
- `GET /api/appointments/my` - Get my appointments
- `GET /api/appointments` - Get all appointments (admin only)
- `GET /api/appointments/{id}` - Get appointment by ID
- `DELETE /api/appointments/{id}` - Cancel appointment
- `PATCH /api/appointments/{id}/status` - Update appointment status (admin/doctor)
- `GET /api/appointments/doctors` - Get all available doctors
- `GET /api/appointments/doctors/{id}` - Get doctor by ID

### Payments (already exists)
- `POST /api/payments/create` - Create payment
- `POST /api/payments/execute` - Execute approved payment
- `POST /api/payments/cancel` - Cancel payment
- `GET /api/payments/my-payments` - Get my payments
- `GET /api/payments/receipts/my-receipts` - Get my receipts

## Troubleshooting

### No doctors showing in dropdown?
Run the `insert-sample-doctors.sql` script in MySQL Workbench.

### Backend not connecting to MySQL?
- Ensure MySQL is running on port 3306
- Check credentials in `application.properties`:
  - URL: jdbc:mysql://localhost:3306/mediwaydb
  - Username: mediway_user
  - Password: admin

### Frontend can't reach backend?
- Ensure backend is running on port 8080
- Check `frontend/src/api/api.js` baseURL: http://localhost:8080/api

### Payment not working?
- Ensure you have PayPal sandbox credentials configured in backend
- Check `application.properties` for PayPal client ID and secret
- Payment status should change from CREATED → APPROVED → COMPLETED

## Files Created

### Backend
- `entity/Doctor.java` - Doctor entity
- `entity/Appointment.java` - Appointment entity
- `repository/DoctorRepository.java` - Doctor repository
- `repository/AppointmentRepository.java` - Appointment repository
- `dto/request/AppointmentRequest.java` - Appointment request DTO
- `dto/response/AppointmentResponse.java` - Appointment response DTO
- `dto/response/DoctorResponse.java` - Doctor response DTO
- `service/AppointmentService.java` - Appointment business logic
- `controller/AppointmentController.java` - Appointment REST endpoints
- `scripts/insert-sample-doctors.sql` - Sample doctors SQL

### Frontend
- Updated `pages/BookAppointment.jsx` - Real backend integration
- Updated `pages/Appointments.jsx` - Fetch real appointments + payment integration
- Updated `api/endpoints.js` - Added appointment endpoints

## Next Steps

After successful testing:
1. The appointment booking + payment flow is complete
2. You can extend the system with:
   - Email notifications for bookings
   - SMS reminders
   - Doctor availability calendar
   - Appointment history filters
   - Receipt download/print functionality
