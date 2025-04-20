#Requires -Version 7
${ErrorActionPreference} = 'Stop'

# MariaDB
${PREFIX} = "${env:LOCALAPPDATA}\Programs\MariaDB"
${VER} = '11.7.2'
${ORIGIN_NAME} = "mariadb-${VER}-winx64"
${NAME} = "${VER}"
${DEST} = "${PREFIX}\${NAME}"
${MARIA_HOME} = "${DEST}"
${SVC_NAME} = 'SAFI.MariaDB'
${SRC} = "https://mirrors.xtom.jp/mariadb//mariadb-${VER}/winx64-packages/mariadb-${VER}-winx64.zip"
${SRC_HASH} = 'FDA36DC30BD10979BA1496D08C2AEE4549833A09BF26AA914B653AB792A9ACBE'
${HASH_ALG} = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
${ZIP} = "${env:TMP}\$( Split-Path -Path ${SRC} -Leaf )"

# Time zone
# https://mirror.mariadb.org/zoneinfo/
${TZ_SRC} = "https://mirrors.xtom.jp/mariadb/zoneinfo/zoneinfo.zip"
${TZ_SRC_HASH} = '2bac8646c92314f8d6c30a0e05bce8355091a7bbb0b6b7b058c9363378c9597a'
${TZ_HASH_ALG} = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
${TZ_ZIP} = "${env:TMP}\$( Split-Path -Path ${TZ_SRC} -Leaf )"
${TZ_SQL} = "${DEST}\zoneinfo.sql"

# OpenSSL configuration
${OPENSSL_PATH} = "${env:LOCALAPPDATA}\Programs\OpenSSL\primary\$( [System.Environment]::Is64BitProcess ? 'x64' : 'x86' )\bin"

# CA configuration
${ORG} = 'Project-k'
${CA_HOME} = "${env:LOCALAPPDATA}\CA"
${CA_CERT_NAME} = "ca.$(${ORG}.toLower())"
${SV_CERT_NAME} = "$( ${env:COMPUTERNAME}.ToLower() ).$(${ORG}.toLower())"

# Environment variables
${env:PATH} = "${MARIA_HOME}\bin" + ';' + "${OPENSSL_PATH}" + ';' + "${env:PATH}"

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


# Download MariaDB
${d} = -Not( Test-Path "${ZIP}" ) -or -Not( "${SRC_HASH}" -eq ( Get-FileHash -Path "${ZIP}" -Algorithm "${HASH_ALG}" ).Hash )

if( ${d} ) {
  Write-Host "Downloading to ${ZIP} from ${SRC}"
  Invoke-WebRequest -Uri "${SRC}" -outfile "${ZIP}"
} else {
  Write-Host 'Skip download MariaDB. Because already exists.'
}


# Download time zones
${d} = -Not( Test-Path "${TZ_ZIP}" ) `
-or -Not( "${TZ_SRC_HASH}" -eq ( Get-FileHash -Path "${TZ_ZIP}" -Algorithm "${TZ_HASH_ALG}" ).Hash )

if( ${d} ) {
  Write-Host "Downloading to ${TZ_ZIP} from ${TZ_SRC}"
  Invoke-WebRequest -Uri "${TZ_SRC}" -outfile "${TZ_ZIP}"
} else {
  Write-Host 'Skip download time zones. Because already exists.'
}


# Extract MariaDB
Write-Host "Extracting to ${PREFIX} from ${ZIP}"
if( Test-Path "${PREFIX}\${ORIGIN_NAME}" ) { Remove-Item "${PREFIX}\${ORIGIN_NAME}" -Recurse -Force }
if( Test-Path "${DEST}" ) { Remove-Item "${DEST}" -Recurse -Force }
Expand-Archive -Path "${ZIP}" -DestinationPath "${PREFIX}"
Rename-Item -Path "${PREFIX}\${ORIGIN_NAME}" -NewName "${NAME}"


# Extract time zones
Write-Host "Extracting to ${DEST} from ${TZ_ZIP}"
if( Test-Path "${TZ_SQL}" ) { Remove-Item "${TZ_SQL}" -Force }
Expand-Archive -Path "${TZ_ZIP}" -DestinationPath "${DEST}"


# Create junctions
Write-Host "Create junction ${PREFIX}\primary to ${DEST}"
if( Test-Path "${PREFIX}\primary" ) { ( Get-Item "${PREFIX}\primary" ).Delete() }
New-Item -Path "${PREFIX}" -Name primary -Value "${DEST}" -ItemType Junction


# Initial data creation
Write-Host "Initial data creation to ${MARIA_HOME}\data"
if( Test-Path "${MARIA_HOME}\data" ) { Remove-Item "${MARIA_HOME}\data" -Recurse -Force }
mysql_install_db


# Generate TDE keys
Write-Host 'Generate TDE keys'
if( Test-Path "${MARIA_HOME}\tdekeys.txt" ) { Remove-Item -Path "${MARIA_HOME}\tdekeys.txt" }

foreach  (${i} in 1..9 ) {
  ${pi} = New-Object System.Diagnostics.ProcessStartInfo
  ${pi}.FileName = 'openssl'
  ${pi}.Arguments = 'rand -hex 32'
  ${pi}.RedirectStandardOutput = $true
  ${pi}.UseShellExecute = $false

  ${p} = New-Object System.Diagnostics.Process
  ${p}.StartInfo = ${pi}
  ${p}.Start() | Out-Null

  ${r} = ${p}.StandardOutput.ReadToEnd()
  ${p}.WaitForExit()

  Add-Content -AsByteStream -Value ( [Text.Encoding]::UTF8.GetBytes( "${i};${r}" ) ) -Path "${MARIA_HOME}\tdekeys.txt"
}


# Generate TDE keys password
${pi} = New-Object System.Diagnostics.ProcessStartInfo
${pi}.FileName = 'openssl'
${pi}.Arguments = 'rand -hex 128'
${pi}.RedirectStandardOutput = $true
${pi}.UseShellExecute = $false

${p} = New-Object System.Diagnostics.Process
${p}.StartInfo = ${pi}
${p}.Start() | Out-Null

${r} = ${p}.StandardOutput.ReadToEnd()
${p}.WaitForExit()

Set-Content -Value ( [Text.Encoding]::UTF8.GetBytes( "${r}" ) ) -AsByteStream -Path "${MARIA_HOME}\tdekeys.key"


# Encode TDE keys
openssl enc -aes-256-cbc -md sha1 -pass file:"${MARIA_HOME}\tdekeys.key" -in "${MARIA_HOME}\tdekeys.txt" `
-out "${MARIA_HOME}\tdekeys.enc" > $null 2>&1


