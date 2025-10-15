# MediWay Backend MySQL Connection Fix Script
# This script will help you start the backend with MySQL and verify the connection

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   MediWay - MySQL Connection Fix" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Check MySQL is running
Write-Host "[Step 1/5] Checking MySQL Server..." -ForegroundColor Yellow
try {
    $mysqlCheck = Test-NetConnection -ComputerName localhost -Port 3306 -WarningAction SilentlyContinue -ErrorAction Stop
    if ($mysqlCheck.TcpTestSucceeded) {
        Write-Host "✓ MySQL server is running on port 3306" -ForegroundColor Green
    } else {
        Write-Host "✗ ERROR: MySQL server is NOT running on port 3306" -ForegroundColor Red
        Write-Host "  Please start MySQL server and try again." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "⚠ WARNING: Could not verify MySQL status" -ForegroundColor Yellow
    $continue = Read-Host "Continue anyway? (y/n)"
    if ($continue -ne "y") {
        exit 1
    }
}

# Step 2: Verify database exists
Write-Host ""
Write-Host "[Step 2/5] Verifying Database..." -ForegroundColor Yellow
Write-Host "Database: mediwaydb" -ForegroundColor Gray
Write-Host "Username: mediway_user" -ForegroundColor Gray
Write-Host "Password: admin" -ForegroundColor Gray
Write-Host ""
Write-Host "Please ensure:" -ForegroundColor Cyan
Write-Host "  1. MySQL Workbench shows 'mediwaydb' database" -ForegroundColor White
Write-Host "  2. User 'mediway_user' has access to 'mediwaydb'" -ForegroundColor White
Write-Host "  3. Password is 'admin'" -ForegroundColor White
Write-Host ""
$dbReady = Read-Host "Is the database ready? (y/n)"
if ($dbReady -ne "y") {
    Write-Host ""
    Write-Host "Please create the database first:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "In MySQL Workbench, run:" -ForegroundColor Cyan
    Write-Host "  CREATE DATABASE IF NOT EXISTS mediwaydb;" -ForegroundColor White
    Write-Host "  CREATE USER IF NOT EXISTS 'mediway_user'@'localhost' IDENTIFIED BY 'admin';" -ForegroundColor White
    Write-Host "  GRANT ALL PRIVILEGES ON mediwaydb.* TO 'mediway_user'@'localhost';" -ForegroundColor White
    Write-Host "  FLUSH PRIVILEGES;" -ForegroundColor White
    Write-Host ""
    exit 1
}

# Step 3: Stop any existing backend process
Write-Host ""
Write-Host "[Step 3/5] Checking Port 8080..." -ForegroundColor Yellow
$port8080 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($port8080) {
    Write-Host "⚠ Port 8080 is in use by process $($port8080.OwningProcess)" -ForegroundColor Yellow
    $kill = Read-Host "Kill the process? (y/n)"
    if ($kill -eq "y") {
        Stop-Process -Id $port8080.OwningProcess -Force
        Write-Host "✓ Process stopped" -ForegroundColor Green
        Start-Sleep -Seconds 3
    } else {
        Write-Host "Please stop the process manually and try again." -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "✓ Port 8080 is available" -ForegroundColor Green
}

# Step 4: Display configuration
Write-Host ""
Write-Host "[Step 4/5] Backend Configuration" -ForegroundColor Yellow
Write-Host "The backend is now configured to use:" -ForegroundColor Gray
Write-Host "  Database: MySQL (NOT H2)" -ForegroundColor Green
Write-Host "  URL: jdbc:mysql://localhost:3306/mediwaydb" -ForegroundColor White
Write-Host "  Username: mediway_user" -ForegroundColor White
Write-Host "  Password: admin" -ForegroundColor White
Write-Host "  Hibernate DDL: update (preserves data)" -ForegroundColor White
Write-Host ""

# Step 5: Start backend
Write-Host "[Step 5/5] Starting Backend..." -ForegroundColor Yellow
Write-Host "Please wait while the backend starts..." -ForegroundColor Gray
Write-Host ""

# Change to backend directory
Set-Location -Path "F:\MediWay\backend"

# Start backend
Write-Host "Starting Spring Boot application..." -ForegroundColor Gray
Write-Host ""
Write-Host "Watch for these messages in the output:" -ForegroundColor Cyan
Write-Host "  ✓ 'HikariPool-1 - Starting...' (MySQL connection pool)" -ForegroundColor White
Write-Host "  ✓ 'HikariPool-1 - Start completed' (Connection successful)" -ForegroundColor White
Write-Host "  ✓ 'Started MediWayBackendApplication' (Backend ready)" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Backend Output Below:" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Run the backend
.\mvnw.cmd spring-boot:run
