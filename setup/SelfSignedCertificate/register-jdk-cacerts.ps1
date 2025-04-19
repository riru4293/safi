#Requires -Version 7
$ErrorActionPreference = 'Continue'

${ORG} = 'Project-k'
${CA_CERT} = "${env:LOCALAPPDATA}\CA\certs\ca.$(${ORG}.toLower()).crt"
${JDKS} = @(
  # @{ HOME = "${env:LOCALAPPDATA}\Programs\Java\jdk8" ; STORE_PASS = 'changeit' }
  # @{ HOME = "${env:LOCALAPPDATA}\Programs\Java\jdk11"; STORE_PASS = 'changeit' }
  # @{ HOME = "${env:LOCALAPPDATA}\Programs\Java\jdk17"; STORE_PASS = 'changeit' }
  @{ HOME = "${env:LOCALAPPDATA}\Programs\Java\jdk21"; STORE_PASS = 'changeit' }
  # @{ HOME = "${env:LOCALAPPDATA}\Programs\Java\jdk24"; STORE_PASS = 'changeit' }
)

foreach ( ${jdk} in ${JDKS} ) {
  ${h} = ${jdk}.HOME
  ${p} = ${jdk}.STORE_PASS

  if( Test-Path "${h}\bin\java.exe" ) {
    Write-Host "Found JDK: ${h}"

    # Get Java version outputs
    ${a} = ( & "${h}\bin\java.exe" -version 2>&1 )

    ${a} | ForEach-Object {
      if ( $_ -match 'version "([\d\.]+)' ) {
        ${v} = ${Matches}[1]
        Write-Host "Java version: ${v}"
  
        # Major version
        ${m} = if ( ${v} -like '1.*' ) {
            # Version 8 and under (example: 1.8 ↁE8)
            ${v}.Split('.')[1]
        } else {
            # Newer than version 8 (example: 9.0 -> 9)
            ${v}.Split('.')[0]
        }
        
        # Register the CA certificate
        if ( [int]${m} -le 8 ) {
          & "${h}\bin\keytool.exe" -delete -alias ${ORG} -storepass "${p}" -keystore "${h}\jre\lib\security\cacerts"
          & "${h}\bin\keytool.exe" -import -alias ${ORG} -storepass "${p}" -keystore "${h}\jre\lib\security\cacerts" `
          -noprompt -trustcacerts -file "${CA_CERT}"
        } else {
          & "${h}\bin\keytool.exe" -delete -cacerts -alias ${ORG} -storepass "${p}"
          & "${h}\bin\keytool.exe" -import -cacerts -alias ${ORG} -storepass "${p}" -noprompt -file "${CA_CERT}"
        }
      }
    }
  }
}

Read-Host 'Press enter to exit'

# SIG # Begin signature block
# MIIGXAYJKoZIhvcNAQcCoIIGTTCCBkkCAQExCzAJBgUrDgMCGgUAMGkGCisGAQQB
# gjcCAQSgWzBZMDQGCisGAQQBgjcCAR4wJgIDAQAABBAfzDtgWUsITrck0sYpfvNR
# AgEAAgEAAgEAAgEAAgEAMCEwCQYFKw4DAhoFAAQU5JESl3ZDlwUujREJfzGRYaXM
# JfSgggPPMIIDyzCCArOgAwIBAgIBBjANBgkqhkiG9w0BAQsFADBRMQswCQYDVQQG
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
# AQQBgjcCAQsxDjAMBgorBgEEAYI3AgEWMCMGCSqGSIb3DQEJBDEWBBTpyuC8Twhw
# Us5S+mhqeIvVzcZpGTANBgkqhkiG9w0BAQEFAASCAQB+VUhnXv7SQXI0e/mmICEk
# mdb42FERdRZE180MoxtcSqC7ApFhdJzlTrsebwWPYgeydvv7k8KstudckmEnkSHQ
# Iwj6ikK7ASaYwenREMATkTcUb0jqYSLR3p6fvsKF3H28AeKRNa7AkHqQVqDsngxJ
# fbtJ6Zd8YbNJb9MQ4fAbktYbz8yvDVIKlXai3fanWZEF/uVD6m+Of5Y+ifmsyPp4
# mRF9I5PGVQGmtVCAJyzD4lqW2dET+nJ1+JWKBZX6OnsXUrIqKpSzDpAq9+iGYBaD
# zGcdEiBeAI3Pc8VFAVyfhRwowwOw/1PN3S5yTEGMF7iobdlA5keJVfZFJGVO42Ei
# SIG # End signature block
