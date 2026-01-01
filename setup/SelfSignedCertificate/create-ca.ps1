#Requires -Version 7
${ErrorActionPreference} = 'Continue'


# Architecture detection
${archProp} = [System.Runtime.InteropServices.RuntimeInformation]::ProcessArchitecture

${archMap} = @{
    'X64'  = 'x64'
    'X86'  = 'x86'
    'Arm64' = 'arm64'
}

${arch} = ${archMap}[${archProp}.ToString()]
if ( -Not ${arch} ) { ${arch} = 'x86' }  # Fallback to x86


# OpenSSL configuration
${OPENSSL_HOME} = "${env:USERPROFILE}\local\opt\OpenSSL\primary"
${OPENSSL_ORIGIN_CONF} = "${OPENSSL_HOME}\ssl\openssl.cnf"


# Append OpenSSL binary path
${env:PATH} = "${OPENSSL_HOME}\${arch}\bin;${env:PATH}"


# Configure CA home directory; if not set, use default path.
if ( -Not ${env:CA_HOME} )
{
    ${env:CA_HOME} = "${env:USERPROFILE}\local\etc\tls"
}


# Certificate configuration
${COUNTRY} = 'JP'
${STATION} = 'Osaka'
${ORG} = 'Project-k'
${DOMAIN} = 'project-k.mydns.jp'


# CA configuration
${CA_CONF} = "${env:CA_HOME}\openssl.ca.cnf"
${CA_SUBJ} = "/C=${COUNTRY}/ST=${STATION}/O=${ORG}/CN=ca.${DOMAIN}"
${CA_CERT_NAME} = "ca.$(${ORG}.toLower())"
${CA_DAYS} = 3650

# --------------------------------------------------

:transaction do {

    Write-Host "CA_CONF      is [ ${CA_CONF} ]."
    Write-Host "CA_SUBJ      is [ ${CA_SUBJ} ]."
    Write-Host "CA_CERT_NAME is [ ${CA_CERT_NAME} ]."
    Write-Host "CA_DAYS      is [ ${CA_DAYS} ]."
    Write-Host "CA_HOME is [ ${env:CA_HOME} ]."

    ${ans} = Read-Host 'Continue? (Y/N)'
    if ( -not ( ( ${ans} -eq 'Y' ) -or ( ${ans} -eq 'y' ) ) ) { Write-Host 'Aborted.'; break :transaction }

    # Get CA passphrase
    ${caPass} = Read-Host -Prompt 'Enter CA passphrase' -AsSecureString
  
    # Create new CA directories
    if( Test-Path "${env:CA_HOME}" ) { ( Get-Item "${env:CA_HOME}" ).Delete() }

    New-Item -Type Directory -Path "${env:CA_HOME}\private"
    New-Item -Type Directory -Path "${env:CA_HOME}\crl"
    New-Item -Type Directory -Path "${env:CA_HOME}\certs"
    New-Item -Type Directory -Path "${env:CA_HOME}\newcerts"
    New-Item -Type File -Path "${env:CA_HOME}\index.txt"

    Set-Content -Value ( [Text.Encoding]::UTF8.GetBytes( '01' ) ) `
        -AsByteStream -Path "${env:CA_HOME}\serial"

    # Set CA passphrase
    ${decoded} = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR( ${caPass} )
    Set-Content -Value ( [Text.Encoding]::UTF8.GetBytes( `
        "$( [System.Runtime.InteropServices.Marshal]::PtrToStringBSTR( ${decoded} ) )" ) ) `
        -AsByteStream -Path "${env:CA_HOME}\.caPass"
  
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
        -keyout "${env:CA_HOME}\private\${CA_CERT_NAME}.key" `
        -out "${env:CA_HOME}\certs\${CA_CERT_NAME}.crt" `
        -passout file:"${env:CA_HOME}\.caPass" `
        -days ${CA_DAYS}
    
    openssl x509 -in "${env:CA_HOME}\certs\${CA_CERT_NAME}.crt" -text

} while ( $false )

Read-Host "Press enter to exit."

# SIG # Begin signature block
# MIIGXAYJKoZIhvcNAQcCoIIGTTCCBkkCAQExCzAJBgUrDgMCGgUAMGkGCisGAQQB
# gjcCAQSgWzBZMDQGCisGAQQBgjcCAR4wJgIDAQAABBAfzDtgWUsITrck0sYpfvNR
# AgEAAgEAAgEAAgEAAgEAMCEwCQYFKw4DAhoFAAQU//xHdIt/Gdv6b/8yDyq2uvJI
# G2SgggPPMIIDyzCCArOgAwIBAgIBBjANBgkqhkiG9w0BAQsFADBRMQswCQYDVQQG
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
# AQQBgjcCAQsxDjAMBgorBgEEAYI3AgEWMCMGCSqGSIb3DQEJBDEWBBS2ozeNNiwY
# VGaqgIKbWZ1Y+P/4cTANBgkqhkiG9w0BAQEFAASCAQDL6Vi7t+bQT/N5/iCu/qZg
# 04l1dmZx//q26IaCltOuZ2xoKfZ1NryrPEbci+GXU+8cKbPvgA+jKDHpoQnLxkc6
# OTH0w7/7oygVKKZbJA3sXgIdVW0JVixJ4I4Bmmf1bgNWBlpN+cx3Xxgoj2dhaQMp
# rism5PJvUBK9hUXVrrFa0UBEoRjDECdP6rDC2G8f0mrfEjXowC7JVARlsEQaYsol
# 3Lk8xpNlQPwJXsGFqmR1AO+W/WPOAHnp2DFgf9uVTfv7aWDHQVPqBAYcCwN6qpJe
# 3juTDm2umTMN0dGVyk1usYKuiz0L4V5oIUBfBlJzswsSfJhbXzPIjMuTdlm/kQTp
# SIG # End signature block
