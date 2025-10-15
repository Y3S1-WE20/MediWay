# MediWay Integration Testing Guide

## Overview
This guide will help you test the complete integration between frontend and backend, including:
1. MySQL database persistence for user registration/login
2. PayPal payment integration
3. Receipt generation

## Prerequisites
- MySQL server running with `mediwaydb` database
- PayPal sandbox account credentials set as environment variables
- Backend and frontend dependencies installed

## Step 1: Fix MySQL Connection Issue

### Problem Identified
Your backend is currently using H2 in-memory database by default, which is why user registration/login data isn't persisting to MySQL.

### Solution
Use the MySQL profile when starting the backend.

### Start Backend with MySQL

**Option A: Using the startup script (Recommended)**
```powershell
cd F:\MediWay\backend
.\start-with-mysql.ps1
```

**Option B: Manual setup**
```powershell
cd F:\MediWay\backend
$env:SPRING_PROFILES_ACTIVE = "mysql"
$env:MYSQL_USER = "mediway_user"
$env:MYSQL_PASSWORD = "admin"
.\mvnw.cmd spring-boot:run
```

### Verify MySQL Connection
1. Wait for backend to start (check for "Started MediWayBackendApplication" message)
2. Check backend logs for MySQL connection:
   ```
   HikariPool-1 - Starting...
   HikariPool-1 - Start completed
   ```
3. Open MySQL Workbench and refresh the `mediwaydb` database
4. Check that tables are created (users, roles, payments, receipts, etc.)

## Step 2: Test User Registration & Login

### Register a New User
1. Start the frontend:
   ```powershell
   cd F:\MediWay\frontend
   npm run dev
   ```

2. Open browser to http://localhost:5174

3. Click "Register" and create a new account:
   - Full Name: Test Patient
   - Email: test@mediway.com
   - Password: Test@123
   - Confirm Password: Test@123
   - Role: PATIENT

4. Submit the form

### Verify Data Persistence
1. Open MySQL Workbench
2. Run query:
   ```sql
   SELECT * FROM mediwaydb.users ORDER BY created_at DESC LIMIT 5;
   ```
3. You should see your newly registered user with:
   - Email: test@mediway.com
   - Encrypted password (bcrypt hash)
   - Role: PATIENT
   - Created timestamp

### Test Login
1. Click "Login"
2. Enter credentials:
   - Email: test@mediway.com
   - Password: Test@123
3. Click "Login"
4. You should be redirected to the home page, logged in

### Verify Token Authentication
1. Open browser DevTools (F12)
2. Go to Application > Local Storage > http://localhost:5174
3. Check for `token` key with JWT value
4. Go to Network tab
5. Make any API request (e.g., navigate to Payments page)
6. Check request headers for `Authorization: Bearer <token>`

## Step 3: Test PayPal Payment Integration

### Configure PayPal Sandbox Credentials

1. Set environment variables (if not already set):
   ```powershell
   $env:PAYPAL_CLIENT_ID = "your_sandbox_client_id"
   $env:PAYPAL_CLIENT_SECRET = "your_sandbox_client_secret"
   ```

2. Restart backend with MySQL profile:
   ```powershell
   cd F:\MediWay\backend
   .\start-with-mysql.ps1
   ```

### Create a Payment

1. Login to frontend as a patient
2. Navigate to "Payments" page
3. Click "Create New Payment" button
4. Fill in payment form:
   - Amount: 50.00
   - Description: Medical Consultation Fee
   - Currency: USD
   - Payment Method: PAYPAL
5. Click "Continue to PayPal"

### Complete Payment on PayPal

1. You will be redirected to PayPal sandbox
2. Login with sandbox buyer account:
   - Email: (your PayPal sandbox buyer email)
   - Password: (your PayPal sandbox password)
3. Review payment details
4. Click "Pay Now" or "Approve"

### Verify Payment Execution

