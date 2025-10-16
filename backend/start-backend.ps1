# Start MediWay Backend
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Starting MediWay Backend Server" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Stop any running backend
Write-Host "Stopping any existing backend processes..." -ForegroundColor Yellow
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# Navigate to backend directory
Set-Location F:\MediWay\backend

# Start backend
Write-Host "Starting backend server..." -ForegroundColor Green
if (Test-Path ".\mvnw.cmd") {
    .\mvnw.cmd spring-boot:run
} else {
    mvn spring-boot:run
}
