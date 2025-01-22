#Requires -Version 7
Param(
  [switch]${WithCertCreation},
  [switch]${WithoutOpensslInstallation}
)

${ErrorActionPreference} = 'Continue'

# OpenSSL configuration
${PREFIX} = "${env:LOCALAPPDATA}\Programs\OpenSSL"
${VER} = '3.4.0'
${DEST} = "${PREFIX}\${VER}"

${OPENSSL_HOME} = "${DEST}"
${OPENSSL_ORIGIN_CONF} = "${OPENSSL_HOME}\ssl\openssl.cnf"

# See: https://kb.firedaemon.com/support/solutions/articles/4000121705
${SRC} = "https://download.firedaemon.com/FireDaemon-OpenSSL/openssl-${VER}.zip"
${ZIP} = "${env:TMP}\$( Split-Path -Path ${SRC} -Leaf )"
${SRC_HASH} = 'FFF7C56C1588644A609F63F60FAD15275E2CE6FFC310C320CDCB72554449CCA5'.ToUpper()
${HASH_ALG} = 'SHA256' # [MD5|SHA1|SHA256|SHA512]

# Certificate configuration
${COUNTRY} = 'JP'
${STATION} = 'Osaka'
${ORG} = 'project-k'
${DOMAIN} = 'project-k.mydns.jp'

# CA configuration
${CA_CONF} = "${env:CA_HOME}\openssl.ca.cnf"
${CA_SUBJ} = "/C=${COUNTRY}/ST=${STATION}/O=${ORG}/CN=ca.${DOMAIN}"
${CA_CERT_NAME} = "ca.${ORG}"
${CA_DAYS} = 3650

# Server configuration
${SV_DAYS} = 1000
${SV_NAMES} = @(
  ${env:COMPUTERNAME}.ToLower()
  'cure'
  'poison'
  'toad'
  'meteor'
)

# Environment variables
${env:PATH} = "${DEST}\x64\bin;${env:PATH}"
if ( -Not ${env:CA_HOME} ) { ${env:CA_HOME} = "${env:LOCALAPPDATA}\CA" }
# --------------------------------------------------
Write-Host "CA_HOME: ${env:CA_HOME}"