1. After approval, you'll be redirected to `/payment-success`
2. The page will:
   - Extract `paymentId` and `PayerID` from URL
   - Call backend to execute payment
   - Fetch receipt details
   - Display success message with payment and receipt info

3. Check the success page displays:
   - ✅ Payment completed message
   - Payment ID
   - Amount paid
   - Receipt number (format: RCP-YYYYMMDD-XXXXXXXX)
   - "View Payments" and "Back to Home" buttons

### Verify in Database

```sql
-- Check payment record
SELECT * FROM mediwaydb.payments ORDER BY created_at DESC LIMIT 1;

-- Check receipt record
SELECT * FROM mediwaydb.receipts ORDER BY issued_at DESC LIMIT 1;

-- Join to see payment with receipt
SELECT 
    p.id AS payment_id,
    p.amount,
    p.currency,
    p.description,
    p.status,
    p.paypal_payment_id,
    r.receipt_number,
    r.issued_at
FROM mediwaydb.payments p
LEFT JOIN mediwaydb.receipts r ON r.payment_id = p.id
ORDER BY p.created_at DESC
LIMIT 5;
```

### Verify in Frontend

1. Navigate back to Payments page
2. Check payment history shows:
   - Your new payment with "Completed" badge
   - Correct amount (USD $50.00)
   - Payment date/time
   - PayPal Payment ID
3. Click "View Receipt" button (if available)
4. Verify receipt details displayed

## Step 4: Test Payment Cancellation

### Start a Payment and Cancel

1. Click "Create New Payment" again
2. Fill in payment form:
   - Amount: 25.00
   - Description: Lab Test Fee
3. Click "Continue to PayPal"
4. On PayPal approval page, click "Cancel" or "Return to Merchant"

### Verify Cancellation

1. You should be redirected to `/payment-cancel`
2. The page will:
   - Extract `paymentId` from session storage
   - Call backend to cancel payment
   - Clear session storage
   - Display cancellation message

3. Navigate back to Payments page
4. Check that cancelled payment shows status "Cancelled"

## Step 5: End-to-End Flow Test

### Complete Workflow

1. **Register** → MySQL stores user
2. **Login** → Backend issues JWT token
3. **Create Payment** → Backend creates PayPal payment
4. **Approve on PayPal** → PayPal redirects with tokens
5. **Execute Payment** → Backend finalizes payment
6. **Generate Receipt** → Backend auto-creates receipt
7. **View History** → Frontend displays all payments and receipts

### Test Checklist

- [ ] Backend starts with MySQL profile
- [ ] User registration persists to `mediwaydb.users` table
- [ ] User can login with registered credentials
- [ ] JWT token stored in localStorage
- [ ] Payments page loads without errors
- [ ] "Create Payment" modal opens
- [ ] PayPal redirect works correctly
- [ ] Payment approval on PayPal sandbox succeeds
- [ ] Success page executes payment and fetches receipt
- [ ] Payment status updates to "COMPLETED" in database
- [ ] Receipt generated with unique receipt number
- [ ] Payment appears in payment history with "Completed" badge
- [ ] Can view receipt details
- [ ] Payment cancellation works correctly
- [ ] Cancelled payment shows "Cancelled" status

## Troubleshooting

### Issue: User data not persisting to MySQL

**Symptom**: After registration, user not found in `mediwaydb.users` table

**Solution**:
1. Check backend is using MySQL profile:
   ```powershell
   # Look for this in backend startup logs:
   The following 1 profile is active: "mysql"
   ```

2. If not, restart backend with:
   ```powershell
   cd F:\MediWay\backend
   .\start-with-mysql.ps1
   ```

3. Verify MySQL connection in logs:
   ```
   HikariPool-1 - Start completed
   ```

### Issue: PayPal payment creation fails

**Symptom**: Error "Failed to create payment" or 401 Unauthorized

**Solution**:
1. Check environment variables are set:
   ```powershell
   echo $env:PAYPAL_CLIENT_ID
   echo $env:PAYPAL_CLIENT_SECRET
   ```

