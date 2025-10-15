# Test the appointments API endpoint directly
# Run this after logging in and getting a token

Write-Host "Testing Appointments API..." -ForegroundColor Cyan
Write-Host ""

# You need to replace YOUR_TOKEN_HERE with an actual JWT token from localStorage
# Get it from browser console: localStorage.getItem('mediway_token')

$token = Read-Host "Paste your JWT token from browser (localStorage.getItem('mediway_token'))"

if ([string]::IsNullOrWhiteSpace($token)) {
    Write-Host "Error: Token is required" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Testing GET /api/appointments/my..." -ForegroundColor Yellow

try {
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/appointments/my" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop
    
    Write-Host "✓ Success! Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host ""
    Write-Host "Response Body:" -ForegroundColor Cyan
    $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
    
} catch {
    Write-Host "✗ Error!" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    Write-Host "Error Message: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $responseBody = $reader.ReadToEnd()
        Write-Host ""
        Write-Host "Response Body:" -ForegroundColor Yellow
        Write-Host $responseBody
    }
}

Write-Host ""
Write-Host "Also testing GET /api/appointments/doctors..." -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/appointments/doctors" `
        -Method GET `
        -ErrorAction Stop
    
    Write-Host "✓ Success! Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host ""
    Write-Host "Doctors found:" -ForegroundColor Cyan
    ($response.Content | ConvertFrom-Json).Length
    
} catch {
    Write-Host "✗ Error fetching doctors!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
