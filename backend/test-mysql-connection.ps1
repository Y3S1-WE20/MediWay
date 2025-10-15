# Test Backend-Database Connection
# This script tests if the backend is properly connected to MySQL

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Backend-Database Connection Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Backend Health Check
Write-Host "[Test 1/5] Backend Health Check" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/health" -Method GET -ErrorAction Stop
    Write-Host "✓ Backend is running" -ForegroundColor Green
    Write-Host "  Response: $response" -ForegroundColor Gray
} catch {
    Write-Host "✗ Backend is NOT running" -ForegroundColor Red
    Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please start the backend first:" -ForegroundColor Yellow
    Write-Host "  cd F:\MediWay\backend" -ForegroundColor White
    Write-Host "  .\start-with-mysql-fixed.ps1" -ForegroundColor White
    exit 1
}

# Test 2: Register a test user
Write-Host ""
Write-Host "[Test 2/5] Register Test User" -ForegroundColor Yellow
$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$testEmail = "test$timestamp@mediway.com"

$registerBody = @{
    fullName = "Test User $timestamp"
    email = $testEmail
    password = "Test@123"
    phone = "1234567890"
    role = "PATIENT"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $registerBody -ContentType "application/json" -ErrorAction Stop
    Write-Host "✓ User registered successfully" -ForegroundColor Green
    Write-Host "  Email: $testEmail" -ForegroundColor Gray
    Write-Host "  User ID: $($registerResponse.userId)" -ForegroundColor Gray
    Write-Host "  Token: $($registerResponse.token.Substring(0, 20))..." -ForegroundColor Gray
    $testUserId = $registerResponse.userId
    $testToken = $registerResponse.token
} catch {
    Write-Host "✗ Registration failed" -ForegroundColor Red
    Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "  Details: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
    exit 1
}

# Test 3: Verify user in MySQL
Write-Host ""
Write-Host "[Test 3/5] Verify User in MySQL" -ForegroundColor Yellow
Write-Host "Please check MySQL Workbench:" -ForegroundColor Cyan
Write-Host "  1. Open MySQL Workbench" -ForegroundColor White
Write-Host "  2. Connect to localhost" -ForegroundColor White
Write-Host "  3. Run this query:" -ForegroundColor White
Write-Host ""
Write-Host "     SELECT * FROM mediwaydb.users WHERE email = '$testEmail';" -ForegroundColor Yellow
Write-Host ""
Write-Host "  4. You should see the newly registered user" -ForegroundColor White
Write-Host ""
$userVisible = Read-Host "Can you see the user in MySQL? (y/n)"
if ($userVisible -eq "y") {
    Write-Host "✓ User data persisted to MySQL" -ForegroundColor Green
} else {
    Write-Host "✗ User NOT visible in MySQL" -ForegroundColor Red
    Write-Host ""
    Write-Host "This means the backend is still using H2 in-memory database." -ForegroundColor Yellow
    Write-Host "Please check:" -ForegroundColor Yellow
    Write-Host "  1. Backend logs for 'HikariPool-1 - Starting...'" -ForegroundColor White
    Write-Host "  2. application.properties has MySQL configuration" -ForegroundColor White
    Write-Host "  3. MySQL server is running" -ForegroundColor White
    exit 1
}

# Test 4: Login with registered user
Write-Host ""
Write-Host "[Test 4/5] Login Test" -ForegroundColor Yellow

$loginBody = @{
    email = $testEmail
    password = "Test@123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json" -ErrorAction Stop
    Write-Host "✓ Login successful" -ForegroundColor Green
    Write-Host "  Email: $($loginResponse.email)" -ForegroundColor Gray
    Write-Host "  Role: $($loginResponse.role)" -ForegroundColor Gray
    Write-Host "  Token: $($loginResponse.token.Substring(0, 20))..." -ForegroundColor Gray
} catch {
    Write-Host "✗ Login failed" -ForegroundColor Red
    Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "  Details: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
    exit 1
}

# Test 5: Restart backend and verify persistence
Write-Host ""
Write-Host "[Test 5/5] Persistence Test" -ForegroundColor Yellow
Write-Host "This test verifies data survives backend restart." -ForegroundColor Gray
Write-Host ""
Write-Host "Steps:" -ForegroundColor Cyan
Write-Host "  1. Stop the backend (Ctrl+C in backend window)" -ForegroundColor White
Write-Host "  2. Wait 5 seconds" -ForegroundColor White
Write-Host "  3. Restart backend with: .\start-with-mysql-fixed.ps1" -ForegroundColor White
Write-Host "  4. Try to login again with:" -ForegroundColor White
Write-Host "     Email: $testEmail" -ForegroundColor Yellow
Write-Host "     Password: Test@123" -ForegroundColor Yellow
Write-Host ""
Write-Host "If login works after restart, data is persisting to MySQL ✓" -ForegroundColor Green
Write-Host "If login fails, backend is using H2 in-memory ✗" -ForegroundColor Red
Write-Host ""

# Summary
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Test Summary" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Test User Created:" -ForegroundColor Yellow
Write-Host "  Email: $testEmail" -ForegroundColor White
Write-Host "  Password: Test@123" -ForegroundColor White
Write-Host "  User ID: $testUserId" -ForegroundColor White
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Open frontend: http://localhost:5174" -ForegroundColor White
Write-Host "  2. Login with test credentials above" -ForegroundColor White
Write-Host "  3. Navigate to Payments page" -ForegroundColor White
Write-Host "  4. Create a test payment" -ForegroundColor White
Write-Host ""
Write-Host "MySQL Queries to Verify Data:" -ForegroundColor Cyan
Write-Host "  -- View all users" -ForegroundColor White
Write-Host "  SELECT * FROM mediwaydb.users;" -ForegroundColor Yellow
Write-Host ""
Write-Host "  -- View test user" -ForegroundColor White
Write-Host "  SELECT * FROM mediwaydb.users WHERE email = '$testEmail';" -ForegroundColor Yellow
Write-Host ""
Write-Host "  -- Count users" -ForegroundColor White
Write-Host "  SELECT COUNT(*) FROM mediwaydb.users;" -ForegroundColor Yellow
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
