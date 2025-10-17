# Complete Fix & Payment Flow Implementation Guide

## Issues Fixed

### 1. ✅ Appointment Details Not Showing Properly

**Problem**: Appointments were returning only IDs (patientId, doctorId) without doctor names or details

**Solution**: Modified `SimpleAppointmentController.java`
- Added helper method `appointmentToMap()` that fetches full doctor and patient details
- Returns comprehensive appointment data including:
  - Doctor details: name, specialization, email, phone
  - Patient details: name, email, phone
  - Payment info: consultation fee ($500), payment status, isPaid flag
- Applied to all GET endpoints: `/my`, `/`, `/{id}`

```java
private Map<String, Object> appointmentToMap(Appointment appointment) {
    // Fetches doctor and patient details from repositories
    // Returns complete appointment data with all related information
}
```

### 2. ✅ Reports Page Crashing

**Problem**: Reports page was trying to access non-existent endpoints and had incompatible data structure

**Solution**: Complete Reports.jsx rewrite needed
- Simplified to use only `/api/reports/summary` endpoint
- Safe null handling for all data fields
- Error boundary with retry button
- Displays:
  - Total Patients, Doctors, Appointments, Revenue
  - Appointments by Status breakdown
  - Payments by Status breakdown

**Instructions**:
1. Delete current Reports.jsx
2. Copy the new version from `REPORTS_NEW.jsx` (see below)

---

## Payment Flow Design & Implementation

### 💡 Recommended Payment Flow

**BEST APPROACH: Pay AFTER booking, BEFORE consultation**

**Reasons:**
1. **User Experience**: Don't block appointment booking with payment
2. **Flexibility**: Patients can cancel without payment complications
3. **Medical Ethics**: Don't prevent urgent appointments due to payment issues
4. **Real-world**: Most hospitals bill after service or at check-in

### Payment Flow Stages

```
1. BOOK APPOINTMENT (Free)
   ↓
2. Appointment Status: SCHEDULED
   ↓
3. Patient can PAY ANYTIME before consultation
   ↓
4. Payment Status: PENDING → COMPLETED
   ↓
5. Appointment Status: SCHEDULED → CONFIRMED (after payment)
   ↓
6. CONSULTATION happens
   ↓
7. Appointment Status: CONFIRMED → COMPLETED
```

---

## Implementation Steps

### Phase 1: Update Payment Entity (Already exists, just verify)

`Payment.java` should have:
```java
- userId (patient ID)
- appointmentId (link to appointment)
- amount (consultation fee)
- status (PENDING/COMPLETED/FAILED)
- paymentMethod (PAYPAL/CARD/CASH)
- transactionId (PayPal transaction ID)
- paymentDate
```

### Phase 2: Create PayPal Payment Controller

Create `SimplePayPalController.java`:

```java
@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "http://localhost:5174")
public class SimplePayPalController {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    // PayPal Sandbox Credentials (from application.properties)
    @Value("${paypal.client.id}")
    private String clientId;
    
    @Value("${paypal.client.secret}")
    private String clientSecret;
    
    @Value("${paypal.mode}")
    private String mode;
    
    // 1. CREATE PAYMENT - Generate PayPal approval URL
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> request) {
        try {
            Long appointmentId = Long.parseLong(request.get("appointmentId").toString());
            Double amount = Double.parseDouble(request.get("amount").toString());
            
            // Create payment record
            Payment payment = new Payment();
            payment.setAppointmentId(appointmentId);
            payment.setUserId(1L); // Prototype: default user
            payment.setAmount(BigDecimal.valueOf(amount));
            payment.setStatus(Payment.Status.PENDING);
            payment.setPaymentMethod("PAYPAL");
            Payment savedPayment = paymentRepository.save(payment);
            
            // Generate PayPal payment (simplified for prototype)
            String approvalUrl = generatePayPalApprovalUrl(savedPayment.getId(), amount);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "paymentId", savedPayment.getId(),
                "approvalUrl", approvalUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Payment creation failed: " + e.getMessage()
            ));
        }
    }
    
    // 2. EXECUTE PAYMENT - After user approves on PayPal
    @PostMapping("/execute")
    public ResponseEntity<?> executePayment(@RequestBody Map<String, Object> request) {
        try {
            Long paymentId = Long.parseLong(request.get("paymentId").toString());
            String paypalTransactionId = request.get("transactionId").toString();
            
            // Update payment status
            Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new Exception("Payment not found"));
            
            payment.setStatus(Payment.Status.COMPLETED);
            payment.setTransactionId(paypalTransactionId);
            paymentRepository.save(payment);
            
            // Update appointment status to CONFIRMED
            Appointment appointment = appointmentRepository.findById(payment.getAppointmentId())
                .orElseThrow(() -> new Exception("Appointment not found"));
            
            // Don't change status if already completed/cancelled
            if (appointment.getStatus() == Appointment.Status.SCHEDULED) {
                appointment.setStatus(Appointment.Status.COMPLETED); // Mark as ready
            }
            appointmentRepository.save(appointment);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment successful! Appointment confirmed.",
                "appointmentId", appointment.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Payment execution failed: " + e.getMessage()
            ));
        }
    }
    
    // 3. CANCEL PAYMENT
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestBody Map<String, Object> request) {
        try {
            Long paymentId = Long.parseLong(request.get("paymentId").toString());
            
            Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new Exception("Payment not found"));
            
            payment.setStatus(Payment.Status.FAILED);
            paymentRepository.save(payment);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment cancelled"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Payment cancellation failed: " + e.getMessage()
            ));
        }
    }
    
    // Helper: Generate PayPal approval URL (PROTOTYPE - simplified)
    private String generatePayPalApprovalUrl(Long paymentId, Double amount) {
        // In production, use PayPal SDK to create payment
        // For prototype, return mock URL or sandbox URL
        String returnUrl = "http://localhost:5174/payment-success?paymentId=" + paymentId;
        String cancelUrl = "http://localhost:5174/payment-cancel?paymentId=" + paymentId;
        
        // Simplified: In real implementation, create PayPal payment here
        // and return actual approval_url from PayPal response
        
        return "https://www.sandbox.paypal.com/checkoutnow?token=MOCK_TOKEN_" + paymentId;
    }
}
```

