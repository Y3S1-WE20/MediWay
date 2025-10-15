<#
start-dev.ps1
- Kills any process listening on port 8080
- Sets JAVA_HOME and PATH to JDK 17 for this session
- Sets MySQL environment variables and activates the mysql profile
- Runs the Spring Boot app using the Maven wrapper

Usage (PowerShell):
.\start-dev.ps1 -MysqlUser mediway_user -MysqlPassword 'admin' -JavaHome 'C:\Program Files\Java\jdk-17'
#>
param(
    [string]$MysqlUser = 'mediway_user',
    [string]$MysqlPassword = 'admin',
    [string]$MysqlUrl = 'jdbc:mysql://127.0.0.1:3306/mediwaydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true',
    [string]$JavaHome = 'C:\Program Files\Java\jdk-17',
    [int]$Port = 8080
)

function Kill-PortProcess {
    param([int]$LocalPort)
    try {
        $conn = Get-NetTCPConnection -LocalPort $LocalPort -ErrorAction SilentlyContinue | Where-Object { $_.State -eq 'Listen' }
        if ($conn) {
            $pids = $conn | Select-Object -ExpandProperty OwningProcess -Unique
            foreach ($pid in $pids) {
                Write-Host "Killing process $pid that is listening on port $LocalPort..."
                try { taskkill /PID $pid /F | Out-Null; Write-Host "Killed $pid" } catch { Write-Warning "Failed to kill $pid: $_" }
            }
        } else {
            Write-Host "No process listening on port $LocalPort"
        }
    } catch {
        Write-Warning "Error while checking port: $_"
    }
}

# Kill any process on the port
Kill-PortProcess -LocalPort $Port

# Set Java for this session
if (-Not (Test-Path "$JavaHome\bin\java.exe")) {
    Write-Warning "Java executable not found at $JavaHome\bin\java.exe. Please update -JavaHome parameter or install JDK 17."
} else {
    $env:JAVA_HOME = $JavaHome
    $env:Path = "$($env:JAVA_HOME)\bin;$env:Path"
    Write-Host "Using JAVA_HOME=$env:JAVA_HOME"
    & java --version
}

# Set DB env for this session
$env:MYSQL_USER = $MysqlUser
$env:MYSQL_PASSWORD = $MysqlPassword
$env:MYSQL_URL = $MysqlUrl
$env:SPRING_PROFILES_ACTIVE = 'mysql'
Write-Host "MYSQL_USER=$env:MYSQL_USER; SPRING_PROFILES_ACTIVE=$env:SPRING_PROFILES_ACTIVE"

# Start app
Push-Location (Split-Path -Path $MyInvocation.MyCommand.Definition -Parent)
if (-Not (Test-Path '.\mvnw.cmd')) { Write-Host "mvnw.cmd not found in script folder; trying ../backend"; Set-Location '..\backend' }
else { Set-Location '.' }

Write-Host "Starting Spring Boot app (this will block the terminal)"
.\mvnw.cmd spring-boot:run

Pop-Location
