# MediWay Authentication Integration Test

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "MediWay Authentication Integration Test" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080/api"

# Test 1: Health Check
Write-Host "[1/4] Testing Backend Health..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "$baseUrl/auth/health" -Method Get
    Write-Host "   Success: Backend is running - $health" -ForegroundColor Green
} catch {
    Write-Host "   Error: Backend health check failed" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Test 2: Register a test patient
Write-Host "[2/4] Registering test patient..." -ForegroundColor Yellow
$patient = @{
    fullName = "Test Patient"
    email = "test.patient@mediway.com"
    password = "patient123"
    phone = "1234567890"
    role = "PATIENT"
} | ConvertTo-Json

try {
    $patientResp = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $patient -ContentType "application/json"
    Write-Host "   Success: Patient registered" -ForegroundColor Green
    Write-Host "   User ID: $($patientResp.userId)" -ForegroundColor Gray
    Write-Host "   Name: $($patientResp.fullName)" -ForegroundColor Gray
    Write-Host "   Role: $($patientResp.role)" -ForegroundColor Gray
} catch {
    Write-Host "   Note: Patient may already exist" -ForegroundColor Yellow
}
Write-Host ""

# Test 3: Register a test doctor
Write-Host "[3/4] Registering test doctor..." -ForegroundColor Yellow
$doctor = @{
    fullName = "Test Doctor"
    email = "test.doctor@mediway.com"
    password = "doctor123"
    phone = "9876543210"
    role = "DOCTOR"
} | ConvertTo-Json

try {
    $doctorResp = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $doctor -ContentType "application/json"
    Write-Host "   Success: Doctor registered" -ForegroundColor Green
    Write-Host "   User ID: $($doctorResp.userId)" -ForegroundColor Gray
    Write-Host "   Name: $($doctorResp.fullName)" -ForegroundColor Gray
    Write-Host "   Role: $($doctorResp.role)" -ForegroundColor Gray
} catch {
    Write-Host "   Note: Doctor may already exist" -ForegroundColor Yellow
}
Write-Host ""

# Test 4: Login as patient
Write-Host "[4/4] Testing login..." -ForegroundColor Yellow
$login = @{
    email = "test.patient@mediway.com"
    password = "patient123"
} | ConvertTo-Json

try {
    $loginResp = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $login -ContentType "application/json"
    Write-Host "   Success: Login successful" -ForegroundColor Green
    Write-Host "   Logged in as: $($loginResp.fullName)" -ForegroundColor Gray
    Write-Host "   Role: $($loginResp.role)" -ForegroundColor Gray
    Write-Host "   Token Type: $($loginResp.tokenType)" -ForegroundColor Gray
    Write-Host "   Token: $($loginResp.token.Substring(0, 30))..." -ForegroundColor Gray
} catch {
    Write-Host "   Error: Login failed" -ForegroundColor Red
}
Write-Host ""

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Integration Test Complete!" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Frontend URL: http://localhost:5174" -ForegroundColor Green
Write-Host "Backend URL:  http://localhost:8080" -ForegroundColor Green
Write-Host ""
Write-Host "Test Credentials:" -ForegroundColor Yellow
Write-Host "  Patient: test.patient@mediway.com / patient123" -ForegroundColor Gray
Write-Host "  Doctor:  test.doctor@mediway.com / doctor123" -ForegroundColor Gray
Write-Host ""
