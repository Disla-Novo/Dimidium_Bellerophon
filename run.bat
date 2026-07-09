@echo off
title Dimidium - Web Server
echo Starting Dimidium Web Server...
echo.

:: Set the default port
set PORT=4567

:: Check config exists; read the port
if exist config.properties (
 :: delims includes space and tab to handle formatting variations
for /f "tokens=1,2 delims== " %%A in (config.properties) do (
    if "%%A"=="server.port" set PORT=%%B
)
)

start "Dimidium Server" .\jre\bin\java.exe -jar SyntaxN.jar
timeout /t 3 /nobreak >nul
start http://localhost:%PORT%
echo.
echo ========================================
echo Server is running in its own window
echo CLOSE the "Dimidium Server" window when done
echo ========================================
echo.
echo Press any key to close this message...
pause >nul
exit
