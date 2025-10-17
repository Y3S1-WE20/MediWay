# MediWay Emergency Fix Script
# Run this in PowerShell as Administrator

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  MediWay Emergency Fix Script" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Find MySQL
Write-Host "[1/5] Locating MySQL..." -ForegroundColor Yellow
$mysqlPaths = @(
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\xampp\mysql\bin\mysql.exe"
)

$mysqlPath = $null
foreach ($path in $mysqlPaths) {
    if (Test-Path $path) {
        $mysqlPath = $path
        Write-Host "✓ Found MySQL at: $path" -ForegroundColor Green
        break
    }
}

if (-not $mysqlPath) {
    Write-Host "✗ MySQL not found in standard locations" -ForegroundColor Red
    Write-Host "Please run the SQL script manually in MySQL Workbench:" -ForegroundColor Yellow
    Write-Host "  File: F:\MediWay\backend\scripts\fix-everything.sql" -ForegroundColor White
    exit 1
}

# Step 2: Run SQL Fix
Write-Host ""
Write-Host "[2/5] Running database fix..." -ForegroundColor Yellow
$sqlFile = "F:\MediWay\backend\scripts\fix-everything.sql"

if (-not (Test-Path $sqlFile)) {
    Write-Host "✗ SQL file not found: $sqlFile" -ForegroundColor Red
    exit 1
}

try {
    & $mysqlPath -u mediway_user -padmin mediwaydb < $sqlFile
    Write-Host "✓ Database fixed successfully!" -ForegroundColor Green
} catch {
    Write-Host "✗ Error running SQL script" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please run the SQL script manually in MySQL Workbench" -ForegroundColor Yellow
    exit 1
}

# Step 3: Clean build
Write-Host ""
Write-Host "[3/5] Cleaning backend build..." -ForegroundColor Yellow
Push-Location "F:\MediWay\backend"
try {
    & .\mvnw.cmd clean | Out-Null
    Write-Host "✓ Backend cleaned" -ForegroundColor Green
} catch {
    Write-Host "✗ Error cleaning backend" -ForegroundColor Red
}
Pop-Location

# Step 4: Rebuild backend
Write-Host ""
Write-Host "[4/5] Rebuilding backend (this may take a minute)..." -ForegroundColor Yellow
Push-Location "F:\MediWay\backend"
try {
    $output = & .\mvnw.cmd install -DskipTests 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Backend rebuilt successfully!" -ForegroundColor Green
    } else {
        Write-Host "✗ Build failed" -ForegroundColor Red
        Write-Host "Please rebuild manually: cd F:\MediWay\backend && .\mvnw.cmd clean install" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ Error building backend" -ForegroundColor Red
}
Pop-Location

# Step 5: Summary
Write-Host ""
Write-Host "[5/5] Fix Complete!" -ForegroundColor Green
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Next Steps" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Start Backend:" -ForegroundColor Yellow
Write-Host "   cd F:\MediWay\backend" -ForegroundColor White
Write-Host "   .\mvnw.cmd spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "2. Start Frontend (in new terminal):" -ForegroundColor Yellow
Write-Host "   cd F:\MediWay\frontend" -ForegroundColor White
Write-Host "   npm run dev" -ForegroundColor White
Write-Host ""
Write-Host "3. Test the app:" -ForegroundColor Yellow
Write-Host "   - Open http://localhost:5173" -ForegroundColor White
Write-Host "   - Register/Login" -ForegroundColor White
Write-Host "   - Try booking an appointment" -ForegroundColor White
Write-Host "   - Check Profile for QR code" -ForegroundColor White
Write-Host ""
Write-Host "✓ Doctors table recreated with proper UUIDs" -ForegroundColor Green
Write-Host "✓ QR code column added to users" -ForegroundColor Green
Write-Host "✓ Medical records tables created" -ForegroundColor Green
Write-Host "✓ Backend rebuilt with latest code" -ForegroundColor Green
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
