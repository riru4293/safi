#Requires -Version 7
${ErrorActionPreference} = 'Stop'

${VER} = '7.0.21'
${PREFIX} = "${env:LOCALAPPDATA}\Programs\GlassFish"
${NAME} = "${VER}"
${DEST} = "${PREFIX}\${NAME}"
${GLASSFISH_HOME} = "${DEST}"
${SVC_NAME} = 'SAFI.GlassFish'
${SRC} = "https://mirror.kakao.com/eclipse/ee4j/glassfish/glassfish-${VER}.zip"
${SRC_HASH} = "Get it later"
${HASH_SRC} = "https://www.eclipse.org/downloads/sums.php?file=/ee4j/glassfish/glassfish-${VER}.zip&type=sha512"
${HASH_ALG} = 'SHA512' # [MD5|SHA1|SHA256|SHA512]
${ZIP} = "${env:TMP}\$( Split-Path -Path ${SRC} -Leaf )"
${ORIGIN_NAME} = "glassfish7"
${JAVA_HOME} = "${env:LOCALAPPDATA}\Programs\Java\primary"

${MARIA_CLIENT_VER} = '3.5.1'
${MARIA_CLIENT_SRC} = "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/${MARIA_CLIENT_VER}/mariadb-java-client-${MARIA_CLIENT_VER}.jar"

${SAFI_PASS} = 'Golden Hammer'
${STORE_PASS} = 'changeit'
${SVC_NAME} = 'SAFI.GlassFish'

# https://github.com/winsw/winsw/releases
${WSW_VER} = '2.12.0'
${WSW_NAME} = 'WinSW.NET461.exe'
${WSW_ASSETS} = "https://api.github.com/repos/winsw/winsw/releases/tags/v${WSW_VER}"
${WSW_SRC} = 'Get it later with GitHub API'
${WSW_SRC_HASH} = 'B5066B7BBDFBA1293E5D15CDA3CAAEA88FBEAB35BD5B38C41C913D492AADFC4F'.ToUpper()
${WSW_HASH_ALG} = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
${WSW_EXE} = "${env:TMP}\${WSW_NAME}"

# --------------------------------------------------


