#!/usr/bin/env pwsh

Write-Host "🚀 FIXING MEDIWAY PAYPAL ISSUES" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

# Kill any existing Java processes
Write-Host "🔪 Stopping existing backend processes..." -ForegroundColor Yellow
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3

# Navigate to backend directory
Write-Host "📂 Navigating to backend directory..." -ForegroundColor Cyan
Set-Location "backend"

# Clean and compile
Write-Host "🧹 Cleaning and compiling backend..." -ForegroundColor Yellow
if (Test-Path "target") {
    Remove-Item -Recurse -Force "target" -ErrorAction SilentlyContinue
}

# Try different approaches to start the backend
Write-Host "🔄 Attempting to start backend..." -ForegroundColor Green

# Method 1: Try Maven wrapper
if (Test-Path "mvnw.cmd") {
    Write-Host "Method 1: Using Maven wrapper..." -ForegroundColor Cyan
    try {
        cmd /c "mvnw.cmd clean compile spring-boot:run"
    }
    catch {
        Write-Host "Maven wrapper failed" -ForegroundColor Red
    }
}

# Method 2: Try with Maven
Write-Host "Method 2: Using Maven directly..." -ForegroundColor Cyan
try {
    mvn clean compile spring-boot:run
}
catch {
    Write-Host "Maven direct failed" -ForegroundColor Red
}

Write-Host "Backend startup completed!" -ForegroundColor Green