# Simple Authentication API Test - MediWay Backend
# Make sure the backend is running before executing

Write-Host "===== MediWay Auth API Simple Test =====" -ForegroundColor Cyan
Write-Host ""

# Test 1: Health Check
Write-Host "1. Health Check:" -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/health" -Method Get
    Write-Host "   Success: $health" -ForegroundColor Green
} catch {
    Write-Host "   Failed: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 2: Register a PATIENT
Write-Host "2. Register PATIENT (john.doe@test.com):" -ForegroundColor Yellow
$patient = @{
    fullName = "John Doe"
    email = "john.doe@test.com"
    password = "password123"
    phone = "1234567890"
    role = "PATIENT"
} | ConvertTo-Json

try {
    $patientResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -Body $patient -ContentType "application/json"
    Write-Host "   Success! User ID: $($patientResp.userId)" -ForegroundColor Green
    Write-Host "   Email: $($patientResp.email), Role: $($patientResp.role)" -ForegroundColor Gray
    Write-Host "   Token: $($patientResp.token.Substring(0, 30))..." -ForegroundColor Gray
} catch {
    Write-Host "   Failed: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 3: Register a DOCTOR
Write-Host "3. Register DOCTOR (dr.smith@test.com):" -ForegroundColor Yellow
$doctor = @{
    fullName = "Dr. Sarah Smith"
    email = "dr.smith@test.com"
    password = "doctor123"
    phone = "9876543210"
    role = "DOCTOR"
} | ConvertTo-Json

try {
    $doctorResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -Body $doctor -ContentType "application/json"
    Write-Host "   Success! User ID: $($doctorResp.userId)" -ForegroundColor Green
    Write-Host "   Email: $($doctorResp.email), Role: $($doctorResp.role)" -ForegroundColor Gray
} catch {
    Write-Host "   Failed: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 4: Login
Write-Host "4. Login as john.doe@test.com:" -ForegroundColor Yellow
$login = @{
    email = "john.doe@test.com"
    password = "password123"
} | ConvertTo-Json

try {
    $loginResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $login -ContentType "application/json"
    Write-Host "   Success! Logged in as: $($loginResp.fullName)" -ForegroundColor Green
    Write-Host "   Role: $($loginResp.role), Token Type: $($loginResp.tokenType)" -ForegroundColor Gray
} catch {
    Write-Host "   Failed: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "===== Test Complete =====" -ForegroundColor Cyan