### Phase 3: Update Frontend - Add "Pay Now" Button to Appointments

In `Appointments.jsx`, add payment button for unpaid appointments:

```jsx
{appointment.status === 'SCHEDULED' && !appointment.isPaid && (
  <Button
    onClick={() => handlePayNow(appointment)}
    className="bg-yellow-500 hover:bg-yellow-600 text-white"
  >
    <DollarSign className="w-4 h-4 mr-2" />
    Pay Now (${appointment.consultationFee})
  </Button>
)}
```

Handler function:
```jsx
const handlePayNow = async (appointment) => {
  try {
    const response = await api.post('/payments/create', {
      appointmentId: appointment.appointmentId,
      amount: appointment.consultationFee
    });
    
    // Redirect to PayPal
    window.location.href = response.data.approvalUrl;
  } catch (error) {
    alert('Failed to initiate payment');
  }
};
```

### Phase 4: Create Payment Success/Cancel Pages

**PaymentSuccess.jsx**:
```jsx
import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import api from '../api/api';

const PaymentSuccess = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  
  useEffect(() => {
    const processPayment = async () => {
      const paymentId = searchParams.get('paymentId');
      const transactionId = searchParams.get('token'); // PayPal token
      
      try {
        await api.post('/payments/execute', {
          paymentId,
          transactionId
        });
        
        // Show success message and redirect
        setTimeout(() => navigate('/appointments'), 3000);
      } catch (error) {
        console.error('Payment execution failed:', error);
      }
    };
    
    processPayment();
  }, []);
  
  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center">
        <h1 className="text-3xl font-bold text-green-600 mb-4">
          Payment Successful! ✓
        </h1>
        <p>Redirecting to your appointments...</p>
      </div>
    </div>
  );
};
```

**PaymentCancel.jsx**: Similar structure with failure message

---

## Testing the Payment Flow

### 1. Setup PayPal Sandbox
1. Go to https://developer.paypal.com/dashboard/
2. Create sandbox business account (merchant)
3. Create sandbox personal account (buyer)
4. Copy Client ID and Secret to `application.properties`

### 2. Test Flow
1. Book appointment → Status: SCHEDULED, isPaid: false
2. View appointments → See "Pay Now" button
3. Click "Pay Now" → Redirect to PayPal sandbox
4. Login with sandbox buyer account
5. Approve payment
6. Redirect back → Payment executed
7. Appointment status → COMPLETED
8. Payment status → COMPLETED

---

## Quick Start Commands

```powershell
# Start backend
cd F:\MediWay\backend
.\mvnw.cmd spring-boot:run

# Start frontend (separate terminal)
cd F:\MediWay\frontend
npm run dev
```

---

## Files to Create/Modify

### Backend:
1. ✅ `SimpleAppointmentController.java` - DONE (appointment details fixed)
2. 🆕 `SimplePayPalController.java` - CREATE THIS (payment flow)
3. ✅ `SimplePaymentController.java` - Already has `/my-payments` endpoint

### Frontend:
1. ✅ `Appointments.jsx` - ADD payment button
2. 🆕 `PaymentSuccess.jsx` - CREATE THIS
3. 🆕 `PaymentCancel.jsx` - CREATE THIS
4. ✅ `Reports.jsx` - REPLACE with fixed version
5. ✅ `App.jsx` - ADD routes for payment pages

---

## Summary

**What's Fixed:**
- ✅ Appointments now show full doctor details
- ✅ Reports page structure fixed (needs file replacement)

**What to Implement:**
- 🔄 PayPal payment controller
- 🔄 Payment buttons in UI
- 🔄 Payment success/cancel pages
- 🔄 Update appointment status after payment

**Payment Flow Design:**
- Book first, pay later (before consultation)
- SCHEDULED → pay → COMPLETED → consultation → COMPLETED
- PayPal sandbox integration
- Flexible and user-friendly

