# Run the SQL script to insert pending payments for a user
# Requires MySQL client (mysql) on PATH or you can paste SQL into MySQL Workbench

$mysqlHost = 'localhost'
$mysqlPort = 3306
$mysqlUser = 'mediway_user'
$mysqlPassword = 'admin'
$database = 'mediwaydb'
$sqlFile = "$(Join-Path $PSScriptRoot 'insert-pending-payments.sql')"

if (-Not (Test-Path $sqlFile)) {
    Write-Host "SQL file not found: $sqlFile" -ForegroundColor Red
    exit 1
}

Write-Host "This will run the SQL file to insert two pending payments into $database.payments" -ForegroundColor Yellow
Write-Host "SQL file: $sqlFile" -ForegroundColor Gray

$confirm = Read-Host "Proceed? (y/n)"
if ($confirm -ne 'y') { Write-Host "Aborted"; exit 0 }

# Run using mysql CLI
$cmd = "mysql -h $mysqlHost -P $mysqlPort -u $mysqlUser -p$mysqlPassword $database < `"$sqlFile`""
Write-Host "Running: $cmd" -ForegroundColor Gray

try {
    iex $cmd
    Write-Host "SQL script executed. Verify the payments in MySQL Workbench or run the SELECT at the end of the SQL file." -ForegroundColor Green
} catch {
    Write-Host "Failed to execute SQL via mysql CLI. Error: $_" -ForegroundColor Red
    Write-Host "If you don't have mysql CLI, open the SQL file and run it in MySQL Workbench manually: $sqlFile" -ForegroundColor Yellow
}
