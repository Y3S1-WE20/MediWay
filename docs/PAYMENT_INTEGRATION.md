# PayPal Payment Integration for MediWay

## Overview

This document describes the PayPal sandbox payment integration for the MediWay Health Management System. The integration allows patients to make secure payments for medical appointments using PayPal.

## Table of Contents

1. [Features](#features)
2. [Architecture](#architecture)
3. [Setup Instructions](#setup-instructions)
4. [API Endpoints](#api-endpoints)
5. [Payment Flow](#payment-flow)
6. [Testing Guide](#testing-guide)
7. [Frontend Integration](#frontend-integration)
8. [Troubleshooting](#troubleshooting)

---

## Features

✅ **PayPal Sandbox Integration**: Test payments without real money
✅ **Payment Creation**: Create PayPal payment intents with amount and description
✅ **Payment Execution**: Complete payments after user approval
✅ **Payment Cancellation**: Cancel pending payments
✅ **Automatic Receipt Generation**: Generate receipts after successful payments
✅ **Payment History**: Track all user payments and receipts
✅ **Role-Based Access Control**: Secure endpoints with JWT authentication
✅ **Transaction Management**: Store payment and receipt records in database

---

## Architecture

### Components

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (React)                        │
│  - Payment form with amount, description                     │
│  - Redirect to PayPal for approval                          │
│  - Handle success/cancel callbacks                          │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│              PaymentController (REST API)                    │
│  POST /api/payments/create                                   │
│  POST /api/payments/execute                                  │
│  POST /api/payments/cancel                                   │
│  GET  /api/payments/{id}                                     │
│  GET  /api/payments/my-payments                              │
│  GET  /api/payments/receipt/{receiptNumber}                  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│         PayPalService & ReceiptService (Business Logic)      │
│  - Create PayPal payment intent                              │
│  - Execute payment with payer approval                       │
│  - Generate unique receipt after completion                  │
│  - Store transaction history                                 │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│              PayPal REST API (Sandbox)                       │
│  - Process payment requests                                  │
│  - Return approval URLs                                      │
│  - Execute approved payments                                 │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│           Database (H2/MySQL)                                │
│  - payments table: Payment transactions                      │
│  - receipts table: Payment receipts                          │
└─────────────────────────────────────────────────────────────┘
```

### Database Schema

**payments table:**
```sql
payment_id          UUID PRIMARY KEY
user_id             UUID NOT NULL
appointment_id      UUID
amount              DECIMAL(10,2) NOT NULL
currency            VARCHAR(3) NOT NULL DEFAULT 'USD'
status              ENUM('CREATED','APPROVED','COMPLETED','FAILED','CANCELLED')
payment_method      ENUM('PAYPAL','CREDIT_CARD','DEBIT_CARD')
paypal_payment_id   VARCHAR(100) UNIQUE
payer_id            VARCHAR(100)
description         VARCHAR(500)
return_url          VARCHAR(500)
cancel_url          VARCHAR(500)
approval_url        VARCHAR(500)
created_at          TIMESTAMP NOT NULL
updated_at          TIMESTAMP
completed_at        TIMESTAMP
```

**receipts table:**
```sql
receipt_id          UUID PRIMARY KEY
receipt_number      VARCHAR(50) UNIQUE NOT NULL
payment_id          UUID NOT NULL
user_id             UUID NOT NULL
appointment_id      UUID
amount              DECIMAL(10,2) NOT NULL
currency            VARCHAR(3) NOT NULL
payment_method      VARCHAR(20) NOT NULL
transaction_id      VARCHAR(100)
payer_email         VARCHAR(100)
payer_name          VARCHAR(100)
description         VARCHAR(500)
payment_date        TIMESTAMP NOT NULL
created_at          TIMESTAMP NOT NULL
pdf_path            VARCHAR(500)
```

---

## Setup Instructions

### 1. Create PayPal Sandbox Account

1. Go to https://developer.paypal.com/
2. Sign up for a PayPal Developer account
3. Navigate to **Dashboard** → **Apps & Credentials**
4. Select **Sandbox** mode
5. Create a new app or use the default app
6. Copy your **Client ID** and **Secret**

### 2. Create Sandbox Test Accounts

1. In PayPal Developer Dashboard, go to **Sandbox** → **Accounts**
2. Create two test accounts:
   - **Personal Account** (buyer/patient) - for making payments
   - **Business Account** (merchant/clinic) - for receiving payments
3. Note the email and password for both accounts

### 3. Configure Backend

Add PayPal credentials to your environment variables or `application.properties`:

**Option A: Environment Variables (Recommended)**

```powershell
# PowerShell
$env:PAYPAL_CLIENT_ID = "YOUR_SANDBOX_CLIENT_ID"
$env:PAYPAL_CLIENT_SECRET = "YOUR_SANDBOX_CLIENT_SECRET"

# Or use setx for permanent (restart required)
setx PAYPAL_CLIENT_ID "YOUR_SANDBOX_CLIENT_ID"
setx PAYPAL_CLIENT_SECRET "YOUR_SANDBOX_CLIENT_SECRET"
```

**Option B: Direct Configuration (Development Only)**

Edit `backend/src/main/resources/application.properties`:

```properties
paypal.mode=sandbox
paypal.client.id=YOUR_SANDBOX_CLIENT_ID
paypal.client.secret=YOUR_SANDBOX_CLIENT_SECRET
```

⚠️ **Security Warning**: Never commit real credentials to source control!

### 4. Build and Run Backend

```powershell
cd F:\MediWay\backend
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

Backend will start on: http://localhost:8080

---

## API Endpoints

All endpoints require JWT authentication (except `/health`).

### Health Check

```http
GET /api/payments/health
```

**Response:**
```json
{
  "status": "UP",
  "service": "Payment Service",
  "timestamp": "2025-10-15T22:30:00"
}
```

---

### Create Payment

Creates a PayPal payment intent and returns approval URL.

```http
POST /api/payments/create
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "amount": 150.00,
  "currency": "USD",
  "description": "Consultation fee for Dr. Smith",
  "appointmentId": "123e4567-e89b-12d3-a456-426614174000",
  "returnUrl": "http://localhost:5174/payment-success",
  "cancelUrl": "http://localhost:5174/payment-cancel",
  "paymentMethod": "PAYPAL"
}
```

**Response:**
```json
{
  "paymentId": "a1b2c3d4-e5f6-7890-ab12-cd34ef567890",
  "userId": "user-uuid",
  "appointmentId": "appointment-uuid",
  "amount": 150.00,
  "currency": "USD",
  "status": "CREATED",
  "paymentMethod": "PAYPAL",
  "paypalPaymentId": "PAYID-M123456-78901234567890123",
  "description": "Consultation fee for Dr. Smith",
  "approvalUrl": "https://www.sandbox.paypal.com/checkoutnow?token=EC-12345678901234567",
  "createdAt": "2025-10-15T22:30:00",
  "message": "Payment created successfully. Please complete the payment."
}
```

---

### Execute Payment

Completes the payment after user approves on PayPal.

```http
POST /api/payments/execute?paymentId=PAYID-M123456&PayerID=ABCDEFGH123
Authorization: Bearer <JWT_TOKEN>
```

**Response:**
```json
{
  "paymentId": "a1b2c3d4-e5f6-7890-ab12-cd34ef567890",
  "userId": "user-uuid",
  "amount": 150.00,
  "currency": "USD",
  "status": "COMPLETED",
  "paymentMethod": "PAYPAL",
  "paypalPaymentId": "PAYID-M123456-78901234567890123",
  "completedAt": "2025-10-15T22:32:00",
  "message": "Payment completed successfully!"
}
```

---

### Cancel Payment

Cancels a pending payment.

```http
POST /api/payments/cancel?paymentId=PAYID-M123456
Authorization: Bearer <JWT_TOKEN>
```

**Response:**
```json
{
  "paymentId": "a1b2c3d4-e5f6-7890-ab12-cd34ef567890",
  "status": "CANCELLED",
  "message": "Payment cancelled successfully."
}
```

---

### Get Payment by ID

```http
GET /api/payments/{paymentId}
Authorization: Bearer <JWT_TOKEN>
```

---

### Get User Payments

```http
GET /api/payments/my-payments
Authorization: Bearer <JWT_TOKEN>
```

**Response:**
```json
[
  {
    "paymentId": "uuid",
    "amount": 150.00,
    "currency": "USD",
    "status": "COMPLETED",
    "description": "Consultation fee",
    "createdAt": "2025-10-15T22:30:00",
    "completedAt": "2025-10-15T22:32:00"
  }
]
```

---

### Get Receipt by Payment ID

```http
GET /api/payments/receipt/payment/{paymentId}
Authorization: Bearer <JWT_TOKEN>
```

**Response:**
```json
{
  "receiptId": "receipt-uuid",
  "receiptNumber": "RCP-20251015-A1B2C3D4",
  "paymentId": "payment-uuid",
  "amount": 150.00,
  "currency": "USD",
  "paymentMethod": "PAYPAL",
  "transactionId": "1AB23456CD789012E",
  "payerEmail": "patient@sandbox.paypal.com",
  "payerName": "John Doe",
  "description": "Consultation fee",
  "paymentDate": "2025-10-15T22:32:00",
  "createdAt": "2025-10-15T22:32:01"
}
```

---

### Get User Receipts

```http
GET /api/payments/receipts/my-receipts
Authorization: Bearer <JWT_TOKEN>
```

---

## Payment Flow

### Step-by-Step Process

```
1. Patient initiates payment
   ↓
2. Frontend calls POST /api/payments/create
   ↓
3. Backend creates PayPal payment intent
   ↓
4. Backend returns approvalUrl
   ↓
5. Frontend redirects user to PayPal approval URL
   ↓
6. User logs into PayPal sandbox and approves payment
   ↓
7. PayPal redirects to returnUrl with paymentId and PayerID
   ↓
8. Frontend calls POST /api/payments/execute with paymentId and PayerID
   ↓
9. Backend executes payment with PayPal
   ↓
10. Backend generates receipt automatically
    ↓
11. Frontend shows success message and receipt
```

### State Diagram

```
CREATED → (user approves) → APPROVED → (execute) → COMPLETED
   ↓                                                      
   → (user cancels) → CANCELLED
   ↓
   → (error) → FAILED
```

---

## Testing Guide

### Test with Sandbox Accounts

1. **Login to backend** with test patient account:
```bash
POST http://localhost:8080/api/auth/login
{
  "email": "test.patient@mediway.com",
  "password": "patient123"
}
```

2. **Create payment** with returned JWT token:
```bash
POST http://localhost:8080/api/payments/create
Authorization: Bearer <token>
{
  "amount": 50.00,
  "currency": "USD",
  "description": "Test consultation payment",
  "returnUrl": "http://localhost:5174/payment-success",
  "cancelUrl": "http://localhost:5174/payment-cancel",
  "paymentMethod": "PAYPAL"
}
```

3. **Copy approvalUrl** from response

4. **Open approvalUrl** in browser

5. **Login with PayPal sandbox personal account**:
   - Email: Your sandbox buyer account email
   - Password: Your sandbox buyer account password

6. **Approve payment** on PayPal page

7. **Copy paymentId and PayerID** from redirect URL

8. **Execute payment**:
```bash
POST http://localhost:8080/api/payments/execute?paymentId=PAYID-XXX&PayerID=XXX
Authorization: Bearer <token>
```

9. **Verify receipt**:
```bash
GET http://localhost:8080/api/payments/receipts/my-receipts
Authorization: Bearer <token>
```

### PowerShell Test Script

```powershell
# Set variables
$baseUrl = "http://localhost:8080/api"
$email = "test.patient@mediway.com"
$password = "patient123"

# 1. Login
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST `
    -ContentType "application/json" `
    -Body (@{email=$email; password=$password} | ConvertTo-Json)

$token = $loginResponse.token
Write-Host "✅ Logged in. Token: $token"

# 2. Create payment
$paymentRequest = @{
    amount = 50.00
    currency = "USD"
    description = "Test payment"
    returnUrl = "http://localhost:5174/success"
    cancelUrl = "http://localhost:5174/cancel"
    paymentMethod = "PAYPAL"
}

$paymentResponse = Invoke-RestMethod -Uri "$baseUrl/payments/create" -Method POST `
    -ContentType "application/json" `
    -Headers @{Authorization="Bearer $token"} `
    -Body ($paymentRequest | ConvertTo-Json)

Write-Host "✅ Payment created. Approval URL:"
Write-Host $paymentResponse.approvalUrl

# Open approval URL in browser
Start-Process $paymentResponse.approvalUrl

Write-Host "`n⚠️  Complete payment in browser, then run execute command with returned params"
```

---

## Frontend Integration

### React Example

```jsx
import { useState } from 'react';
import api from '../api/api';

function PaymentForm({ appointmentId, amount }) {
  const [loading, setLoading] = useState(false);

  const handlePayment = async () => {
    setLoading(true);
    
    try {
      // Create payment
      const response = await api.post('/payments/create', {
        amount: amount,
        currency: 'USD',
        description: `Payment for appointment ${appointmentId}`,
        appointmentId: appointmentId,
        returnUrl: `${window.location.origin}/payment-success`,
        cancelUrl: `${window.location.origin}/payment-cancel`,
        paymentMethod: 'PAYPAL'
      });

      // Redirect to PayPal
      window.location.href = response.data.approvalUrl;
    } catch (error) {
      console.error('Payment error:', error);
      alert('Failed to create payment');
    } finally {
      setLoading(false);
    }
  };

  return (
    <button onClick={handlePayment} disabled={loading}>
      {loading ? 'Processing...' : `Pay $${amount} with PayPal`}
    </button>
  );
}

export default PaymentForm;
```

### Payment Success Handler

```jsx
import { useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import api from '../api/api';

function PaymentSuccess() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const executePayment = async () => {
      const paymentId = searchParams.get('paymentId');
      const payerId = searchParams.get('PayerID');

      if (!paymentId || !payerId) {
        navigate('/payment-error');
        return;
      }

      try {
        const response = await api.post(
          `/payments/execute?paymentId=${paymentId}&PayerID=${payerId}`
        );

        // Show success message
        alert('Payment completed successfully!');
        
        // Navigate to receipt
        navigate(`/receipts/${response.data.paymentId}`);
      } catch (error) {
        console.error('Execute payment error:', error);
        navigate('/payment-error');
      }
    };

    executePayment();
  }, [searchParams, navigate]);

  return <div>Processing payment...</div>;
}

export default PaymentSuccess;
```

---

## Troubleshooting

### Common Issues

**1. "Authentication failed" error**

❌ Problem: PayPal credentials are invalid or not set

✅ Solution: 
- Verify credentials in PayPal Developer Dashboard
- Check environment variables are set correctly
- Ensure mode is set to "sandbox"

**2. "Payment not found" error**

❌ Problem: PaymentId doesn't exist in database

✅ Solution:
- Verify payment was created successfully
- Check database for payment record
- Ensure correct paymentId is being used

**3. PayPal redirects to error page**

❌ Problem: Return/Cancel URLs are invalid

✅ Solution:
- Use valid HTTP/HTTPS URLs
- Ensure URLs are accessible from browser
- For local testing, use http://localhost:5174/...

**4. "Insufficient permissions" error**

❌ Problem: User doesn't have required role

✅ Solution:
- Ensure user is logged in with JWT token
- Verify user has PATIENT or ADMIN role
- Check Authorization header is set correctly

**5. Receipt not generated**

❌ Problem: Payment completed but no receipt

✅ Solution:
- Check payment status is COMPLETED
- Verify ReceiptService is running
- Check database receipts table
- Look for errors in backend logs

### Debugging

**Enable debug logging:**

```properties
# application.properties
logging.level.com.mediway.backend.service=DEBUG
logging.level.com.paypal=DEBUG
```

**Check payment status in database:**

```sql
-- H2 Console: http://localhost:8080/h2-console
SELECT * FROM payments ORDER BY created_at DESC LIMIT 10;
SELECT * FROM receipts ORDER BY created_at DESC LIMIT 10;
```

**Test PayPal connection:**

```bash
# Check health endpoint
curl http://localhost:8080/api/payments/health
```

---

## Security Best Practices

1. ✅ **Never commit credentials** to source control
2. ✅ **Use environment variables** for sensitive data
3. ✅ **Validate all inputs** with @Valid annotations
4. ✅ **Use HTTPS** in production
5. ✅ **Implement rate limiting** for payment endpoints
6. ✅ **Log all transactions** for audit trail
7. ✅ **Use sandbox mode** for development/testing
8. ✅ **Switch to live mode** only for production

---

## Production Deployment

When deploying to production:

1. **Switch to live mode:**
```properties
paypal.mode=live
```

2. **Use production credentials:**
   - Create production app in PayPal dashboard
   - Update PAYPAL_CLIENT_ID and PAYPAL_CLIENT_SECRET

3. **Update URLs:**
   - Use production frontend URLs for return/cancel
   - Ensure HTTPS is enabled

4. **Enable additional security:**
   - Implement webhook verification
   - Add fraud detection
   - Enable 2FA for admin accounts

---

## Additional Resources

- [PayPal Developer Documentation](https://developer.paypal.com/docs/api/overview/)
- [PayPal REST API Reference](https://developer.paypal.com/api/rest/)
- [PayPal Sandbox Testing Guide](https://developer.paypal.com/tools/sandbox/)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)

---

## Support

For issues or questions:
- Check [Troubleshooting](#troubleshooting) section
- Review backend logs: `backend/logs/`
- Contact development team: dev@mediway.com

---

**Last Updated**: October 15, 2025  
**Version**: 1.0.0  
**Author**: MediWay Development Team
