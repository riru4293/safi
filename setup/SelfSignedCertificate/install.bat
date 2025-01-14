@ECHO OFF

REM Get installed PowerShell versions
FOR /F "USEBACKQ TOKENS=*" %%A in ( `pwsh -Command "$PSVersionTable.PSVersion.Major" 2^>NUL` ) do (
    SET PS_VER=%%A
)

REM Confirm PowerShell 7 is installed
IF DEFINED PS_VER IF %PS_VER% GEQ 7 (
    SETLOCAL ENABLEDELAYEDEXPANSION

    REM Escape arguments
    SET "ARGS="
    FOR %%A IN ( %* ) DO (
        SET "ESCAPED_ARG=%%~A"
        SET "ESCAPED_ARG=!ESCAPED_ARG:"=\"!"
        SET "ARGS=!ARGS! "!ESCAPED_ARG!""
    )

    pwsh -ExecutionPolicy RemoteSigned -File "%~dp0%~n0.ps1" !ARGS!

    ENDLOCAL
) ELSE (
    ECHO [Error] Missing PowerShell 7 or higher
    TIMEOUT /T -1
)
