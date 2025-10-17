# Quick Test Guide - All Features

## 🧪 Complete Feature Testing Checklist

### 1. Authentication ✓
```
Login: http://localhost:5174/login
- Email: john@example.com
- Password: password123
- Expected: Redirect to home, see "User" in navbar

Register: http://localhost:5174/register
- Fill form with new details
- Expected: Success, QR code shown, redirect to login
```

### 2. Appointments ✓
```
View: http://localhost:5174/appointments
- Expected: See list of appointments
- Each shows: Doctor name, specialization, date, time, fee
- Blue "SCHEDULED" badge
- Yellow "Pay Now ($500)" button
- Red "Cancel" button

Book: http://localhost:5174/book-appointment
- Select doctor from dropdown
- Pick date and time
- Add reason
- Expected: Success, redirect to appointments
```

### 3. Profile ✓
```
View: http://localhost:5174/profile
- Expected: See your name, email, phone
- Patient ID: PAT-1
- Role: PATIENT
- Health card with initial
- QR code placeholder
- "Edit" button works
```

### 4. Reports ✓
```
View: http://localhost:5174/reports
- Expected: No errors
- 4 stat cards showing:
  * Total Patients
  * Total Doctors (should show 3)
  * Total Appointments
  * Total Revenue
- 2 breakdown cards:
  * Appointments by Status
  * Payments by Status
```

### 5. Payments ✓
```
View: http://localhost:5174/payments
- Expected: Shows payment summary
- Total Paid, Pending, Total Payments cards
- "Create New Payment" button exists
- But logical flow is: Book appointment → Pay from appointments page
```

---

## 🎨 Visual Cues to Verify

### Appointment Status Colors:
- 🔵 **SCHEDULED** (Blue) - New appointment, not paid
- 🟢 **CONFIRMED** (Green) - Paid, ready for consultation
- 🟣 **COMPLETED** (Purple) - Consultation finished
- 🔴 **CANCELLED** (Red) - Cancelled

### Payment Indicators:
- 🟡 **"Pay Now ($500)"** button - Yellow background, shows when unpaid
- 🟢 **"Paid ✓"** badge - Green, shows when payment complete

---

## 🐛 Known Limitations (Prototype)

1. **Payment Processing**:
   - "Pay Now" button exists but doesn't process actual payments yet
   - Need to implement PayPal controller (code provided)
   - Currently shows button and UI only

2. **Authentication**:
   - No real JWT tokens (simplified for prototype)
   - Using simple token format: "simple-token-{userId}"
   - Passwords not hashed

3. **Default Values**:
   - All consultation fees: $500
   - Default patient ID: 1
   - QR code is placeholder image

4. **Data**:
   - Sample doctors in database: Dr. Smith, Dr. Jones, Dr. Wilson
   - Sample users: john@example.com, jane@example.com, bob@example.com

---

## ✅ Success Indicators

**Everything is working if you see:**

1. ✅ Login works with john@example.com
2. ✅ Can book appointments successfully
3. ✅ Appointments show doctor names (not just IDs)
4. ✅ Reports page loads without errors
5. ✅ Profile shows your name and email
6. ✅ "Pay Now" button appears on appointments
7. ✅ Can cancel appointments
8. ✅ No CORS errors in browser console

---

## 🚨 If Something Doesn't Work

### Reports Page Shows Error:
- Check backend is running on port 8080
- Check browser console for actual error
- Verify database has data (run SIMPLE_DATABASE.sql)

### Profile Shows Empty:
- Make sure you're logged in
- Check browser console for errors
- Backend should return user data from /api/profile

### Appointments Empty:
- Book an appointment first
- Check database: `SELECT * FROM appointments;`
- Verify backend running

### Pay Now Button Not Showing:
- Appointment must be SCHEDULED status
- Must have consultationFee value
- Must not be already paid (isPaid: false)

---

## 📊 Database Quick Check

Open MySQL Workbench and run:

```sql
-- Check doctors
SELECT * FROM doctors;
-- Should show 3 doctors

-- Check users
SELECT * FROM users;
-- Should show 3 users

-- Check appointments
SELECT * FROM appointments;
-- Should show your booked appointments

-- Check if appointment has needed fields
SELECT id, doctor_id, patient_id, status, appointment_date 
FROM appointments;
```

---

## 🎯 Prototype Complete!

Your working features:
1. ✅ User registration and login
2. ✅ View available doctors
3. ✅ Book appointments
4. ✅ View appointments with full details
5. ✅ See payment buttons (UI ready)
6. ✅ View profile and health card
7. ✅ View hospital reports and statistics
8. ✅ Cancel appointments

**Ready for demo! 🚀**

To make payments actually work:
- See `PAYMENT_FLOW_IMPLEMENTATION.md`
- Implement SimplePayPalController
- Add PayPal credentials
- Create payment success/cancel pages
- Estimated: 30-60 minutes

But for prototype demo, current state is fully functional! ✨
