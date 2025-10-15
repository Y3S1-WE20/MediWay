# Start MediWay Backend with MySQL Database
# This script configures the backend to use MySQL instead of H2 in-memory

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   MediWay Backend - MySQL Mode" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Set MySQL environment variables
$env:SPRING_PROFILES_ACTIVE = "mysql"
$env:MYSQL_USER = "mediway_user"
$env:MYSQL_PASSWORD = "admin"
$env:MYSQL_URL = "jdbc:mysql://localhost:3306/mediwaydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"

Write-Host "Configuration:" -ForegroundColor Yellow
Write-Host "  Profile: mysql" -ForegroundColor Gray
Write-Host "  Database: mediwaydb" -ForegroundColor Gray
Write-Host "  User: mediway_user" -ForegroundColor Gray
Write-Host "  URL: localhost:3306" -ForegroundColor Gray
Write-Host ""

# Check if port 8080 is available
Write-Host "Checking port 8080..." -ForegroundColor Yellow
$portCheck = Test-NetConnection -ComputerName localhost -Port 8080 -InformationLevel Quiet -WarningAction SilentlyContinue

if ($portCheck) {
    Write-Host "⚠️  Port 8080 is in use. Killing process..." -ForegroundColor Yellow
    $connections = netstat -ano | Select-String ":8080" | Select-String "LISTENING"
    if ($connections) {
        $connections | ForEach-Object {
            $line = $_.Line
            $pid = ($line -split '\s+')[-1]
            if ($pid -match '^\d+$') {
                try {
                    Stop-Process -Id $pid -Force
                    Write-Host "✅ Stopped process PID: $pid" -ForegroundColor Green
                }
                catch {
                    Write-Host "❌ Could not stop process: $_" -ForegroundColor Red
                }
            }
        }
        Start-Sleep -Seconds 2
    }
}

Write-Host ""
Write-Host "Starting backend..." -ForegroundColor Yellow
Write-Host ""

# Start Spring Boot with MySQL profile
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Backend stopped" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
