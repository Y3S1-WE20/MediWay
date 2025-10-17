# Complete Backend Fix Script
Write-Host '=====================================' -ForegroundColor Cyan
Write-Host ' MediWay Complete Backend Fix' -ForegroundColor Cyan
Write-Host '=====================================' -ForegroundColor Cyan
Write-Host ''

$backendPath = "F:\MediWay\backend"

# Step 1: Check if MySQL fix was run
Write-Host '[1/4] Checking database...' -ForegroundColor Yellow
Write-Host 'Have you run ULTIMATE_FIX.sql in MySQL Workbench? (Y/N)' -ForegroundColor White
$response = Read-Host

if ($response -ne "Y" -and $response -ne "y") {
    Write-Host ''
    Write-Host 'Please run ULTIMATE_FIX.sql in MySQL Workbench first!' -ForegroundColor Red
    Write-Host ''
    Write-Host 'Steps:' -ForegroundColor Yellow
    Write-Host '1. Open MySQL Workbench' -ForegroundColor White
    Write-Host '2. File -> Open SQL Script' -ForegroundColor White
    Write-Host '3. Select: F:\\MediWay\\ULTIMATE_FIX.sql' -ForegroundColor White
    Write-Host '4. Click Execute' -ForegroundColor White
    Write-Host '5. Run this script again' -ForegroundColor White
    Write-Host ''
    exit 1
}

# Step 2: Delete target folder
Write-Host ""
Write-Host '[2/4] Deleting old build cache...' -ForegroundColor Yellow
Push-Location $backendPath
if (Test-Path "target") {
    Remove-Item -Recurse -Force target
    Write-Host 'Deleted target folder' -ForegroundColor Green
} else {
    Write-Host 'No target folder to delete' -ForegroundColor Green
}
Pop-Location

# Step 3: Clean build
Write-Host ""
Write-Host '[3/4] Cleaning project...' -ForegroundColor Yellow
Push-Location $backendPath
& .\mvnw.cmd clean
if ($LASTEXITCODE -eq 0) {
    Write-Host 'Clean successful' -ForegroundColor Green
} else {
    Write-Host 'Clean failed' -ForegroundColor Red
    Pop-Location
    exit 1
}
Pop-Location

# Step 4: Build
Write-Host ""
Write-Host '[4/4] Building project (this may take a minute)...' -ForegroundColor Yellow
Push-Location $backendPath
& .\mvnw.cmd install -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host 'Build successful!' -ForegroundColor Green
} else {
    Write-Host 'Build failed' -ForegroundColor Red
    Pop-Location
    exit 1
}
Pop-Location

# Success
Write-Host ''
Write-Host '=====================================' -ForegroundColor Cyan
Write-Host ' Backend Fixed Successfully!' -ForegroundColor Green
Write-Host '=====================================' -ForegroundColor Cyan
Write-Host ''
Write-Host 'Next Steps:' -ForegroundColor Yellow
Write-Host ''
Write-Host '1. Start Backend:' -ForegroundColor White
Write-Host '   cd F:\\MediWay\\backend' -ForegroundColor Gray
Write-Host '   .\\mvnw.cmd spring-boot:run' -ForegroundColor Gray
Write-Host ''
Write-Host "2. Wait for: 'Started MediWayBackendApplication'" -ForegroundColor White
Write-Host ''
Write-Host '3. Test API:' -ForegroundColor White
Write-Host '   http://localhost:8080/api/appointments/doctors' -ForegroundColor Gray
Write-Host ''
Write-Host '4. Test Frontend:' -ForegroundColor White
Write-Host '   http://localhost:5174' -ForegroundColor Gray
Write-Host ''
Write-Host 'Changes Applied:' -ForegroundColor Yellow
Write-Host 'Removed H2 database dependency' -ForegroundColor Green
Write-Host 'Fixed Doctor entity (removed @GeneratedValue)' -ForegroundColor Green
Write-Host 'Changed Hibernate to validate mode' -ForegroundColor Green
Write-Host 'Removed H2 console from security' -ForegroundColor Green
Write-Host ''
Write-Host '=====================================' -ForegroundColor Cyan
