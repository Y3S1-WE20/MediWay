# Quick Fix Script for UUID Format Issue
# Run this script to fix the doctor data and test the appointment booking

Write-Host "=== MediWay UUID Format Fix ===" -ForegroundColor Cyan
Write-Host ""

# Check if MySQL is accessible
Write-Host "Step 1: Checking MySQL connection..." -ForegroundColor Yellow
$mysqlPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

if (-not (Test-Path $mysqlPath)) {
    Write-Host "❌ MySQL not found at default location" -ForegroundColor Red
    Write-Host "Please run the fix-doctors-table.sql script manually in MySQL Workbench" -ForegroundColor Yellow
    Write-Host "Script location: F:\MediWay\backend\scripts\fix-doctors-table.sql" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "In MySQL Workbench:" -ForegroundColor Yellow
    Write-Host "1. Open the script file" -ForegroundColor White
    Write-Host "2. Click Execute (lightning bolt icon)" -ForegroundColor White
    Write-Host "3. Verify 5 doctors are inserted" -ForegroundColor White
    exit
}

Write-Host "✅ MySQL found" -ForegroundColor Green

# Run the fix script
Write-Host ""
Write-Host "Step 2: Running fix-doctors-table.sql..." -ForegroundColor Yellow

$scriptPath = "F:\MediWay\backend\scripts\fix-doctors-table.sql"

if (-not (Test-Path $scriptPath)) {
    Write-Host "❌ Script not found: $scriptPath" -ForegroundColor Red
    exit
}

try {
    & $mysqlPath -u mediway_user -padmin -D mediwaydb < $scriptPath
    Write-Host "✅ Doctors table fixed and populated" -ForegroundColor Green
}
catch {
    Write-Host "❌ Error running SQL script: $_" -ForegroundColor Red
    Write-Host "Please run the script manually in MySQL Workbench" -ForegroundColor Yellow
    exit
}

# Verify doctors were inserted
Write-Host ""
Write-Host "Step 3: Verifying doctors..." -ForegroundColor Yellow

$verifyQuery = "SELECT COUNT(*) as count FROM doctors;"
$result = & $mysqlPath -u mediway_user -padmin -D mediwaydb -se $verifyQuery

if ($result -match "5") {
    Write-Host "✅ 5 doctors found in database" -ForegroundColor Green
}
else {
    Write-Host "⚠️  Expected 5 doctors, found: $result" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Fix Complete! ===" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Refresh your frontend browser (Ctrl+F5)" -ForegroundColor White
Write-Host "2. Navigate to Book Appointment page" -ForegroundColor White
Write-Host "3. Doctors should now load successfully" -ForegroundColor White
Write-Host "4. Book an appointment via the UI" -ForegroundColor White
Write-Host "5. Check Appointments page to see your booking" -ForegroundColor White
Write-Host ""
