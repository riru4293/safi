#Requires -Version 7
${ErrorActionPreference} = 'Stop'

${LOCAL_ROOT} = "${env:USERPROFILE}\local"
${JAVA_HOME} = "${LOCAL_ROOT}\opt\Java\jdk21"

${MAJOR_VER} = 8
${MINOR_VER} = 0
${PATCH_VER} = 0

${VER} = "${MAJOR_VER}.${MINOR_VER}.${PATCH_VER}"
# ${SRC} = "https://download.eclipse.org/ee4j/glassfish/glassfish-${VER}.zip" # Note: Official but slow.
${SRC} = "https://repo1.maven.org/maven2/org/glassfish/main/distributions/glassfish/${VER}/glassfish-${VER}.zip"
${ZIP} = "${env:TMP}\$( Split-Path -Path ${SRC} -Leaf )" # Note: Extracts a file name.
${ORIGIN_NAME} = "glassfish${MAJOR_VER}" # Note: Folder name in zip.

${PREFIX} = "${LOCAL_ROOT}\opt\GlassFish"
${NAME} = "${VER}"
${DEST} = "${PREFIX}\${NAME}"

${SVC_NAME} = 'SAFI_GLASSFISH'
${GLASSFISH_HOME} = "${DEST}"

# MariaDB Connector/J
${MARIACONN_VER} = '3.5.7'
${MARIACONN_SRC} = "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/${MARIACONN_VER}/mariadb-java-client-${MARIACONN_VER}.jar"

${SAFI_PASS} = 'Golden Hammer'
${STORE_PASS} = 'changeit'

# https://github.com/winsw/winsw/releases
${WSW_VER} = '2.12.0'
${WSW_ASSETS_SRC} = "https://api.github.com/repos/winsw/winsw/releases/tags/v${WSW_VER}"
${WSW_ASSET_NAME} = 'WinSW.NET461.exe'
${WSW_SRC} = 'Get it later with GitHub API'
${WSW_EXE} = "${env:TMP}\WinSW_NET461_${WSW_VER}.exe"

# --------------------------------------------------


