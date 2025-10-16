@echo off
echo ========================================
echo   Starting MediWay Backend Server
echo ========================================
echo.

cd /d F:\MediWay\backend

echo Stopping any existing Java processes...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

echo.
echo Starting backend...
echo.
call mvnw.cmd spring-boot:run

pause
