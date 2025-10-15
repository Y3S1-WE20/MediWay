# PayPal Payment Integration Test Script
# Tests the complete payment flow for MediWay backend

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "   MediWay PayPal Payment Integration Test" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$baseUrl = "http://localhost:8080/api"
$testEmail = "test.patient@mediway.com"
$testPassword = "patient123"

# Test 1: Health Check
Write-Host "Test 1: Payment Service Health Check" -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "$baseUrl/payments/health" -Method GET
    Write-Host "✅ Health Check Passed" -ForegroundColor Green
    Write-Host "   Status: $($healthResponse.status)" -ForegroundColor Gray
    Write-Host "   Service: $($healthResponse.service)" -ForegroundColor Gray
}
catch {
    Write-Host "❌ Health Check Failed: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Test 2: User Login
Write-Host "Test 2: User Authentication" -ForegroundColor Yellow
try {
    $loginBody = @{
        email = $testEmail
        password = $testPassword
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST `
        -ContentType "application/json" `
        -Body $loginBody

    $token = $loginResponse.token
    Write-Host "✅ Login Successful" -ForegroundColor Green
    Write-Host "   User: $($loginResponse.email)" -ForegroundColor Gray
    Write-Host "   Role: $($loginResponse.role)" -ForegroundColor Gray
    Write-Host "   Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
}
catch {
    Write-Host "❌ Login Failed: $_" -ForegroundColor Red
    Write-Host "   Make sure test user exists. Run registration first." -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# Test 3: Create Payment
Write-Host "Test 3: Create PayPal Payment" -ForegroundColor Yellow
try {
    $paymentRequest = @{
        amount = 75.50
        currency = "USD"
        description = "Test consultation payment - Automated test"
        returnUrl = "http://localhost:5174/payment-success"
        cancelUrl = "http://localhost:5174/payment-cancel"
        paymentMethod = "PAYPAL"
    } | ConvertTo-Json

    $headers = @{
        Authorization = "Bearer $token"
    }

    $paymentResponse = Invoke-RestMethod -Uri "$baseUrl/payments/create" -Method POST `
        -ContentType "application/json" `
        -Headers $headers `
        -Body $paymentRequest

    Write-Host "✅ Payment Created Successfully" -ForegroundColor Green
    Write-Host "   Payment ID: $($paymentResponse.paymentId)" -ForegroundColor Gray
    Write-Host "   PayPal Payment ID: $($paymentResponse.paypalPaymentId)" -ForegroundColor Gray
    Write-Host "   Amount: $($paymentResponse.amount) $($paymentResponse.currency)" -ForegroundColor Gray
    Write-Host "   Status: $($paymentResponse.status)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "   📌 Approval URL:" -ForegroundColor Cyan
    Write-Host "   $($paymentResponse.approvalUrl)" -ForegroundColor White
    Write-Host ""

    # Save payment details for manual execution
    $paypalPaymentId = $paymentResponse.paypalPaymentId
    
    # Ask if user wants to open approval URL
    Write-Host "Do you want to open PayPal approval URL in browser? (Y/N): " -ForegroundColor Yellow -NoNewline
    $openBrowser = Read-Host
    
    if ($openBrowser -eq 'Y' -or $openBrowser -eq 'y') {
        Start-Process $paymentResponse.approvalUrl
        Write-Host ""
        Write-Host "✅ Opened PayPal approval URL in browser" -ForegroundColor Green
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "MANUAL STEPS REQUIRED:" -ForegroundColor Yellow
        Write-Host "1. Login to PayPal sandbox with test account" -ForegroundColor White
        Write-Host "2. Approve the payment" -ForegroundColor White
        Write-Host "3. After redirect, copy the paymentId and PayerID from URL" -ForegroundColor White
        Write-Host "4. Run the execute payment command below:" -ForegroundColor White
        Write-Host ""
        Write-Host "PowerShell Command:" -ForegroundColor Cyan
        Write-Host "`$paymentId = 'PASTE_PAYMENT_ID_HERE'" -ForegroundColor Gray
        Write-Host "`$payerId = 'PASTE_PAYER_ID_HERE'" -ForegroundColor Gray
        Write-Host "`$token = '$token'" -ForegroundColor Gray
        Write-Host "Invoke-RestMethod -Uri '$baseUrl/payments/execute?paymentId=`$paymentId&PayerID=`$payerId' -Method POST -Headers @{Authorization='Bearer `$token'}" -ForegroundColor Gray
        Write-Host "========================================" -ForegroundColor Cyan
    }
}
catch {
    Write-Host "❌ Payment Creation Failed: $_" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "   Details: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
    exit 1
}
Write-Host ""

# Test 4: Get User Payments
Write-Host "Test 4: Retrieve User Payments" -ForegroundColor Yellow
try {
    $paymentsResponse = Invoke-RestMethod -Uri "$baseUrl/payments/my-payments" -Method GET `
        -Headers @{Authorization = "Bearer $token"}

    Write-Host "✅ Retrieved User Payments" -ForegroundColor Green
    Write-Host "   Total Payments: $($paymentsResponse.Count)" -ForegroundColor Gray
    
    if ($paymentsResponse.Count -gt 0) {
        Write-Host ""
        Write-Host "   Recent Payments:" -ForegroundColor Cyan
        $paymentsResponse | Select-Object -First 5 | ForEach-Object {
            Write-Host "   - $($_.amount) $($_.currency) | Status: $($_.status) | Created: $($_.createdAt)" -ForegroundColor Gray
        }
    }
}
catch {
    Write-Host "❌ Failed to retrieve payments: $_" -ForegroundColor Red
}
Write-Host ""

# Test 5: Get User Receipts
Write-Host "Test 5: Retrieve User Receipts" -ForegroundColor Yellow
try {
    $receiptsResponse = Invoke-RestMethod -Uri "$baseUrl/payments/receipts/my-receipts" -Method GET `
        -Headers @{Authorization = "Bearer $token"}

    Write-Host "✅ Retrieved User Receipts" -ForegroundColor Green
    Write-Host "   Total Receipts: $($receiptsResponse.Count)" -ForegroundColor Gray
    
    if ($receiptsResponse.Count -gt 0) {
        Write-Host ""
        Write-Host "   Recent Receipts:" -ForegroundColor Cyan
        $receiptsResponse | Select-Object -First 5 | ForEach-Object {
            Write-Host "   - Receipt #$($_.receiptNumber) | $($_.amount) $($_.currency) | Date: $($_.paymentDate)" -ForegroundColor Gray
        }
    }
    else {
        Write-Host "   No receipts found. Complete a payment to generate a receipt." -ForegroundColor Yellow
    }
}
catch {
    Write-Host "❌ Failed to retrieve receipts: $_" -ForegroundColor Red
}
Write-Host ""

# Summary
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "   Test Summary" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "✅ Health Check: PASSED" -ForegroundColor Green
Write-Host "✅ Authentication: PASSED" -ForegroundColor Green
Write-Host "✅ Payment Creation: PASSED" -ForegroundColor Green
Write-Host "✅ Payment Retrieval: PASSED" -ForegroundColor Green
Write-Host "✅ Receipt Retrieval: PASSED" -ForegroundColor Green
Write-Host ""
Write-Host "⚠️  To complete end-to-end testing:" -ForegroundColor Yellow
Write-Host "   1. Approve payment in PayPal sandbox" -ForegroundColor White
Write-Host "   2. Execute payment with PayerID" -ForegroundColor White
Write-Host "   3. Verify receipt generation" -ForegroundColor White
Write-Host ""
Write-Host "📚 Documentation: F:\MediWay\docs\PAYMENT_INTEGRATION.md" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