# To be running again as administrators if not administrators.
${u} = [Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()

if ( -Not( ${u} ).IsInRole( 'Administrators' ) ) {
  Start-Process -Verb RunAs -FilePath pwsh -ArgumentList @(
    '-ExecutionPolicy', 'RemoteSigned', '-File', "`"${PSCommandPath}`""
  )
  exit
}


# Remove an existing service.
if ( ${s} = Get-Service -Name "${SVC_NAME}" -ErrorAction SilentlyContinue ) {
    if ( ${s}.Status -eq 'Running' ) {
        Write-Host '-----'
        Write-Host 'Stop exists service.'
        Stop-Service -Name "${SVC_NAME}"
    }

    Write-Host '-----'
    Write-Host 'Remove exists service.'
    Remove-Service -Name "${SVC_NAME}"
}


# Download GlassFish
Write-Host '-----'
Write-Host 'Download GlassFish'
Write-Host "source: ${SRC}"
Write-Host "destination ${ZIP}"
Invoke-WebRequest -Uri "${SRC}" -outfile "${ZIP}"


# Download WinSW
Write-Host '-----'
Write-Host 'Download WinSW'
${r} = Invoke-RestMethod -Uri ${WSW_ASSETS_SRC} -Headers @{ 'User-Agent' = 'PowerShell' }
${a} = ${r}.assets | Where-Object { $_.name -eq ${WSW_ASSET_NAME} }
${WSW_SRC} = ${a}.browser_download_url

Write-Host "source: ${WSW_SRC}"
Write-Host "destination ${WSW_EXE}"
Invoke-WebRequest -Uri "${WSW_SRC}" -outfile "${WSW_EXE}" -Headers @{ 'User-Agent' = 'PowerShell' }


# Extract GlassFish
Write-Host '-----'
Write-Host 'Extract GlassFish'
Write-Output "source: ${ZIP}"
Write-Output "destination: ${PREFIX}"
if( Test-Path "${PREFIX}\${ORIGIN_NAME}" ) { Remove-Item "${PREFIX}\${ORIGIN_NAME}" -Recurse -Force }
if( Test-Path "${DEST}" ) { Remove-Item "${DEST}" -Recurse -Force }
Expand-Archive -Path "${ZIP}" -DestinationPath "${PREFIX}"
Rename-Item -Path "${PREFIX}\${ORIGIN_NAME}" -NewName "${NAME}"


# Copy WinSW
Write-Host '-----'
Write-Host 'Copy WinSW'
New-Item -ItemType Directory -Path "${DEST}\glassfish\domains\domain1\service"
Write-Output "Copy to ${DEST}\glassfish\domains\domain1\service\glassfish_service.exe from ${WSW_EXE}"
Copy-Item -Path "${WSW_EXE}" -Destination "${DEST}\glassfish\domains\domain1\service\glassfish_service.exe"


# Download MariaDB Connector/J
Write-Host '-----'
Write-Host 'Download MariaDB Connector/J'
Write-Host "source: ${MARIACONN_SRC}"
Write-Host "destination ${DEST}/glassfish/domains/domain1/lib\$( Split-Path -Path ${MARIACONN_SRC} -Leaf )"
Invoke-WebRequest -Uri "${MARIACONN_SRC}" -outfile "${DEST}/glassfish/domains/domain1/lib/"


# Configure AS_JAVA(JAVA_HOME)
Write-Host '-----'
Write-Host 'Configure AS_JAVA'
Write-Host "AS_JAVA=${JAVA_HOME}"
Add-Content -AsByteStream -Value ( [Text.Encoding]::UTF8.GetBytes( "SET `"AS_JAVA=${JAVA_HOME}`"" ) ) -Path "${DEST}/glassfish/config/asenv.bat"


# Create junctions
Write-Host '-----'
Write-Host 'Create junction to GlassFish'
Write-Host "${PREFIX}\primary=${PREFIX}\${DEST}"
if( Test-Path "${PREFIX}\primary" ) { ( Get-Item "${PREFIX}\primary" ).Delete() }
New-Item -Path "${PREFIX}" -Name primary -Value "${DEST}" -ItemType Junction


# Install Service
Write-Host '-----'
Write-Host "Install service as ${SVC_NAME}"
@"
<service>
  <id>${SVC_NAME}</id>
  <name>${SVC_NAME}</name>
  <startmode>Manual</startmode>
  <description>GlassFish Server for SAFI</description>
  <executable>$( ${GLASSFISH_HOME}.Replace( '\', '/' ) )/glassfish/lib/nadmin.bat</executable>
  <logpath>$( ${GLASSFISH_HOME}.Replace( '\', '\\' ) )\\glassfish\\domains\\domain1\\service</logpath>
  <logmode>reset</logmode>
  <depend>tcpip</depend>
  <startargument>start-domain</startargument>
  <startargument>--watchdog</startargument>
  <startargument>--domaindir</startargument>
  <startargument>$( ${GLASSFISH_HOME}.Replace( '\', '\\' ) )\\glassfish\\domains</startargument>
  <startargument>domain1</startargument>
  <stopargument>stop-domain</stopargument>
  <stopargument>--domaindir</stopargument>
  <stopargument>$( ${GLASSFISH_HOME}.Replace( '\', '\\' ) )\\glassfish\\domains</stopargument>
  <stopargument>domain1</stopargument>
</service>
"@ | Out-File -enc default "${DEST}\glassfish\domains\domain1\service\glassfish_service.xml"

& "${DEST}\glassfish\domains\domain1\service\glassfish_service.exe" install

Get-Service -Name "${SVC_NAME}" `
| ForEach-Object { Start-Service -InputObject $_ } `
| ForEach-Object { $_.WaitForStatus( 'Running', '00:02:00' ) }


# Wait GlassFish domain1 running
Write-Host '-----'
Write-Host 'Wait GlassFish domain1 running'
do {
  Write-Host -NoNewLine '.'
  Start-Sleep -Seconds 1
  ${o} = ( & "${GLASSFISH_HOME}\bin\asadmin.bat" list-domains 2>&1 )
} while ( -Not ( ${o} | Out-String | Select-String -Pattern 'domain1 running' ) )
Write-Host 'Complete start domain1'


# Configure JDBC
Write-Host '-----'
Write-Host 'Configure JDBC'
& "${GLASSFISH_HOME}\bin\asadmin.bat" create-jdbc-connection-pool `
--datasourceclassname=org.mariadb.jdbc.MariaDbDataSource `
--restype=javax.sql.DataSource `
--property=$(Write-Output $(Join-String -Separator ':' -InputObject @(`
  'user=safi', `
  "password=${SAFI_PASS}", `
  'sessionVariables=innodb_lock_wait_timeout\=60', `
  'sslMode=verify-full', `
  "trustStore=file\:///$( ${LOCAL_ROOT}.Replace( '\', '/' ).Replace( ':', '\:' ) )/etc/tls/mariadb-connector-cacerts", `
  'trustStoreType=pkcs12', `
  "trustStorePassword=${STORE_PASS}", `
  'URL=jdbc\:mariadb\://localhost/safi?serverTimezone\=UTC' `
))) `
SafiPool

& "${GLASSFISH_HOME}\bin\asadmin.bat" set "resources.jdbc-connection-pool.SafiPool.statement-timeout-in-seconds=300"
& "${GLASSFISH_HOME}\bin\asadmin.bat" set "resources.jdbc-connection-pool.SafiPool.statement-cache-size=20"
& "${GLASSFISH_HOME}\bin\asadmin.bat" set "resources.jdbc-connection-pool.SafiPool.validation-table-name=DUAL"
& "${GLASSFISH_HOME}\bin\asadmin.bat" set "resources.jdbc-connection-pool.SafiPool.connection-validation-method=table"
& "${GLASSFISH_HOME}\bin\asadmin.bat" set "resources.jdbc-connection-pool.SafiPool.is-connection-validation-required=true"
& "${GLASSFISH_HOME}\bin\asadmin.bat" set "resources.jdbc-connection-pool.SafiPool.fail-all-connections=true"
& "${GLASSFISH_HOME}\bin\asadmin.bat" set "resources.jdbc-connection-pool.SafiPool.statement-cache-type="

& "${GLASSFISH_HOME}\bin\asadmin.bat" create-jdbc-resource --connectionpoolid SafiPool jdbc/safi


# Configure Logging
Write-Host '-----'
Write-Host 'Configure Logging'
& "${GLASSFISH_HOME}\bin\asadmin.bat" set-log-levels "jp.mydns.projectk.safi=FINEST"


# Configure JVM options
Write-Host '-----'
Write-Host 'Configure JVM options'
& "${GLASSFISH_HOME}\bin\asadmin.bat" create-jvm-options "-Dsafi.home=$( ${LOCAL_ROOT}.Replace( '\', ',' ).Replace( ':', '\:' ) ),var,safi"
& "${GLASSFISH_HOME}\bin\asadmin.bat" delete-jvm-options "-Xmx512m"
& "${GLASSFISH_HOME}\bin\asadmin.bat" create-jvm-options "-Xmx256m"


# Configura deployment
Write-Host '-----'
Write-Host 'Configura deployment'
& "${GLASSFISH_HOME}\bin\asadmin.bat" set "server.admin-service.das-config.autodeploy-enabled=false"
& "${GLASSFISH_HOME}\bin\asadmin.bat" set "server.admin-service.das-config.dynamic-reload-enabled=false"


# Configure console log-level
Write-Host '-----'
Write-Host 'Configure console log-level'
Rename-Item -Path "${GLASSFISH_HOME}\glassfish\domains\domain1\config\logging.properties" -NewName "logging.properties.origin"

Get-Content "${GLASSFISH_HOME}\glassfish\domains\domain1\config\logging.properties.origin" `
| ForEach-Object { $_ -replace `
  'org.glassfish.main.jul.handler.SimpleLogHandler.level=INFO', `
  'org.glassfish.main.jul.handler.SimpleLogHandler.level=ALL' } `
| Set-Content -Path "${GLASSFISH_HOME}\glassfish\domains\domain1\config\logging.properties"


# Restart service
Write-Host '-----'
Write-Host 'Restart GlassFish'
Restart-Service -Name "${SVC_NAME}"

Write-Host '-----'
Write-Host 'Completed!'
Write-Host '-----'
Read-Host 'Press enter to exit'


# SIG # Begin signature block
# MIIGgQYJKoZIhvcNAQcCoIIGcjCCBm4CAQExDzANBglghkgBZQMEAgEFADB5Bgor
# BgEEAYI3AgEEoGswaTA0BgorBgEEAYI3AgEeMCYCAwEAAAQQH8w7YFlLCE63JNLG
# KX7zUQIBAAIBAAIBAAIBAAIBADAxMA0GCWCGSAFlAwQCAQUABCCS3gP6yY12oiwW
# SjUrovby35Vg6DhpBTI5kcbt8skJFaCCA88wggPLMIICs6ADAgECAgEGMA0GCSqG
# SIb3DQEBCwUAMFExCzAJBgNVBAYTAkpQMQ4wDAYDVQQIDAVPc2FrYTESMBAGA1UE
# CgwJUHJvamVjdC1LMR4wHAYDVQQDDBVjYS5wcm9qZWN0LWsubXlkbnMuanAwHhcN
# MjUwMTAyMTM0NzE3WhcNMjcwOTI5MTM0NzE3WjBjMQswCQYDVQQGEwJKUDEOMAwG
# A1UECAwFT3Nha2ExEjAQBgNVBAoMCVByb2plY3QtSzENMAsGA1UEAwwEcmlydTEh
# MB8GCSqGSIb3DQEJARYScmlydTQyOTNAZ21haWwuY29tMIIBIjANBgkqhkiG9w0B
# AQEFAAOCAQ8AMIIBCgKCAQEA4hE8VczM9PPbWl8+NQizY8kHVZAYOvj50IKTePXs
# QVArHelkk5stcG12MDBurFkmcX8OjJSmwJxUcvCoju3v6V+xSKrv8xNEUiEkG97m
# gpEIYrd9gN7b5dLV9jnIWMmZaabx5EjuBIkeL0goIlHNndpnn0SbIjliF5+ey1jM
# qHmuj8W0cXwmAk2LHmVZFB+yqjxNslws2DqdgaYG4qxN6CBWY1iLlJLXPrrbLr2R
# rFf+FZqdAJPxuzs/AEp/E3CM5UMLtdV36UYHnV4J9R0s6qC+bk58qdjDyFvxhpz4
# 0EfzaMkaRkP9x3Gi9P5gaiNdRRchaNfrv5THbn9NgqcUZQIDAQABo4GbMIGYMB0G
# A1UdDgQWBBTW8lFnA3uEG/fpnwqSo7aodhgyIzAJBgNVHRMEAjAAMAsGA1UdDwQE
# AwIHgDArBgNVHSUEJDAiBggrBgEFBQcDAwYKKwYBBAGCNwIBFQYKKwYBBAGCNwIB
# FjARBglghkgBhvhCAQEEBAMCBLAwHwYDVR0jBBgwFoAUsAKpDwH3fVWDYfS0NspO
# CdnXRuowDQYJKoZIhvcNAQELBQADggEBAGAEhZb4/w5iNCpALFq7OopmEAsY0BPq
# bAhyxfs0iK0nt9gDWAfkl4MEIfgZtx6FHmxcDuvvnHCEYntFwfYu7WNAT9aMEJLU
# iFlAQI/zM+cfbsRnWRhBllFqnfSMlTxeBh7It6KJ+dEfH65OmUT7OwyniFmWsFZV
# 2dAFlHC7wVk89+r3AhiOF7exfyCDUwiVbmfDMHDEDq0CjxLv85awwoDaTDFHYZ5s
# amDlxjIB+PIItv13hPt4zDMGdt7zvqEy/nfrB06d6UA3g+ZRI5FXYFpqFg4MtoD8
# qjhbY+BC4vsIXlYzJHLaYzKiBPqXDzZky0wfooC71F88QIF9pjMoEm8xggIIMIIC
# BAIBATBWMFExCzAJBgNVBAYTAkpQMQ4wDAYDVQQIDAVPc2FrYTESMBAGA1UECgwJ
# UHJvamVjdC1LMR4wHAYDVQQDDBVjYS5wcm9qZWN0LWsubXlkbnMuanACAQYwDQYJ
# YIZIAWUDBAIBBQCggYQwGAYKKwYBBAGCNwIBDDEKMAigAoAAoQKAADAZBgkqhkiG
# 9w0BCQMxDAYKKwYBBAGCNwIBBDAcBgorBgEEAYI3AgELMQ4wDAYKKwYBBAGCNwIB
# FjAvBgkqhkiG9w0BCQQxIgQg7uIhayUlfz1TebUfg8e43gEbaEfh6b/xHK473vO9
# +rYwDQYJKoZIhvcNAQEBBQAEggEAXHe99IPEz5xB4nB8Rasnva5xDXxlqCWRBXuX
# rN/fpli/m8GxBD9JrV+PUYOZMIR1PCaLYCv6TDh34CDbCPIe4TfAyj2CYE/uGji+
# tRbSAEHo+lJkvl18m/1Ud/Uddl901YnbFkPFzPsY06Mn82gn8vstA0fzQipwDfWY
# uU7hw8Zl3G968f9t/RE9/PPV6uVU6k0RIczc3quUFNGd65nMeERq8QLRQor6ajuV
# /FgremyU21syubk3Y7wrjc6uoD+bqi76gIH8FTEaWGPPLls46udE9zTME3zZULdV
# trE3LYDZVcrtpoxnji0pdhPuywlf289Y+eA3FPlJy4H9/kEugw==
# SIG # End signature block
