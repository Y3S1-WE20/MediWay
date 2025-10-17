@echo off
echo Running simplified database schema...
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u mediway_user -padmin < "F:\MediWay\SIMPLE_DATABASE.sql"
if %ERRORLEVEL% EQU 0 (
    echo Database created successfully!
) else (
    echo Error creating database. Trying with root user...
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < "F:\MediWay\SIMPLE_DATABASE.sql"
)
pause