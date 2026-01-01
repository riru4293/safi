#Requires -Version 7
${ErrorActionPreference} = 'Continue'

# OpenSSL configuration
${VER} = '3.6.0'
${PREFIX} = "${env:USERPROFILE}\local\opt\OpenSSL"
${DEST} = "${PREFIX}\${VER}"


# See: https://kb.firedaemon.com/support/solutions/articles/4000121705
${SRC} = "https://download.firedaemon.com/FireDaemon-OpenSSL/openssl-${VER}.zip"
${ZIP} = "${env:TMP}\$( Split-Path -Path ${SRC} -Leaf )"
${SRC_HASH} = 'C1C831E8BCCE7D6C204D6813AAFB87C0D44DD88841AB31105185B55CDEC1D759'.ToUpper()
${HASH_ALG} = 'SHA256' # [MD5|SHA1|SHA256|SHA512]



:transaction do {

    Write-Host "Install the OpenSSL-${VER} to [ ${DEST} ]."
    ${ans} = Read-Host 'Continue? (Y/N)'
    if ( -not ( ( ${ans} -eq 'Y' ) -or ( ${ans} -eq 'y' ) ) ) { Write-Host 'Aborted.'; break :transaction }

    # Confirm require fetch source
    ${needFetch} = ( -not ( Test-Path "${ZIP}" ) ) -or `
        ( "${SRC_HASH}" -ne ( Get-FileHash -Path "${ZIP}" -Algorithm "${HASH_ALG}" ).Hash )

    # Fetch source if require
    if( ${needFetch} ) {
        Write-Host "Downloading to ${ZIP} from ${SRC}"
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

    Write-Host 'Completed.'

} while ( $false )

Read-Host "Press enter to exit."

# SIG # Begin signature block
# MIIGXAYJKoZIhvcNAQcCoIIGTTCCBkkCAQExCzAJBgUrDgMCGgUAMGkGCisGAQQB
# gjcCAQSgWzBZMDQGCisGAQQBgjcCAR4wJgIDAQAABBAfzDtgWUsITrck0sYpfvNR
# AgEAAgEAAgEAAgEAAgEAMCEwCQYFKw4DAhoFAAQUW6gRvmqOS8ViekuAPV3k3jii
# euSgggPPMIIDyzCCArOgAwIBAgIBBjANBgkqhkiG9w0BAQsFADBRMQswCQYDVQQG
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
# AQQBgjcCAQsxDjAMBgorBgEEAYI3AgEWMCMGCSqGSIb3DQEJBDEWBBS227oXyB2L
# 8LUfMzuOTrPa+VTWkDANBgkqhkiG9w0BAQEFAASCAQC0jlDV8xidZXdjduYNv5pE
# BFsfw5kVsuee8CipsjUk1o8A/4ld2yKVYPuVdgXOLby5pmLspA6hwszLlSuSYmaF
# +bTLEiLTD/Sz83FB6Es1jCo8U3hqPbf9mXIZI5qs73qZCIJ19+Z/QGA79iB4a+tC
# r7UDZnGpvZMBVKeGqS7AOZuOL+Sr2wLXyJ0nYOgCpywwp9KlfGTaCRIcFEEYv2Ci
# mzqhE0JKt616zHRlduAifN1wXdudly0khY5ewNyJBfBPvVmqmEOB9CBd9he2X7xI
# //vEvsX13aZca38qsAGgWSHEQMFf7LsXl5iXbrcvh/1fxnnnsnPrxMLT828URFWB
# SIG # End signature block
