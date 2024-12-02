OPENFILES > NUL 2>&1
IF ERRORLEVEL 1 (
    @powershell -NoProfile -ExecutionPolicy RemoteSigned -Command "Start-Process -FilePath '%~0' -Verb runas" -Wait
    GOTO END
)


SET "ERRORLEVEL="
SET "SVC_NAME=SAFI.MariaDB"
SET "MARIA_HOME=%LOCALAPPDATA%\Programs\MariaDB\primary"
SET "PATH=%MARIA_HOME%\bin;%PATH%"
SET "TDE_KEYS=%TMP%\MariaDB-Key.%DATE:/=-%"

ECHO Delete service.
ECHO ----------
SC QUERY "%SVC_NAME%" | FIND /I "STATE" | FIND "STOPPED" > NUL || NET STOP "%SVC_NAME%" & SC DELETE "%SVC_NAME%"

ECHO Install software.
ECHO ----------
@powershell -ExecutionPolicy RemoteSigned -File "%~dp0%~n0.ps1"


ECHO Initial data creation.
ECHO ----------
IF EXIST "%MARIA_HOME%\data" RMDIR /Q /S "%MARIA_HOME%\data"
mysql_install_db
IF ERRORLEVEL 1 GOTO ERROR


ECHO Put TDE-keys.
ECHO ----------
COPY /Y "%TDE_KEYS%\tdekeys.enc" "%MARIA_HOME%\tdekeys.enc"
COPY /Y "%TDE_KEYS%\tdekeys.key" "%MARIA_HOME%\tdekeys.key"


ECHO Install service.
ECHO ----------
mysqld --install "%SVC_NAME%" && NET START "%SVC_NAME%"
IF ERRORLEVEL 1 GOTO ERROR


ECHO Import timezone.
ECHO ----------
mariadb --user=root --database=mysql < "%MARIA_HOME%\zoneinfo.sql"


ECHO Generate my.ini.
ECHO ----------
@powershell -ExecutionPolicy RemoteSigned -File "%~dp0%gen-my-ini.ps1"


ECHO Restart service.
ECHO ----------
sc query "%SVC_NAME%" | find /I "STATE" | find "STOPPED" > nul || NET STOP "%SVC_NAME%" & NET START "%SVC_NAME%"
IF ERRORLEVEL 1 GOTO ERROR


ECHO Confirm timezone.
ECHO ----------
ECHO SELECT NOW(); | mariadb --user=root

ECHO Complete
TIMEOUT /T 100


:END
EXIT /B %ERRORLEVEL%


:ERROR
ECHO Occurs error!!
TIMEOUT /T -1
EXIT /B %ERRORLEVEL%