2. Verify credentials in backend logs (check for authentication errors)

3. Restart backend after setting env vars

### Issue: Payment execution fails after PayPal approval

**Symptom**: Success page shows error or payment status not updating

**Solution**:
1. Check browser console for errors (F12)
2. Check backend logs for execution errors
3. Verify `paymentId` and `PayerID` in URL parameters
4. Check session storage has `pendingPaymentId`

### Issue: Frontend can't connect to backend

**Symptom**: Network errors, CORS errors, or "Failed to load payments"

**Solution**:
1. Verify backend is running on http://localhost:8080
2. Check `api.js` baseURL is correct: `http://localhost:8080/api`
3. Check CORS configuration in SecurityConfig.java
4. Clear browser cache and localStorage

### Issue: Receipt not generated

**Symptom**: Payment completes but no receipt found

**Solution**:
1. Check backend logs for receipt generation errors
2. Verify ReceiptService is being called after payment execution
3. Check database for receipt record:
   ```sql
   SELECT * FROM mediwaydb.receipts WHERE payment_id = 'your_payment_id';
   ```

## API Testing with PowerShell

### Test Backend Health
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/health" -Method GET
```

### Test Payment Creation (with auth token)
```powershell
$token = "your_jwt_token_here"
$headers = @{ "Authorization" = "Bearer $token" }
$body = @{
    amount = 50.00
    currency = "USD"
    description = "Test Payment"
    paymentMethod = "PAYPAL"
    returnUrl = "http://localhost:5174/payment-success"
    cancelUrl = "http://localhost:5174/payment-cancel"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/payments/create" -Method POST -Headers $headers -Body $body -ContentType "application/json"
```

### Test Get My Payments
```powershell
$token = "your_jwt_token_here"
$headers = @{ "Authorization" = "Bearer $token" }
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/my-payments" -Method GET -Headers $headers
```

## Success Criteria

✅ **Database Integration**
- User registration data persists to MySQL `users` table
- User can login after server restart
- Multiple users can be registered

✅ **Payment Integration**
- Payment creation redirects to PayPal sandbox
- PayPal approval redirects back to success page
- Payment execution updates status to COMPLETED
- Payment stored in `payments` table with PayPal ID

✅ **Receipt Generation**
- Receipt auto-generated after successful payment
- Receipt has unique number (RCP-YYYYMMDD-XXXXXXXX format)
- Receipt stored in `receipts` table linked to payment

✅ **Frontend Integration**
- Payments page displays all user payments
- Status badges show correct payment status
- "Create Payment" flow works end-to-end
- Success/Cancel pages handle redirects properly

## Next Steps

After successful integration testing:

1. **Test with Multiple Users**
   - Register 3-5 different users
   - Create payments for each user
   - Verify each user only sees their own payments

2. **Test Edge Cases**
   - Very small amounts (0.01)
   - Large amounts (999.99)
   - Different currencies (EUR, GBP)
   - Rapid consecutive payments

3. **Performance Testing**
   - Create 10+ payments
   - Check payment history load time
   - Verify pagination (if implemented)

4. **Security Testing**
   - Try accessing payments without login
   - Try accessing another user's payment details
   - Test token expiration (wait 24 hours)

5. **Error Handling**
   - Test with invalid payment amounts
   - Test network failures
   - Test backend downtime scenarios

## Support

If you encounter issues not covered in this guide:

1. Check backend logs: `F:\MediWay\backend\logs` (if configured)
2. Check browser console for JavaScript errors
3. Review MySQL error logs
4. Check PayPal sandbox dashboard for transaction status
5. Verify all environment variables are correctly set

---

**Last Updated**: January 2025
**MediWay Version**: 1.0.0
**Backend**: Spring Boot 3.4.10
**Frontend**: React 18 + Vite
**Database**: MySQL 8.0.42
**Payment Gateway**: PayPal Sandbox
