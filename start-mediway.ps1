# MediWay - Quick Start Script
# This script starts both backend (with MySQL) and frontend

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   MediWay - Quick Start" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if we're in the correct directory
if (-Not (Test-Path "backend") -or -Not (Test-Path "frontend")) {
    Write-Host "ERROR: Please run this script from the MediWay root directory (F:\MediWay)" -ForegroundColor Red
    exit 1
}

# Step 1: Start Backend with MySQL
Write-Host "[1/3] Starting Backend with MySQL..." -ForegroundColor Yellow
Write-Host "      Profile: mysql" -ForegroundColor Gray
Write-Host "      Database: mediwaydb" -ForegroundColor Gray
Write-Host "      User: mediway_user" -ForegroundColor Gray
Write-Host ""

# Set environment variables for MySQL
$env:SPRING_PROFILES_ACTIVE = "mysql"
$env:MYSQL_USER = "mediway_user"
$env:MYSQL_PASSWORD = "admin"

# Check if MySQL is accessible
Write-Host "Checking MySQL connection..." -ForegroundColor Gray
try {
    # Try to connect to MySQL (requires MySQL client)
    $mysqlCheck = Test-NetConnection -ComputerName localhost -Port 3306 -WarningAction SilentlyContinue
    if ($mysqlCheck.TcpTestSucceeded) {
        Write-Host "✓ MySQL server is running on port 3306" -ForegroundColor Green
    } else {
        Write-Host "⚠ WARNING: MySQL server not detected on port 3306" -ForegroundColor Yellow
        Write-Host "  Please make sure MySQL is running before continuing." -ForegroundColor Yellow
        $continue = Read-Host "Continue anyway? (y/n)"
        if ($continue -ne "y") {
            exit 1
        }
    }
} catch {
    Write-Host "⚠ Could not verify MySQL status" -ForegroundColor Yellow
}

# Check if port 8080 is already in use
Write-Host "Checking port 8080..." -ForegroundColor Gray
$port8080 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($port8080) {
    Write-Host "⚠ Port 8080 is already in use" -ForegroundColor Yellow
    Write-Host "  Process ID: $($port8080.OwningProcess)" -ForegroundColor Gray
    $kill = Read-Host "Kill the process and continue? (y/n)"
    if ($kill -eq "y") {
        Stop-Process -Id $port8080.OwningProcess -Force
        Write-Host "✓ Process killed" -ForegroundColor Green
        Start-Sleep -Seconds 2
    } else {
        Write-Host "Please stop the process on port 8080 manually and try again." -ForegroundColor Red
        exit 1
    }
}

# Start backend in a new window
Write-Host "Starting backend server..." -ForegroundColor Gray
$backendProcess = Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD\backend'; `$env:SPRING_PROFILES_ACTIVE='mysql'; `$env:MYSQL_USER='mediway_user'; `$env:MYSQL_PASSWORD='admin'; .\mvnw.cmd spring-boot:run" -PassThru
Write-Host "✓ Backend starting in new window (PID: $($backendProcess.Id))" -ForegroundColor Green
Write-Host "  Waiting 30 seconds for backend to initialize..." -ForegroundColor Gray
Start-Sleep -Seconds 30

# Step 2: Verify Backend Started
Write-Host ""
Write-Host "[2/3] Verifying Backend..." -ForegroundColor Yellow
$maxAttempts = 10
$attempt = 0
$backendReady = $false

while ($attempt -lt $maxAttempts -and -not $backendReady) {
    $attempt++
    Write-Host "  Attempt $attempt/$maxAttempts - Checking http://localhost:8080/api/payments/health..." -ForegroundColor Gray
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/payments/health" -TimeoutSec 5 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Host "✓ Backend is ready!" -ForegroundColor Green
            Write-Host "  Response: $($response.Content)" -ForegroundColor Gray
            $backendReady = $true
        }
    } catch {
        Write-Host "  Backend not ready yet..." -ForegroundColor Gray
        if ($attempt -lt $maxAttempts) {
            Start-Sleep -Seconds 5
        }
    }
}

