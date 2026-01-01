#Requires -Version 7
Param(
    [Parameter(Mandatory=$true)]
    [string]${serverName}
)

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
${CA_CERT} = "${env:CA_HOME}\certs\ca.project-k.crt"
${CA_KEY} = "${env:CA_HOME}\private\ca.project-k.key"


# Server configuration
${SV_CONF} = "${env:CA_HOME}\openssl.${serverName}.cnf"
${SV_SUBJ} = "/C=${COUNTRY}/ST=${STATION}/O=${ORG}/CN=${serverName}.${DOMAIN}"
${SV_CERT_NAME} = "${serverName}.$(${ORG}.toLower())"
${SV_DAYS} = 1000


# --------------------------------------------------

:transaction do {

    Write-Host "server is [ ${serverName} ]."
    Write-Host "CA is [ ${CA_CERT} ]."

    ${ans} = Read-Host 'Continue? (Y/N)'
    if ( -not ( ( ${ans} -eq 'Y' ) -or ( ${ans} -eq 'y' ) ) ) { Write-Host 'Aborted.'; break :transaction }

    # Get CA passphrase
    ${caPass} = Read-Host -Prompt 'Enter CA passphrase' -AsSecureString

    # Set CA passphrase
    ${decoded} = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR( ${caPass} )
    Set-Content -Value ( [Text.Encoding]::UTF8.GetBytes( `
        "$( [System.Runtime.InteropServices.Marshal]::PtrToStringBSTR( ${decoded} ) )" ) ) `
        -AsByteStream -Path "${env:CA_HOME}\.caPass"

    # OpenSSL configuration for server
    $( Get-Content "${OPENSSL_ORIGIN_CONF}" ) | `
        ForEach-Object { $_ -replace './demoCA', '$ENV::CA_HOME' } | `
        Set-Content "${SV_CONF}"
  
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
        | Add-Content -AsByteStream -Path "${SV_CONF}"
  
    # Create secret key
    Write-Host 'Create secret key'

    openssl genrsa -out "${env:CA_HOME}/private/${SV_CERT_NAME}.key" 2048
  
    # Create CSR
    Write-Host 'Create CSR'
    openssl req -new `
        -config   "${SV_CONF}" `
        -key      "${env:CA_HOME}/private/${SV_CERT_NAME}.key" `
        -out      "${env:CA_HOME}/certs/${SV_CERT_NAME}.csr" `
        -subj     "${SV_SUBJ}"

    # Create Cert
    Write-Host 'Create Cert'
    openssl ca `
        -config     "${SV_CONF}" `
        -cert       "${CA_CERT}" `
        -keyfile    "${CA_KEY}" `
        -batch `
        -extensions v3_self_server `
        -out        "${env:CA_HOME}/certs/${SV_CERT_NAME}.crt" `
        -days       ${SV_DAYS} `
        -passin     "file:${env:CA_HOME}/.caPass" `
        -infiles    "${env:CA_HOME}/certs/${SV_CERT_NAME}.csr"
    
    openssl x509 -in "${env:CA_HOME}/certs/${SV_CERT_NAME}.crt" -text

} while ( $false )

Read-Host "Press enter to exit."

# SIG # Begin signature block
# MIIGXAYJKoZIhvcNAQcCoIIGTTCCBkkCAQExCzAJBgUrDgMCGgUAMGkGCisGAQQB
# gjcCAQSgWzBZMDQGCisGAQQBgjcCAR4wJgIDAQAABBAfzDtgWUsITrck0sYpfvNR
# AgEAAgEAAgEAAgEAAgEAMCEwCQYFKw4DAhoFAAQUVUK/NxEyOuZEHi2NGvd0Z9fw
# ynOgggPPMIIDyzCCArOgAwIBAgIBBjANBgkqhkiG9w0BAQsFADBRMQswCQYDVQQG
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
# AQQBgjcCAQsxDjAMBgorBgEEAYI3AgEWMCMGCSqGSIb3DQEJBDEWBBQul4mT1Oey
# WRnMAQkuQJw1fuUx8jANBgkqhkiG9w0BAQEFAASCAQCu40eDSRWgPP1wSZXdzZy3
# tur5TC616pcRLuR3oUN2oIyfMnegNedu1HcXnr8AaFJttQFYKgUdjohRTYiNt1E1
# +rNZtaFis3APkIRRtKmiC8PcEmiDMV9s6iD4bBH5QKOWs97CGmA46o2CV0QqwzdD
# JjwVzphlZ4Wt8oqqSNwrRqVdO+hgD6mZmilDgNGy8I3BOsrubnG0Xz7HkKwOgQVw
# SCdc6l3s8V/5ueCn7Uu+qk15yHLq14CprvlcVYC/NpOSun28PwFy/B7a3yq55iO0
# ttUmRwiAyL3mcKMHEBAVSsD80c8/ODJZvz8TwzAScqnTi8ploRRXyeLwqqqClgPM
# SIG # End signature block