# To be running again as administrators if not administrators.
${u} = [Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()

if ( -Not( ${u} ).IsInRole( 'Administrators' ) ) {
  Start-Process -Verb RunAs -FilePath pwsh -ArgumentList @(
    '-ExecutionPolicy', 'RemoteSigned', '-File', "`"${PSCommandPath}`""
  )
  exit
}


# Remove an existing service
if ( ${s} = Get-Service -Name "${SVC_NAME}" -ErrorAction SilentlyContinue ) {
  if ( ${s}.Status -eq 'Running' ) {
    Stop-Service -Name "${SVC_NAME}"
  }

  Remove-Service -Name "${SVC_NAME}"
  Write-Host 'Removed existng service'
}


# Download GlassFish
${SRC_HASH} = Invoke-WebRequest -Uri "${HASH_SRC}" | ForEach-Object { ($_ -split ' ')[0] }

${d} = -Not( Test-Path "${ZIP}" ) -or -Not( "${SRC_HASH}" -eq ( Get-FileHash -Path "${ZIP}" -Algorithm "${HASH_ALG}" ).Hash )

if( ${d} ) {
  Write-Host "Downloading to ${ZIP} from ${SRC}"
  Invoke-WebRequest -Uri "${SRC}" -outfile "${ZIP}"
} else {
  Write-Host 'Skip download GlassFish. Because already exists.'
}


# Download WinSW
${r} = Invoke-RestMethod -Uri ${WSW_ASSETS} -Headers @{ 'User-Agent' = 'PowerShell' }
${a} = ${r}.assets | Where-Object { $_.name -eq ${WSW_NAME} }
${WSW_SRC} = ${a}.browser_download_url

${d} = -Not( Test-Path "${WSW_EXE}" ) `
-or -Not( "${WSW_SRC_HASH}" -eq ( Get-FileHash -Path "${WSW_EXE}" -Algorithm "${WSW_HASH_ALG}" ).Hash )

if( ${d} ) {
  Write-Host "Downloading to ${WSW_EXE} from ${WSW_SRC}"
  Invoke-WebRequest -Uri "${WSW_SRC}" -outfile "${WSW_EXE}" -Headers @{ 'User-Agent' = 'PowerShell' }
} else {
  Write-Host 'Skip download WinSW. Because already exists.'
}


# Extract GlassFish
Write-Output "Extracting to ${PREFIX} from ${ZIP}"
if( Test-Path "${PREFIX}\${ORIGIN_NAME}" ) { Remove-Item "${PREFIX}\${ORIGIN_NAME}" -Recurse -Force }
if( Test-Path "${DEST}" ) { Remove-Item "${DEST}" -Recurse -Force }
Expand-Archive -Path "${ZIP}" -DestinationPath "${PREFIX}"
Rename-Item -Path "${PREFIX}\${ORIGIN_NAME}" -NewName "${NAME}"


# Copy WinSW
New-Item -ItemType Directory -Path "${DEST}\glassfish\domains\domain1\service"
Write-Output "Copy to ${DEST}\glassfish\domains\domain1\service\glassfish_service.exe from ${WSW_EXE}"
Copy-Item -Path "${WSW_EXE}" -Destination "${DEST}\glassfish\domains\domain1\service\glassfish_service.exe"


# Download MariaDB Client
Invoke-WebRequest -Uri "${MARIA_CLIENT_SRC}" -outfile "${DEST}/glassfish/domains/domain1/lib/"


# Configure AS_JAVA(JAVA_HOME)
Add-Content -AsByteStream -Value ( [Text.Encoding]::UTF8.GetBytes( "SET AS_JAVA=`"${JAVA_HOME}`"" ) ) -Path "${DEST}/glassfish/config/asenv.bat"


# Create junctions
if( Test-Path "${PREFIX}\primary" ) { ( Get-Item "${PREFIX}\primary" ).Delete() }
New-Item -Path "${PREFIX}" -Name primary -Value "${DEST}" -ItemType Junction


# Install Service
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
do {
  Write-Host -NoNewLine '.'
  Start-Sleep -Seconds 1
  ${o} = ( & "${GLASSFISH_HOME}\bin\asadmin.bat" list-domains 2>&1 )
} while ( -Not ( ${o} | Out-String | Select-String -Pattern 'domain1 running' ) )
Write-Host 'Complete start domain1'

# Configure JDBC
& "${GLASSFISH_HOME}\bin\asadmin.bat" create-jdbc-connection-pool `
--datasourceclassname=org.mariadb.jdbc.MariaDbDataSource `
--restype=javax.sql.DataSource `
--property=$(Write-Output $(Join-String -Separator ':' -InputObject @(`
  'user=safi', `
  "password=${SAFI_PASS}", `
  'sessionVariables=innodb_lock_wait_timeout\=60', `
  'sslMode=verify-full', `
  "trustStore=file\:///$( ${env:LOCALAPPDATA}.Replace( '\', '/' ).Replace( ':', '\:' ) )/CA/mariadb-connector-cacerts", `
  'trustStoreType=pkcs12', `
  "trustStorePassword=${STORE_PASS}", `
  'URL=jdbc\:mariadb\://localhost/safi' `
))) `
SafiPool


& "${GLASSFISH_HOME}\bin\asadmin.bat" create-jdbc-resource --connectionpoolid SafiPool jdbc/safi


# Configure JVM options
& "${GLASSFISH_HOME}\bin\asadmin.bat" create-jvm-options "-Dsafi.home=$( ${env:LOCALAPPDATA}.Replace( '\', ',' ).Replace( ':', '\:' ) ),safi"


# Configure console log-level
Rename-Item -Path "${GLASSFISH_HOME}\glassfish\domains\domain1\config\logging.properties" -NewName "logging.properties.origin"

Get-Content "${GLASSFISH_HOME}\glassfish\domains\domain1\config\logging.properties.origin" `
| ForEach-Object { $_ -replace `
  'org.glassfish.main.jul.handler.SimpleLogHandler.level=INFO', `
  'org.glassfish.main.jul.handler.SimpleLogHandler.level=ALL' } `
| Set-Content -Path "${GLASSFISH_HOME}\glassfish\domains\domain1\config\logging.properties"


# Restart service
Restart-Service -Name "${SVC_NAME}"


Read-Host 'Press enter to exit'

# SIG # Begin signature block
# MIIGXAYJKoZIhvcNAQcCoIIGTTCCBkkCAQExCzAJBgUrDgMCGgUAMGkGCisGAQQB
# gjcCAQSgWzBZMDQGCisGAQQBgjcCAR4wJgIDAQAABBAfzDtgWUsITrck0sYpfvNR
# AgEAAgEAAgEAAgEAAgEAMCEwCQYFKw4DAhoFAAQUk58c4/sqjtKGt5j6BK4iajss
# tNKgggPPMIIDyzCCArOgAwIBAgIBBjANBgkqhkiG9w0BAQsFADBRMQswCQYDVQQG
# EwJKUDEOMAwGA1UECAwFT3Nha2ExEjAQBgNVBAoMCVByb2plY3QtSzEeMBwGA1UE
# AwwVY2EucHJvamVjdC1rLm15ZG5zLmpwMB4XDTI1MDEwMjEzNDcxN1oXDTI3MDky
# OTEzNDcxN1owYzELMAkGA1UEBhMCSlAxDjAMBgNVBAgMBU9zYWthMRIwEAYDVQQK
# DAlQcm9qZWN0LUsxDTALBgNVBAMMBHJpcnUxITAfBgkqhkiG9w0BCQEWEnJpcnU0
# MjkzQGdtYWlsLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAOIR
# PFXMzPTz21pfPjUIs2PJB1WQGDr4+dCCk3j17EFQKx3pZJObLXBtdjAwbqxZJnF/
# DoyUpsCcVHLwqI7t7+lfsUiq7/MTRFIhJBve5oKRCGK3fYDe2+XS1fY5yFjJmWmm
# 8eRI7gSJHi9IKCJRzZ3aZ59EmyI5YhefnstYzKh5ro/FtHF8JgJNix5lWRQfsqo8
# TbJcLNg6nYGmBuKsTeggVmNYi5SS1z662y69kaxX/hWanQCT8bs7PwBKfxNwjOVD
# C7XVd+lGB51eCfUdLOqgvm5OfKnYw8hb8Yac+NBH82jJGkZD/cdxovT+YGojXUUX
# IWjX67+Ux25/TYKnFGUCAwEAAaOBmzCBmDAdBgNVHQ4EFgQU1vJRZwN7hBv36Z8K
# kqO2qHYYMiMwCQYDVR0TBAIwADALBgNVHQ8EBAMCB4AwKwYDVR0lBCQwIgYIKwYB
# BQUHAwMGCisGAQQBgjcCARUGCisGAQQBgjcCARYwEQYJYIZIAYb4QgEBBAQDAgSw
# MB8GA1UdIwQYMBaAFLACqQ8B931Vg2H0tDbKTgnZ10bqMA0GCSqGSIb3DQEBCwUA
# A4IBAQBgBIWW+P8OYjQqQCxauzqKZhALGNAT6mwIcsX7NIitJ7fYA1gH5JeDBCH4
# GbcehR5sXA7r75xwhGJ7RcH2Lu1jQE/WjBCS1IhZQECP8zPnH27EZ1kYQZZRap30
# jJU8XgYeyLeiifnRHx+uTplE+zsMp4hZlrBWVdnQBZRwu8FZPPfq9wIYjhe3sX8g
# g1MIlW5nwzBwxA6tAo8S7/OWsMKA2kwxR2GebGpg5cYyAfjyCLb9d4T7eMwzBnbe
# 876hMv536wdOnelAN4PmUSORV2BaahYODLaA/Ko4W2PgQuL7CF5WMyRy2mMyogT6
# lw82ZMtMH6KAu9RfPECBfaYzKBJvMYIB9zCCAfMCAQEwVjBRMQswCQYDVQQGEwJK
# UDEOMAwGA1UECAwFT3Nha2ExEjAQBgNVBAoMCVByb2plY3QtSzEeMBwGA1UEAwwV
# Y2EucHJvamVjdC1rLm15ZG5zLmpwAgEGMAkGBSsOAwIaBQCgeDAYBgorBgEEAYI3
# AgEMMQowCKACgAChAoAAMBkGCSqGSIb3DQEJAzEMBgorBgEEAYI3AgEEMBwGCisG
# AQQBgjcCAQsxDjAMBgorBgEEAYI3AgEWMCMGCSqGSIb3DQEJBDEWBBTIxcfClO8C
# ee4G+qpHNXZLP2d2QDANBgkqhkiG9w0BAQEFAASCAQC/FlA/GGlV72JtYbW+zY8R
# 92W8vrWJTgFSLrm1ueXqPRjBjUBGsG0Sn+2vZsA9W9aqAysFc9YxvrsYwJdaGFJP
# hP9b3LPzFI8rvD3Jx9LeI9N08Jw0NQYp+nhS4KiJvqXxishwMSOsGO90uHa73mAw
# JdJqBKZNyzbkBC/v1Bov0+KQVggKutIPNk9sqbj3MTEb+AzRpdAVx04/KloGPoEJ
# HaUSb57ALSOEV5KIHpxm24NJ9A7pEjeuz02MX8Pn/tgOyL3QdumRKxBWVMJWoxYw
# rIJSFWlGOtSV4x/bpHk10B+kSAcvjNFO6w2zuoO93pnVmdbQKPg19DSnR6c7wXeK
# SIG # End signature block
