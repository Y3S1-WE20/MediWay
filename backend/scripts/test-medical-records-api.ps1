# Test Medical Records API
Write-Host "Testing Medical Records API..." -ForegroundColor Green

# Base URL
$baseUrl = "http://localhost:8080/api"

# Test 1: Health Check
Write-Host "`n1. Testing Health Check..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/health" -Method GET
    Write-Host "✅ Health Check: $response" -ForegroundColor Green
} catch {
    Write-Host "❌ Health Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Register a test user
Write-Host "`n2. Registering Test User..." -ForegroundColor Yellow
$registerData = @{
    fullName = "Test Doctor"
    email = "test.doctor@example.com"
    password = "password123"
    phone = "+1234567890"
    role = "DOCTOR"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method POST -Body $registerData -ContentType "application/json"
    Write-Host "✅ User Registered: $($response.token)" -ForegroundColor Green
    $token = $response.token
} catch {
    Write-Host "❌ Registration Failed: $($_.Exception.Message)" -ForegroundColor Red
    # Try to login instead
    Write-Host "Trying to login with existing user..." -ForegroundColor Yellow
    $loginData = @{
        email = "test.doctor@example.com"
        password = "password123"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginData -ContentType "application/json"
        Write-Host "✅ Login Successful: $($response.token)" -ForegroundColor Green
        $token = $response.token
    } catch {
        Write-Host "❌ Login Failed: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
}

# Test 3: Get Medical Records (should be empty initially)
Write-Host "`n3. Testing Get Medical Records..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/medical-records" -Method GET -Headers $headers
    Write-Host "✅ Medical Records Retrieved: $($response.Count) records" -ForegroundColor Green
    if ($response.Count -gt 0) {
        $response | ForEach-Object { Write-Host "  - $($_.diagnosis) for $($_.patientName)" -ForegroundColor Cyan }
    }
} catch {
    Write-Host "❌ Get Medical Records Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Create a Medical Record
Write-Host "`n4. Testing Create Medical Record..." -ForegroundColor Yellow
$medicalRecordData = @{
    patientId = "123e4567-e89b-12d3-a456-426614174001"
    doctorId = "123e4567-e89b-12d3-a456-426614174002"
    diagnosis = "Test Diagnosis"
    medications = "Test Medication"
    notes = "This is a test medical record created via API"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/medical-records" -Method POST -Body $medicalRecordData -Headers $headers
    Write-Host "✅ Medical Record Created: $($response.recordId)" -ForegroundColor Green
    $createdRecordId = $response.recordId
} catch {
    Write-Host "❌ Create Medical Record Failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "This might be expected if the patient/doctor IDs don't exist" -ForegroundColor Yellow
}

# Test 5: Get Medical Records Again
Write-Host "`n5. Testing Get Medical Records After Creation..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/medical-records" -Method GET -Headers $headers
    Write-Host "✅ Medical Records Retrieved: $($response.Count) records" -ForegroundColor Green
    if ($response.Count -gt 0) {
        $response | ForEach-Object { Write-Host "  - $($_.diagnosis) for $($_.patientName)" -ForegroundColor Cyan }
    }
} catch {
    Write-Host "❌ Get Medical Records Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 API Testing Complete!" -ForegroundColor Green
Write-Host "If you see any errors above, they might be expected if sample data hasn't been inserted yet." -ForegroundColor Yellow
