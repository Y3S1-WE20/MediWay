# Test Authentication API Endpoints
# Make sure the backend is running before executing this script

$baseUrl = "http://localhost:8080/api"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "MediWay Authentication API Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Health Check
Write-Host "[1] Testing Health Check..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "$baseUrl/auth/health" -Method Get
    Write-Host "✓ Health check passed" -ForegroundColor Green
    Write-Host "Response: $health" -ForegroundColor Gray
} catch {
    Write-Host "✗ Health check failed" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}
Write-Host ""

# Test 2: Register a new PATIENT
Write-Host "[2] Testing User Registration (PATIENT)..." -ForegroundColor Yellow
$registerBody = @{
    fullName = "John Doe"
    email = "john.doe@example.com"
    password = "password123"
    phone = "1234567890"
    role = "PATIENT"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $registerBody -ContentType "application/json"
    Write-Host "✓ Registration successful" -ForegroundColor Green
    Write-Host "User ID: $($registerResponse.userId)" -ForegroundColor Gray
    Write-Host "Full Name: $($registerResponse.fullName)" -ForegroundColor Gray
    Write-Host "Email: $($registerResponse.email)" -ForegroundColor Gray
    Write-Host "Role: $($registerResponse.role)" -ForegroundColor Gray
    Write-Host "Token: $($registerResponse.token.Substring(0, 50))..." -ForegroundColor Gray
} catch {
    Write-Host "✗ Registration failed" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host ($_.ErrorDetails.Message | ConvertFrom-Json | ConvertTo-Json -Depth 10) -ForegroundColor Red
    } else {
        Write-Host $_.Exception.Message -ForegroundColor Red
    }
}
Write-Host ""

# Test 3: Register a DOCTOR
Write-Host "[3] Testing User Registration (DOCTOR)..." -ForegroundColor Yellow
$doctorBody = @{
    fullName = "Dr. Sarah Smith"
    email = "dr.sarah@example.com"
    password = "password123"
    phone = "9876543210"
    role = "DOCTOR"
} | ConvertTo-Json

try {
    $doctorResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $doctorBody -ContentType "application/json"
    Write-Host "✓ Doctor registration successful" -ForegroundColor Green
    Write-Host "User ID: $($doctorResponse.userId)" -ForegroundColor Gray
    Write-Host "Full Name: $($doctorResponse.fullName)" -ForegroundColor Gray
    Write-Host "Role: $($doctorResponse.role)" -ForegroundColor Gray
} catch {
    Write-Host "✗ Doctor registration failed" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host ($_.ErrorDetails.Message | ConvertFrom-Json | ConvertTo-Json -Depth 10) -ForegroundColor Red
    } else {
        Write-Host $_.Exception.Message -ForegroundColor Red
    }
}
Write-Host ""

# Test 4: Login with the registered user
Write-Host "[4] Testing User Login..." -ForegroundColor Yellow
$loginBody = @{
    email = "john.doe@example.com"
    password = "password123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    Write-Host "✓ Login successful" -ForegroundColor Green
    Write-Host "User ID: $($loginResponse.userId)" -ForegroundColor Gray
    Write-Host "Email: $($loginResponse.email)" -ForegroundColor Gray
    Write-Host "Role: $($loginResponse.role)" -ForegroundColor Gray
    Write-Host "Token Type: $($loginResponse.tokenType)" -ForegroundColor Gray
    Write-Host "Token: $($loginResponse.token.Substring(0, 50))..." -ForegroundColor Gray
    $token = $loginResponse.token
} catch {
    Write-Host "✗ Login failed" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host ($_.ErrorDetails.Message | ConvertFrom-Json | ConvertTo-Json -Depth 10) -ForegroundColor Red
    } else {
        Write-Host $_.Exception.Message -ForegroundColor Red
    }
}
Write-Host ""

# Test 5: Test duplicate email registration (expected to fail)
Write-Host "[5] Testing Duplicate Email Registration (expected to fail)..." -ForegroundColor Yellow
try {
    $duplicateResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $registerBody -ContentType "application/json"
    Write-Host "✗ Duplicate registration expected to fail but succeeded" -ForegroundColor Red
} catch {
    Write-Host "✓ Duplicate registration correctly rejected" -ForegroundColor Green
    if ($_.ErrorDetails.Message) {
        $error = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "Error: $($error.message)" -ForegroundColor Gray
    }
}
Write-Host ""

# Test 6: Test invalid login credentials (expected to fail)
Write-Host "[6] Testing Invalid Login Credentials (expected to fail)..." -ForegroundColor Yellow
$invalidLoginBody = @{
    email = "john.doe@example.com"
    password = "wrongpassword"
} | ConvertTo-Json

try {
    $invalidLoginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $invalidLoginBody -ContentType "application/json"
    Write-Host "✗ Invalid login expected to fail but succeeded" -ForegroundColor Red
} catch {
    Write-Host "✓ Invalid login correctly rejected" -ForegroundColor Green
    if ($_.ErrorDetails.Message) {
        $error = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "Error: $($error.message)" -ForegroundColor Gray
    }
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test Suite Complete" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
