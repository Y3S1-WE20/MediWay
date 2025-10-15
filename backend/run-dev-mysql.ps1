<#
Sets environment variables in the current session and runs the Spring Boot app with the MySQL profile.
Usage (PowerShell):
.\run-dev-mysql.ps1 -User mediway_user -Password 'StrongPasswordHere!'
#>
param(
    [string]$User = 'mediway_user',
    [string]$Password = 'change_me',
    [string]$Url = 'jdbc:mysql://localhost:3306/mediwaydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true'
)

Write-Host "Setting env vars for MySQL and running Spring Boot (profile: mysql)"
$env:MYSQL_USER = $User
$env:MYSQL_PASSWORD = $Password
$env:MYSQL_URL = $Url
$env:SPRING_PROFILES_ACTIVE = 'mysql'

# Run the app via mvnw in backend folder
Push-Location -Path (Split-Path -Path $MyInvocation.MyCommand.Definition -Parent)
if (-Not (Test-Path '.\mvnw.cmd')) {
    Write-Host "mvnw.cmd not found in script dir; trying project backend folder"
    Set-Location '..\backend'
}
else {
    Set-Location '.'
}

.\mvnw.cmd spring-boot:run

Pop-Location