# To be running again as administrators if not administrators.
${currentUser} = [Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()

if ( -Not( ${currentUser} ).IsInRole( [System.Security.Principal.WindowsBuiltInRole]::Administrator ) ) {
  Start-Process -Verb RunAs -FilePath pwsh -ArgumentList @(
    '-ExecutionPolicy', 'RemoteSigned'
    '-File', "`"${PSCommandPath}`""
    $( if( ${WithCertCreation} ) { '-WithCertCreation' } )
    $( if( ${WithoutOpensslInstallation} ) { '-WithoutOpensslInstallation' } )
  )
  exit
}


# Install OpenSSL if specified
if ( -Not ${WithoutOpensslInstallation} ) {

  # Download OpenSSL
  ${requireDownload} = -Not ( Test-Path "${ZIP}" ) -Or `
    -Not ( "${SRC_HASH}" -eq ( Get-FileHash -Path "${ZIP}" -Algorithm "${HASH_ALG}" ).Hash )
  
  Write-Host "Downloading to ${ZIP} from ${SRC}"
  if( ${requireDownload} ) {
    Invoke-WebRequest -Uri "${SRC}" -outfile "${ZIP}"
  } else {
    Write-Host 'Skip download OpenSSL. Because already exists.'
  }
  
  # Extract OpenSSL
  Write-Host "Extracting to ${DEST} from ${ZIP}"
  if( Test-Path "${DEST}" ) { Remove-Item "${DEST}" -Recurse -Force }
  Expand-Archive -Path "${ZIP}" -DestinationPath "${DEST}"
  
  # Create junctions
  Write-Host "Create junction to ${DEST} from ${PREFIX}\primary"
  if( Test-Path "${PREFIX}\primary" ) { ( Get-Item "${PREFIX}\primary" ).Delete() }
  New-Item -Path "${PREFIX}" -Name primary -Value "${DEST}" -ItemType Junction
}


# Create certs if specified
if ( ${WithCertCreation} ) {
  # Get CA passphrase
  ${CaPass} = Read-Host -Prompt 'Enter CA passphrase' -AsSecureString
  
  # Create CA directories
  if( Test-Path "${env:CA_HOME}" ) { }
  New-Item -Type Directory -Path "${env:CA_HOME}\private"
  New-Item -Type Directory -Path "${env:CA_HOME}\crl"
  New-Item -Type Directory -Path "${env:CA_HOME}\certs"
  New-Item -Type Directory -Path "${env:CA_HOME}\newcerts"
  New-Item -Type File -Path "${env:CA_HOME}\index.txt"
  Set-Content -Value ( [Text.Encoding]::UTF8.GetBytes( '01' ) ) `
    -AsByteStream -Path "${env:CA_HOME}\serial"

  # Set CA passphrase
  ${decoded} = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR( ${CaPass} )
  Set-Content -Value ( [Text.Encoding]::UTF8.GetBytes( `
    "$( [System.Runtime.InteropServices.Marshal]::PtrToStringBSTR( ${decoded} ) )" ) ) `
    -AsByteStream -Path "${env:CA_HOME}\.capass"
  
  # OpenSSL configuration for CA
  $( Get-Content "${OPENSSL_ORIGIN_CONF}" ) | `
    ForEach-Object { $_ -replace './demoCA', '$ENV::CA_HOME' } | `
    Set-Content "${CA_CONF}"
  
@"

[ v3_self_ca ]
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid:always,issuer
basicConstraints = CA:true
keyUsage = cRLSign, keyCertSign
"@ `
    | ForEach-Object{ [Text.Encoding]::UTF8.GetBytes( $_ + "`n" ) } `
    | Add-Content -AsByteStream -Path "${CA_CONF}"
  
  # Create CA cert
  Write-Host 'Create CA cert'

  openssl req -new -x509 -extensions v3_self_ca -config "${CA_CONF}" -subj "${CA_SUBJ}" `
  -keyout "${env:CA_HOME}\private\${CA_CERT_NAME}.key" -out "${env:CA_HOME}\certs\${CA_CERT_NAME}.crt" `
  -passout file:"${env:CA_HOME}\.capass" -days ${CA_DAYS}
  
  openssl x509 -in "${env:CA_HOME}\certs\${CA_CERT_NAME}.crt" -text
  
  
  # Create servers certs
  foreach ( ${serverName} in ${SV_NAMES} ) {
  
    # OpenSSL configuration for server
    Get-Content "${OPENSSL_ORIGIN_CONF}" | ForEach-Object { $_ -replace './demoCA', '$ENV::CA_HOME' } `
    | Set-Content -Path "${env:CA_HOME}\openssl.${serverName}.cnf"
  
@"

[ v3_self_server ]
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment, keyAgreement
extendedKeyUsage = serverAuth, clientAuth
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid,issuer
subjectAltName=DNS:${serverName}.${DOMAIN},DNS:${serverName},DNS:localhost,IP:127.0.0.1
"@ `
      | ForEach-Object{ [Text.Encoding]::UTF8.GetBytes( $_ + "`n" ) } `
      | Add-Content -AsByteStream -Path "${env:CA_HOME}\openssl.${serverName}.cnf"
  
    ## Create server cert
    Write-Host "Create server cert: ${serverName}"

    openssl genrsa -out "${env:CA_HOME}\private\${serverName}.${ORG}.key" 2048
  
    openssl req -new -config "${env:CA_HOME}\openssl.${serverName}.cnf" -key "${env:CA_HOME}\private\${serverName}.${ORG}.key" `
    -out "${env:CA_HOME}\certs\${serverName}.${ORG}.csr" -subj "/C=${COUNTRY}/ST=${STATION}/O=${ORG}/CN=${serverName}.${DOMAIN}"
  
    openssl ca -batch -extensions v3_self_server -config "${env:CA_HOME}\openssl.${serverName}.cnf" `
    -cert "${env:CA_HOME}\certs\${CA_CERT_NAME}.crt" -keyfile "${env:CA_HOME}\private\${CA_CERT_NAME}.key" `
    -passin file:"${env:CA_HOME}\.capass" -out "${env:CA_HOME}\certs\${serverName}.${ORG}.crt" -days ${SV_DAYS} `
    -infiles "${env:CA_HOME}\certs\${serverName}.${ORG}.csr"
  }
}

Write-Host 'Install CA cert'

# Uninstall CA if exists
Get-ChildItem -Path Cert:\LocalMachine\Root | Where-Object { $_.Subject -like "*CN=ca.${DOMAIN},*" } `
  | ForEach-Object{ Remove-Item -Path "Cert:\LocalMachine\Root\$( $_.Thumbprint )" }

  
# Install CA
Import-Certificate -FilePath "${env:CA_HOME}\certs\${CA_CERT_NAME}.crt" `
  -CertStoreLocation Cert:\LocalMachine\Root

Read-Host "Press enter to exit"

# SIG # Begin signature block
# MIIGXAYJKoZIhvcNAQcCoIIGTTCCBkkCAQExCzAJBgUrDgMCGgUAMGkGCisGAQQB
# gjcCAQSgWzBZMDQGCisGAQQBgjcCAR4wJgIDAQAABBAfzDtgWUsITrck0sYpfvNR
# AgEAAgEAAgEAAgEAAgEAMCEwCQYFKw4DAhoFAAQU2BCQLJAPDOeHi+Itniq6DsUT
# 1IGgggPPMIIDyzCCArOgAwIBAgIBBjANBgkqhkiG9w0BAQsFADBRMQswCQYDVQQG
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
# AQQBgjcCAQsxDjAMBgorBgEEAYI3AgEWMCMGCSqGSIb3DQEJBDEWBBQIop9sgVIG
# xknWW/TPrQpS+0X+7jANBgkqhkiG9w0BAQEFAASCAQC69tkLIibGU9j5BlHn3Efm
# CbYinCihfp2xZLh0Y0WNKXv60ECf4FJKfs3CTh7K5Pjeo+GQ+anokTv3pFxyjBWd
# hVMFaqeUqYNLMrlr/bXnwu9qz9su4n3BmkqZEZJPLw/GZQp+C4mj2ObDmE2LImPY
# WiZ0gyWxvwnlMzoIOaLzi5DieLiuI9LRP6Nf5EoHIsksmBckhPdUoZuEI8phqePJ
# Vji09Pw98ueKMm3/lGunY0rw6lrBNSI3EiGM95kU5tvrBJPnqjVbh0CjOitt7akU
# 2GK7Q5UgjNLP+6SNrXzQ6I7l4KFodBqanLv7utx3uYfGyYt5aMjflkM25fpij5Cy
# SIG # End signature block
