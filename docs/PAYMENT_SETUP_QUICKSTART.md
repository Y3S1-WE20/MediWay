# PayPal Payment Setup - Quick Start Guide

## Prerequisites

✅ PayPal Developer account  
✅ Backend running on http://localhost:8080  
✅ Test user account created (patient)  

## Step 1: Get PayPal Sandbox Credentials

1. Visit https://developer.paypal.com/dashboard/
2. Login or create a PayPal Developer account
3. Navigate to **Apps & Credentials** → **Sandbox**
4. Create a new app or use the default app
5. Copy your credentials:
   - **Client ID**: AxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxWQ (example)
   - **Secret**: ExxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxYZ (example)

## Step 2: Create Sandbox Test Accounts

1. Go to **Sandbox** → **Accounts**
2. Create **Personal Account** (buyer):
   - Email: buyer-test@personal.example.com
   - Password: (auto-generated, click "Manage Accounts" to view)
   - This is your patient/payer account
3. Create **Business Account** (merchant):
   - Email: merchant-test@business.example.com
   - This receives payments (MediWay clinic account)

## Step 3: Configure Backend

### Option A: Environment Variables (Recommended)

**PowerShell:**
```powershell
# Set for current session
$env:PAYPAL_CLIENT_ID = "YOUR_CLIENT_ID_HERE"
$env:PAYPAL_CLIENT_SECRET = "YOUR_CLIENT_SECRET_HERE"

# Or set permanently (requires restart)
setx PAYPAL_CLIENT_ID "YOUR_CLIENT_ID_HERE"
setx PAYPAL_CLIENT_SECRET "YOUR_CLIENT_SECRET_HERE"
```

### Option B: Configuration File (Development Only)

Edit `backend/src/main/resources/application.properties`:

```properties
paypal.mode=sandbox
paypal.client.id=YOUR_CLIENT_ID_HERE
paypal.client.secret=YOUR_CLIENT_SECRET_HERE
```

⚠️ **Never commit credentials to Git!**

## Step 4: Start Backend

```powershell
cd F:\MediWay\backend

# Clean build
.\mvnw.cmd clean package -DskipTests

# Run backend
.\mvnw.cmd spring-boot:run
```

Backend should start on: http://localhost:8080

## Step 5: Test Payment Integration

Run the automated test script:

```powershell
cd F:\MediWay\backend
.\test-payment-integration.ps1
```

### Manual Testing

1. **Health check:**
```powershell
curl http://localhost:8080/api/payments/health
```

2. **Login:**
```powershell
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body '{"email":"test.patient@mediway.com","password":"patient123"}'

$token = $loginResponse.token
```

3. **Create payment:**
```powershell
$payment = @{
    amount = 50.00
    currency = "USD"
    description = "Test payment"
    returnUrl = "http://localhost:5174/payment-success"
    cancelUrl = "http://localhost:5174/payment-cancel"
    paymentMethod = "PAYPAL"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/payments/create" `
    -Method POST `
    -ContentType "application/json" `
    -Headers @{Authorization="Bearer $token"} `
    -Body $payment

# Open approval URL
Start-Process $response.approvalUrl
```

4. **Approve payment in browser:**
   - Login with your sandbox personal account
   - Click "Pay Now"
   - Note the `paymentId` and `PayerID` from redirect URL

5. **Execute payment:**
```powershell
$executeResponse = Invoke-RestMethod `
    -Uri "http://localhost:8080/api/payments/execute?paymentId=PAYID-XXX&PayerID=XXX" `
    -Method POST `
    -Headers @{Authorization="Bearer $token"}

Write-Host "Payment Status: $($executeResponse.status)"
```

6. **Get receipt:**
```powershell
$receipts = Invoke-RestMethod `
    -Uri "http://localhost:8080/api/payments/receipts/my-receipts" `
    -Headers @{Authorization="Bearer $token"}

$receipts | Format-Table receiptNumber, amount, paymentDate
```

## Troubleshooting

### Error: "Authentication failed"

❌ PayPal credentials are invalid

✅ **Solution:**
- Verify credentials in PayPal Dashboard
- Check environment variables: `echo $env:PAYPAL_CLIENT_ID`
- Restart backend after setting env vars

### Error: "Port 8080 already in use"

❌ Another process is using port 8080

✅ **Solution:**
```powershell
cd F:\MediWay\backend
.\kill-port-8080.ps1
```

### Payment stays in CREATED status

❌ Payment not executed after approval

✅ **Solution:**
- Ensure you called `/payments/execute` endpoint
- Check `paymentId` and `PayerID` are correct
- Verify PayPal approval was successful

### No receipt generated

❌ Payment completed but receipt missing

✅ **Solution:**
- Check payment status is COMPLETED
- View backend logs for errors
- Query database: `SELECT * FROM receipts;`

## Next Steps

1. ✅ Test payment flow end-to-end
2. ✅ Integrate with frontend (see PAYMENT_INTEGRATION.md)
3. ✅ Create appointment payment workflow
4. ✅ Add receipt PDF generation (optional)
5. ✅ Configure production credentials (when ready)

## Documentation

- **Full Guide**: `F:\MediWay\docs\PAYMENT_INTEGRATION.md`
- **API Reference**: See PaymentController endpoints
- **PayPal Docs**: https://developer.paypal.com/docs/

## Support

- Backend logs: `backend/logs/`
- Database console: http://localhost:8080/h2-console
- Health check: http://localhost:8080/api/payments/health

---

**Quick Links:**
- PayPal Dashboard: https://developer.paypal.com/dashboard/
- Sandbox Accounts: https://developer.paypal.com/dashboard/accounts
- API Reference: https://developer.paypal.com/api/rest/
