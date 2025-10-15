# 🚀 MediWay Payment Integration - Quick Reference

## 📋 Quick Commands

### Start Backend
```powershell
cd F:\MediWay\backend
.\mvnw.cmd spring-boot:run
```

### Run Tests
```powershell
.\test-payment-integration.ps1
```

### Set PayPal Credentials
```powershell
$env:PAYPAL_CLIENT_ID = "YOUR_CLIENT_ID"
$env:PAYPAL_CLIENT_SECRET = "YOUR_SECRET"
```

---

## 🔗 API Endpoints Cheat Sheet

**Base URL**: `http://localhost:8080/api/payments`

| Endpoint | Method | Auth | Purpose |
|----------|--------|------|---------|
| `/health` | GET | ❌ | Health check |
| `/create` | POST | ✅ | Create payment |
| `/execute` | POST | ✅ | Complete payment |
| `/cancel` | POST | ✅ | Cancel payment |
| `/{id}` | GET | ✅ | Get payment |
| `/my-payments` | GET | ✅ | List user payments |
| `/receipts/my-receipts` | GET | ✅ | List user receipts |

---

## 📝 Request Examples

### Create Payment
```json
POST /api/payments/create
Authorization: Bearer <token>

{
  "amount": 50.00,
  "currency": "USD",
  "description": "Consultation fee",
  "returnUrl": "http://localhost:5174/success",
  "cancelUrl": "http://localhost:5174/cancel",
  "paymentMethod": "PAYPAL"
}
```

### Execute Payment
```http
POST /api/payments/execute?paymentId=PAYID-XXX&PayerID=XXX
Authorization: Bearer <token>
```

---

## 💾 Database Tables

### payments
- `payment_id` (UUID, PK)
- `user_id` (UUID, FK)
- `amount` (DECIMAL)
- `status` (ENUM: CREATED, APPROVED, COMPLETED, FAILED, CANCELLED)
- `paypal_payment_id` (VARCHAR, UNIQUE)
- `approval_url` (VARCHAR)

### receipts
- `receipt_id` (UUID, PK)
- `receipt_number` (VARCHAR, UNIQUE)
- `payment_id` (UUID, FK)
- `transaction_id` (VARCHAR)
- `payer_email`, `payer_name`

---

## 🎯 Payment Flow

```
1. Create Payment → Get approval_url
2. Redirect user to PayPal
3. User approves payment
4. PayPal redirects to returnUrl with paymentId & PayerID
5. Execute Payment → Payment COMPLETED
6. Receipt auto-generated
```

---

## 🔐 Roles & Permissions

- **PATIENT**: Create, view own payments/receipts
- **DOCTOR**: View all payments/receipts
- **ADMIN**: Full access to all operations

---

## 📚 Documentation Files

1. **Full Guide**: `docs/PAYMENT_INTEGRATION.md` (500+ lines)
2. **Quick Setup**: `docs/PAYMENT_SETUP_QUICKSTART.md`
3. **Summary**: `docs/PAYMENT_IMPLEMENTATION_SUMMARY.md`
4. **Test Script**: `backend/test-payment-integration.ps1`

---

## 🧪 Testing URLs

- Health: http://localhost:8080/api/payments/health
- H2 Console: http://localhost:8080/h2-console
- PayPal Dashboard: https://developer.paypal.com/dashboard/

---

## ⚡ PowerShell Quick Test

```powershell
# Login
$login = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method POST -ContentType "application/json" `
    -Body '{"email":"test.patient@mediway.com","password":"patient123"}'

# Create Payment
$payment = @{amount=50;currency="USD";description="Test";
returnUrl="http://localhost:5174/success";cancelUrl="http://localhost:5174/cancel";
paymentMethod="PAYPAL"} | ConvertTo-Json

$result = Invoke-RestMethod -Uri "http://localhost:8080/api/payments/create" `
    -Method POST -ContentType "application/json" `
    -Headers @{Authorization="Bearer $($login.token)"} -Body $payment

# Open PayPal
Start-Process $result.approvalUrl
```

---

## 🐛 Troubleshooting Quick Fixes

| Issue | Fix |
|-------|-----|
| Port 8080 in use | `.\kill-port-8080.ps1` |
| Auth failed | Check `$env:PAYPAL_CLIENT_ID` |
| Payment not found | Check database: `SELECT * FROM payments;` |
| No receipt | Verify payment status is COMPLETED |

---

## 📞 Key Files

- **Controller**: `PaymentController.java`
- **Service**: `PayPalService.java`, `ReceiptService.java`
- **Entities**: `Payment.java`, `Receipt.java`
- **Config**: `PayPalConfig.java`, `application.properties`

---

## ✅ Status

- ✅ Build: SUCCESS (27 files)
- ✅ Tests: Ready
- ✅ Docs: Complete
- ✅ Code: Clean (0 errors)

---

**Ready to test!** 🎉 Run: `.\test-payment-integration.ps1`
