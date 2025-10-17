# =====================================================
# AUTOMATED FRESH START
# This script stops backend, recreates database, and restarts backend
# =====================================================

Write-Host ''
Write-Host '========================================' -ForegroundColor Cyan
Write-Host '   MEDIWAY - FRESH START AUTOMATION' -ForegroundColor Cyan
Write-Host '========================================' -ForegroundColor Cyan
Write-Host ''

# Step 1: Stop backend if running
Write-Host '[Step 1/4] Checking for running backend processes...' -ForegroundColor Yellow
$javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.Path -like "*MediWay*" }
if ($javaProcesses) {
    Write-Host 'Stopping backend processes...' -ForegroundColor Yellow
    $javaProcesses | Stop-Process -Force
    Start-Sleep -Seconds 2
    Write-Host 'Backend stopped successfully!' -ForegroundColor Green
} else {
    Write-Host 'No backend processes running.' -ForegroundColor Gray
}

# Step 2: Instructions for MySQL
Write-Host ''
Write-Host '[Step 2/4] DATABASE RECREATION REQUIRED' -ForegroundColor Yellow
Write-Host ''
Write-Host 'PLEASE DO THE FOLLOWING IN MYSQL WORKBENCH:' -ForegroundColor White -BackgroundColor DarkRed
Write-Host ''
Write-Host '  1. Open MySQL Workbench' -ForegroundColor White
Write-Host '  2. Connect to your database (mediway_user@localhost)' -ForegroundColor White
Write-Host '  3. File -> Open SQL Script' -ForegroundColor White
Write-Host '  4. Select: F:\MediWay\FRESH_START.sql' -ForegroundColor White
Write-Host '  5. Click Execute (lightning bolt icon)' -ForegroundColor White
Write-Host '  6. Verify output shows SUCCESS messages' -ForegroundColor White
Write-Host ''

$confirmation = Read-Host 'Have you run FRESH_START.sql successfully? (yes/no)'
if ($confirmation -ne 'yes') {
    Write-Host ''
    Write-Host 'Please run the SQL script first, then run this script again!' -ForegroundColor Red
    Write-Host ''
    exit 1
}

# Step 3: Clean and rebuild backend
Write-Host ''
Write-Host '[Step 3/4] Cleaning and rebuilding backend...' -ForegroundColor Yellow
Set-Location 'F:\MediWay\backend'

Write-Host 'Running clean...' -ForegroundColor Gray
& .\mvnw.cmd clean -q
if ($LASTEXITCODE -ne 0) {
    Write-Host 'Clean failed!' -ForegroundColor Red
    exit 1
}
Write-Host 'Clean completed!' -ForegroundColor Green

Write-Host 'Compiling...' -ForegroundColor Gray
& .\mvnw.cmd compile -DskipTests -q
if ($LASTEXITCODE -ne 0) {
    Write-Host 'Compilation failed!' -ForegroundColor Red
    exit 1
}
Write-Host 'Compilation successful!' -ForegroundColor Green

# Step 4: Start backend
Write-Host ''
Write-Host '[Step 4/4] Starting backend...' -ForegroundColor Yellow
Write-Host ''
Write-Host 'Backend starting in new terminal window...' -ForegroundColor Cyan
Write-Host 'Wait for: "Started MediWayBackendApplication"' -ForegroundColor Cyan
Write-Host ''

# Start backend in a new window
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location 'F:\MediWay\backend'; .\mvnw.cmd spring-boot:run"

Write-Host ''
Write-Host '========================================' -ForegroundColor Green
Write-Host '   AUTOMATION COMPLETE!' -ForegroundColor Green
Write-Host '========================================' -ForegroundColor Green
Write-Host ''
Write-Host 'Next Steps:' -ForegroundColor Cyan
Write-Host '  1. Wait for backend to start (check new terminal window)' -ForegroundColor White
Write-Host '  2. Test API: http://localhost:8080/api/appointments/doctors' -ForegroundColor White
Write-Host '  3. Should see 3 doctors with PROPER UUIDs!' -ForegroundColor White
Write-Host '  4. Go to frontend: http://localhost:5174' -ForegroundColor White
Write-Host '  5. Register new account and test!' -ForegroundColor White
Write-Host ''
Write-Host 'Expected Result: 3 doctors with DIFFERENT UUIDs, no duplicates!' -ForegroundColor Green
Write-Host ''
