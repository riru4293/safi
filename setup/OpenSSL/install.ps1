#Requires -Version 7
${ErrorActionPreference} = 'Continue'

${LOCAL_ROOT} = "${env:USERPROFILE}\local"

# OpenSSL configuration
# See: https://kb.firedaemon.com/support/solutions/articles/4000121705
${VER} = '3.6.1'
${SRC} = "https://download.firedaemon.com/FireDaemon-OpenSSL/openssl-${VER}.zip"
${ZIP} = "${env:TMP}\$( Split-Path -Path ${SRC} -Leaf )"

${PREFIX} = "${LOCAL_ROOT}\opt\OpenSSL"
${DEST} = "${PREFIX}\${VER}"

:transaction do {

    Write-Host "Install the OpenSSL-${VER} to [ ${DEST} ]."
    ${ans} = Read-Host 'Continue? (Y/N)'
    if ( -not ( ( ${ans} -eq 'Y' ) -or ( ${ans} -eq 'y' ) ) ) { Write-Host 'Aborted.'; break :transaction }

    # Fetch source if require
    Write-Host '-----'
    Write-Host 'Download OpenSSL'
    Write-Host "source: ${SRC}"
    Write-Host "destination ${ZIP}"
    Invoke-WebRequest -Uri "${SRC}" -outfile "${ZIP}"

    # Extract OpenSSL
    Write-Host '-----'
    Write-Host 'Extract OpenSSL'
    Write-Output "source: ${ZIP}"
    Write-Output "destination: ${DEST}"
    if( Test-Path "${DEST}" ) { Remove-Item "${DEST}" -Recurse -Force }
    Expand-Archive -Path "${ZIP}" -DestinationPath "${DEST}"

    # Create junctions
    Write-Host "Create junction to ${DEST} from ${PREFIX}\primary"
    if( Test-Path "${PREFIX}\primary" ) { ( Get-Item "${PREFIX}\primary" ).Delete() }
    New-Item -Path "${PREFIX}" -Name primary -Value "${DEST}" -ItemType Junction

    Write-Host 'Completed.'

} while ( $false )

Read-Host "Press enter to exit.
# SIG # Begin signature block
# MIIGgQYJKoZIhvcNAQcCoIIGcjCCBm4CAQExDzANBglghkgBZQMEAgEFADB5Bgor
# BgEEAYI3AgEEoGswaTA0BgorBgEEAYI3AgEeMCYCAwEAAAQQH8w7YFlLCE63JNLG
# KX7zUQIBAAIBAAIBAAIBAAIBADAxMA0GCWCGSAFlAwQCAQUABCC4VSQ0mNpqo5eD
# OsD/UD8TR1WzDbe77kCfoMlFjH8FDaCCA88wggPLMIICs6ADAgECAgEGMA0GCSqG
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
# FjAvBgkqhkiG9w0BCQQxIgQgUIUR273jk1wvcVDHl7l2lDFyP+VCZoa0NYcOWUZ9
# mDcwDQYJKoZIhvcNAQEBBQAEggEAlre5gpcJYKCHDdcbfw/5QZq4pRb1D2taIRKi
# 1CWPqUifU2cfgba8yyR0MFGwmmO7z4mtFwa+8voUsbTvVf5pdVPz2xHkJpES5qsa
# HVNO6gYlvSidc7vssEYcxurhZsMfo8LwPScMpo/JZuMbAFyjbn27eZ68OvuE6t4P
# ZdrQVLrzyeaboCfSHUgiNDoExJOJoS1nmgiEaokblm1+DdTVQUfQglXXCaGlE7Wl
# qskOi0wIdvQZBw+ulCiyYs1OYP1LD4Yg2xQZviqXRFtQFJpH1jhPYIn62XCfia5b
# /xoXJgHGRWohj4UvFVgdcGx6xLjDXZpBKuN5i8JzFw8FNQcHQA==
# SIG # End signature block
