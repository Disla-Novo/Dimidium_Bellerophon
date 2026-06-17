@echo off
title Dimidium - Web Server
echo Starting Dimidium Web Server...
echo.
start "Dimidium Server" .\jre\bin\java.exe -jar SyntaxN.jar
timeout /t 3 /nobreak >nul
start http://localhost:4567
echo.
echo ========================================
echo Server is running in its own window
echo CLOSE the "Dimidium Server" window when done
echo ========================================
echo.
echo Press any key to close this message...
pause >nul
exit