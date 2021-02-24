@echo off
clear

set _target=%cd%\target
set _jre=%cd%\jre
set _jfxmods=%homedrive%%homepath%\develop\ferramentas\java\javafx\javafx-jmods-11.0.2

:start
cls
echo.
echo 1. Package
echo 2. Custom JRE
echo 3. Exit
echo.
set /p choice=Informe a opcao:
if '%choice%'=='' echo "%choice%" invalido
if '%choice%'=='1' goto package
if '%choice%'=='2' goto jre
if '%choice%'=='3' goto end
echo.
goto start

:package
set delTarget=0
if exist "%_target%\" (set delTarget=1)
if %delTarget%==1 (
    del /s /q "%_target%\*.*"
    for /d %%p in ("%_target%\*.*") do rmdir "%%p" /s /q
    rmdir "%_target%\" /s /q
)
mvn clean compile package
goto end

:jre
set delJRE=0
if exist "%_jre%\" (set delJRE=1)
if %delJRE%==1 (
    del /s /q "%_jre%\*.*"
    for /d %%p in ("%_jre%\*.*") do rmdir "%%p" /s /q
    rmdir "%_jre%\" /s /q
)
jlink --output jre ^
 --module-path %_jfxmods% ^
 --add-modules javafx.media,javafx.web,javafx.fxml,java.logging,java.net.http,java.sql,java.security.jgss
goto end

:end
pause
exit