if (-not $backendReady) {
    Write-Host "⚠ WARNING: Backend health check failed after $maxAttempts attempts" -ForegroundColor Yellow
    Write-Host "  The backend may still be starting. Check the backend window for errors." -ForegroundColor Yellow
    $continue = Read-Host "Continue to start frontend anyway? (y/n)"
    if ($continue -ne "y") {
        Write-Host "Exiting. Please check backend logs and try again." -ForegroundColor Red
        exit 1
    }
}

# Step 3: Start Frontend
Write-Host ""
Write-Host "[3/3] Starting Frontend..." -ForegroundColor Yellow
Write-Host "      Dev server: http://localhost:5174" -ForegroundColor Gray
Write-Host ""

# Check if port 5174 is already in use
$port5174 = Get-NetTCPConnection -LocalPort 5174 -ErrorAction SilentlyContinue
if ($port5174) {
    Write-Host "⚠ Port 5174 is already in use" -ForegroundColor Yellow
    Write-Host "  Process ID: $($port5174.OwningProcess)" -ForegroundColor Gray
    $kill = Read-Host "Kill the process and continue? (y/n)"
    if ($kill -eq "y") {
        Stop-Process -Id $port5174.OwningProcess -Force
        Write-Host "✓ Process killed" -ForegroundColor Green
        Start-Sleep -Seconds 2
    } else {
        Write-Host "Please stop the process on port 5174 manually and try again." -ForegroundColor Red
        exit 1
    }
}

# Start frontend in a new window
Write-Host "Starting frontend dev server..." -ForegroundColor Gray
$frontendProcess = Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD\frontend'; npm run dev" -PassThru
Write-Host "✓ Frontend starting in new window (PID: $($frontendProcess.Id))" -ForegroundColor Green
Write-Host "  Waiting 10 seconds for frontend to initialize..." -ForegroundColor Gray
Start-Sleep -Seconds 10

# Step 4: Final Summary
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   🚀 MediWay Started Successfully!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Backend:" -ForegroundColor Yellow
Write-Host "  URL:      http://localhost:8080" -ForegroundColor White
Write-Host "  API:      http://localhost:8080/api" -ForegroundColor White
Write-Host "  Profile:  mysql" -ForegroundColor White
Write-Host "  Database: mediwaydb (MySQL)" -ForegroundColor White
Write-Host "  PID:      $($backendProcess.Id)" -ForegroundColor Gray
Write-Host ""
Write-Host "Frontend:" -ForegroundColor Yellow
Write-Host "  URL:      http://localhost:5174" -ForegroundColor White
Write-Host "  PID:      $($frontendProcess.Id)" -ForegroundColor Gray
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Open browser to http://localhost:5174" -ForegroundColor White
Write-Host "  2. Register a new user (role: PATIENT)" -ForegroundColor White
Write-Host "  3. Login with registered credentials" -ForegroundColor White
Write-Host "  4. Navigate to Payments page" -ForegroundColor White
Write-Host "  5. Create a test payment" -ForegroundColor White
Write-Host ""
Write-Host "Testing:" -ForegroundColor Cyan
Write-Host "  See INTEGRATION_TESTING_GUIDE.md for detailed testing steps" -ForegroundColor White
Write-Host ""
Write-Host "Troubleshooting:" -ForegroundColor Cyan
Write-Host "  - Backend logs: Check the backend PowerShell window" -ForegroundColor White
Write-Host "  - Frontend logs: Check the frontend PowerShell window" -ForegroundColor White
Write-Host "  - MySQL: Open MySQL Workbench and check mediwaydb database" -ForegroundColor White
Write-Host ""
Write-Host "To stop:" -ForegroundColor Cyan
Write-Host "  Close the backend and frontend PowerShell windows" -ForegroundColor White
Write-Host "  Or run: Stop-Process $($backendProcess.Id), $($frontendProcess.Id)" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Press any key to exit this window..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
