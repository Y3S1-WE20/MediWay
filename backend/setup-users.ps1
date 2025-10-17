# Setup Admin and Doctor Users Script
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   MediWay User Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# MySQL path (adjust if needed)
$mysqlPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if (-not (Test-Path $mysqlPath)) {
    $mysqlPath = "C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe"
}

if (-not (Test-Path $mysqlPath)) {
    Write-Host "ERROR: MySQL not found!" -ForegroundColor Red
    Write-Host "Please update the path in this script or add MySQL to PATH" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Alternative: Run these SQL commands manually in MySQL Workbench:" -ForegroundColor Yellow
    Write-Host ""
    Get-Content .\SETUP_USERS.sql
    exit 1
}

Write-Host "Found MySQL at: $mysqlPath" -ForegroundColor Green
Write-Host "Running setup script..." -ForegroundColor Yellow
Write-Host ""

# Run the SQL script
& $mysqlPath -u root -ppassword mediway_db -e "source F:\CSSE` Assignments\MediWay\backend\SETUP_USERS.sql"

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "   Setup Complete!" -ForegroundColor Green  
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "LOGIN CREDENTIALS:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "ADMIN:" -ForegroundColor Yellow
    Write-Host "  Email: admin@mediway.com"
    Write-Host "  Password: Admin123"
    Write-Host ""
    Write-Host "DOCTORS:" -ForegroundColor Yellow
    Write-Host "  Dr. Smith"
    Write-Host "    Email: dr.smith@mediway.com"
    Write-Host "    Password: Doctor123"
    Write-Host ""
    Write-Host "  Dr. Johnson"  
    Write-Host "    Email: dr.johnson@mediway.com"
    Write-Host "    Password: Doctor123"
    Write-Host ""
    Write-Host "  Dr. Williams"
    Write-Host "    Email: dr.williams@mediway.com"
    Write-Host "    Password: Doctor123"
    Write-Host ""
    Write-Host "PATIENT:" -ForegroundColor Yellow
    Write-Host "  Email: tester1@gmail.com"
    Write-Host "  Password: 123456"
    Write-Host ""
} else {
    Write-Host "ERROR: Failed to run SQL script" -ForegroundColor Red
    Write-Host "Try running the SQL manually from SETUP_USERS.sql" -ForegroundColor Yellow
}
