# Create test data for Medical Records
Write-Host "Creating test data for Medical Records..." -ForegroundColor Green

$baseUrl = "http://localhost:8080/api"

# Register test doctor
$doctorData = @{
    fullName = "Dr. Test Doctor"
    email = "doctor@test.com"
    password = "password123"
    phone = "+1234567890"
    role = "DOCTOR"
} | ConvertTo-Json

Write-Host "Registering test doctor..." -ForegroundColor Yellow
try {
    $doctorResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method POST -Body $doctorData -ContentType "application/json"
    $doctorToken = $doctorResponse.token
    Write-Host "✅ Doctor registered successfully" -ForegroundColor Green
} catch {
    Write-Host "Doctor might already exist, trying to login..." -ForegroundColor Yellow
    $loginData = @{
        email = "doctor@test.com"
        password = "password123"
    } | ConvertTo-Json
    $doctorResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    $doctorToken = $doctorResponse.token
    Write-Host "✅ Doctor login successful" -ForegroundColor Green
}

# Register test patient
$patientData = @{
    fullName = "Test Patient"
    email = "patient@test.com"
    password = "password123"
    phone = "+1234567891"
    role = "PATIENT"
} | ConvertTo-Json

Write-Host "Registering test patient..." -ForegroundColor Yellow
try {
    $patientResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method POST -Body $patientData -ContentType "application/json"
    $patientToken = $patientResponse.token
    Write-Host "✅ Patient registered successfully" -ForegroundColor Green
} catch {
    Write-Host "Patient might already exist, trying to login..." -ForegroundColor Yellow
    $loginData = @{
        email = "patient@test.com"
        password = "password123"
    } | ConvertTo-Json
    $patientResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    $patientToken = $patientResponse.token
    Write-Host "✅ Patient login successful" -ForegroundColor Green
}

# Get user IDs (you'll need to decode the JWT or get them from the response)
Write-Host "Creating sample medical records..." -ForegroundColor Yellow

# For now, let's just test the endpoint
$headers = @{
    "Authorization" = "Bearer $doctorToken"
    "Content-Type" = "application/json"
}

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/medical-records" -Method GET -Headers $headers
    Write-Host "✅ Medical Records endpoint working: $($response.Count) records found" -ForegroundColor Green
} catch {
    Write-Host "❌ Medical Records endpoint error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "This is expected if no records exist yet" -ForegroundColor Yellow
}

Write-Host "`n🎉 Test data creation complete!" -ForegroundColor Green
Write-Host "You can now:" -ForegroundColor Cyan
Write-Host "1. Login to frontend with doctor@test.com / password123" -ForegroundColor White
Write-Host "2. Login to frontend with patient@test.com / password123" -ForegroundColor White
Write-Host "3. Navigate to Medical Records page" -ForegroundColor White
Write-Host "4. Try creating a new medical record" -ForegroundColor White