# Remove raw TDE keys
Get-Content "${MARIA_HOME}\tdekeys.txt"
Remove-Item -Path "${MARIA_HOME}\tdekeys.txt"


# Install service
Write-Host "Register a service under the name ${SVC_NAME}"

mysqld --install "${SVC_NAME}"

Get-Service -Name "${SVC_NAME}" `
| ForEach-Object { Start-Service -InputObject $_ } `
| ForEach-Object { $_.WaitForStatus( 'Running', '00:00:30' ) }


# Import timezone.
${pi} = New-Object System.Diagnostics.ProcessStartInfo
${pi}.FileName = 'mariadb'
${pi}.Arguments = '--user=root --database=mysql'
${pi}.RedirectStandardInput = $true
${pi}.UseShellExecute = $false

${p} = New-Object System.Diagnostics.Process
${p}.StartInfo = ${pi}
${p}.Start() | Out-Null
${p}.StandardInput.WriteLine( $( Get-Content -Path "${MARIA_HOME}\zoneinfo.sql" -Raw ) )
${p}.StandardInput.Close()
${p}.WaitForExit()


# Generate my.ini
@"
[mysqld]
transaction_isolation = READ-COMMITTED
default-time-zone='GMT'

## TLS
#
ssl-ca   = "$( ${CA_HOME}.Replace( '\', '/' ) )/certs/${CA_CERT_NAME}.crt"
ssl-key  = "$( ${CA_HOME}.Replace( '\', '/' ) )/private/${SV_CERT_NAME}.key"
ssl-cert = "$( ${CA_HOME}.Replace( '\', '/' ) )/certs/${SV_CERT_NAME}.crt"

## timeout
#
wait_timeout=1800
interactive_timeout=1800


## innodb
#
innodb_rollback_on_timeout = TRUE

## charset
#
character-set-server=utf8mb4
collation-server=utf8mb4_general_ci

## encryption
#
plugin_load_add = file_key_management.dll
file_key_management = FORCE_PLUS_PERMANENT
file_key_management_filename = $( ${MARIA_HOME}.Replace( '\', '/' ) )/tdekeys.enc
file_key_management_filekey = FILE:$( ${MARIA_HOME}.Replace( '\', '/' ) )/tdekeys.key
file_key_management_encryption_algorithm = AES_CBC

innodb_encrypt_tables = FORCE
innodb_encrypt_log = ON
innodb_encrypt_temporary_tables = ON
innodb_encryption_threads = 4
innodb_encryption_rotate_key_age = 1

encrypt_tmp_disk_tables = ON
encrypt_tmp_files = ON
encrypt_binlog = ON


[client]
default-character-set = utf8mb4
"@ ` | ForEach-Object{ [Text.Encoding]::UTF8.GetBytes( $_ + "`n" ) } `
| Add-Content -AsByteStream -Path "${MARIA_HOME}\data\my.ini"


# Restart service
Restart-Service -Name "${SVC_NAME}"


# Confirm time zone
Write-Output 'SELECT NOW();' | mariadb --user=root

Read-Host 'Press enter to exit'

# SIG # Begin signature block
# MIIGXAYJKoZIhvcNAQcCoIIGTTCCBkkCAQExCzAJBgUrDgMCGgUAMGkGCisGAQQB
# gjcCAQSgWzBZMDQGCisGAQQBgjcCAR4wJgIDAQAABBAfzDtgWUsITrck0sYpfvNR
# AgEAAgEAAgEAAgEAAgEAMCEwCQYFKw4DAhoFAAQUxYNjpCZ9vovlCcDElNXpjRMs
# pYugggPPMIIDyzCCArOgAwIBAgIBBjANBgkqhkiG9w0BAQsFADBRMQswCQYDVQQG
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
# AQQBgjcCAQsxDjAMBgorBgEEAYI3AgEWMCMGCSqGSIb3DQEJBDEWBBQwAtcs2ECt
# maUQ5k1TOXVyYH3cvTANBgkqhkiG9w0BAQEFAASCAQCh/U54CQjorVUAg4Wbjmtn
# fC5PhUw0Z98dKxDic1vsMKvBLHaDlXwzyOX/bCjhg3YXcFFwfbVJjg5h7ugJUjC6
# XAh/0WLfYS7CIgg/22hP1/Dcch7b6T5P/iRFZZLb2NpcJhxhBdR7kg+ml8Dfd90A
# Z326Dbv6kr3LC/zzXSrKW3wMllwDZ7L0v63vymzwcA+TxWJGzcO2ntRnXSbR6Z9L
# vrZdytDkVZwFSG0jjhjwceCVOYhCbePXMc+K3rNmuOgth53aY3jd9H9tbTVqgL19
# pvZ7kRF1etymKbBptZEJZt/dvVdRgLbOvtR293AolwixlZsRPti+PUA7hu17s66S
# SIG # End signature block
