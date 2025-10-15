# Kill process using port 8080
Write-Host "Checking for processes using port 8080..." -ForegroundColor Cyan

$connections = netstat -ano | Select-String ":8080" | Select-String "LISTENING"

if ($connections) {
    $connections | ForEach-Object {
        $line = $_.Line
        $pid = ($line -split '\s+')[-1]
        
        if ($pid -match '^\d+$') {
            try {
                $process = Get-Process -Id $pid -ErrorAction Stop
                Write-Host "Found process: $($process.ProcessName) (PID: $pid)" -ForegroundColor Yellow
                Write-Host "Stopping process..." -ForegroundColor Yellow
                Stop-Process -Id $pid -Force
                Write-Host "Process stopped successfully!" -ForegroundColor Green
            }
            catch {
                Write-Host "Could not stop process (PID: $pid): $_" -ForegroundColor Red
            }
        }
    }
    Start-Sleep -Seconds 1
    Write-Host "`nPort 8080 is now free!" -ForegroundColor Green
}
else {
    Write-Host "Port 8080 is not in use." -ForegroundColor Green
}
