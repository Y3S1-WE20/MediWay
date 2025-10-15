MySQL Server & Workbench setup for MediWay

This document explains how to install MySQL Server and MySQL Workbench on Windows, create a database and user for the MediWay project, and verify connectivity from both Workbench and the Spring Boot app.

1) Install MySQL Server and Workbench
- Download MySQL Installer (Windows) from https://dev.mysql.com/downloads/installer/
- Run the installer and choose "Developer Default" (includes Workbench) or customize to include MySQL Server + Workbench.
- During installation set a strong root password and note it.

2) Start MySQL Server
- Use Windows Services or MySQL Notifier to ensure the MySQL service is running.

3) Create database and user (using MySQL Workbench or command-line)
- Open MySQL Workbench and connect as `root` (or another admin user).
- In Workbench, open a new SQL tab and run the following (change password as needed):

```sql
CREATE DATABASE mediwaydb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'mediway_user'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON mediwaydb.* TO 'mediway_user'@'localhost';
FLUSH PRIVILEGES;
```

- Or from command line (PowerShell):

```powershell
# Start mysql client (you'll be prompted for root password)
mysql -u root -p
# then run the SQL above
```

4) Verify in MySQL Workbench
- Create a new connection in Workbench using:
  - Host: 127.0.0.1
  - Port: 3306
  - Username: mediway_user
  - Password: (fill in)
- Test Connection — it should succeed.

5) Configure the app to use MySQL
- The project contains `src/main/resources/application-mysql.properties`.
- You can set environment variables or pass them at runtime. Available variables:
  - MYSQL_URL (default: `jdbc:mysql://localhost:3306/mediwaydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`)
  - MYSQL_USER (default: `mediway_user`)
  - MYSQL_PASSWORD (default: `change_me`)

- To run the app locally with the MySQL profile (PowerShell):

```powershell
setx MYSQL_USER "mediway_user"
setx MYSQL_PASSWORD "StrongPasswordHere!"
setx SPRING_PROFILES_ACTIVE "mysql"
# Restart terminal or sign out/in to pick up setx changes, or set in current session:
$env:MYSQL_USER = 'mediway_user'
$env:MYSQL_PASSWORD = 'StrongPasswordHere!'
$env:SPRING_PROFILES_ACTIVE = 'mysql'
# Run the app using Maven wrapper
cd backend
.\mvnw.cmd spring-boot:run
```

- Alternatively, pass properties on command line:

```powershell
cd backend
.\mvnw.cmd -Dspring-boot.run.profiles=mysql -Dspring-boot.run.jvmArguments="-DMYSQL_USER=mediway_user -DMYSQL_PASSWORD=StrongPasswordHere!" spring-boot:run
```

6) Troubleshooting
- If you see connector errors, ensure `mysql-connector-j` exists in `backend/pom.xml` (it does by default).
- Check `application-mysql.properties` for correct URL and driver class.
- Check MySQL server logs and that the user host is `localhost` (or `%` for remote access).

7) Security note
- Do not commit passwords to source control. Use environment variables or a secrets manager in production.

If you want, I can also:
- Add README steps to the project root with the commands above.
- Add a small script `backend/run-mysql.ps1` to set env vars and run the app for dev (no secrets committed).
