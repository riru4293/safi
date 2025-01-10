$ErrorActionPreference = 'Continue'

$ORG = 'project-k'
$env:CA_HOME = "${env:LOCALAPPDATA}\CA"
$CA_CERT_NAME = "ca.${ORG}"

# Register CA to JDK
$JDK_PREFIX = "${env:LOCALAPPDATA}\Programs\Java"
$JdkNames = Get-ChildItem "${JDK_PREFIX}" | Where-Object { $_.PSIsContainer } | Foreach-Object { $_.Name } `
| Out-String -Stream | Select-String "jdk-"

$ErrorActionPreference = 'Continue'
foreach ( ${JdkName} in ${JdkNames} ) {
  Write-Output ${JdkName}

  $JAVA_HOME = Join-Path -Path "${JDK_PREFIX}" -ChildPath "${JdkName}"

  Start-Process -Wait -NoNewWindow -FilePath "${JAVA_HOME}\bin\keytool.exe" -ArgumentList @(
    '-delete', '-cacerts', '-alias', ${ORG}, '-storepass', 'changeit'
  )

  Start-Process -Wait -NoNewWindow -FilePath "${JAVA_HOME}\bin\keytool.exe" -ArgumentList @(
    '-import', '-cacerts', '-alias', ${ORG}, '-storepass', 'changeit', '-noprompt' `
  , '-file', "`"${env:CA_HOME}\certs\${CA_CERT_NAME}.crt`""
  )

  Start-Process -Wait -NoNewWindow -FilePath "${JAVA_HOME}\bin\keytool.exe" -ArgumentList @(
    '-list', '-cacerts', '-alias', ${ORG}, '-v'
  )
}

Read-Host "Press enter to exit."


# SIG # Begin signature block
# MIIGgQYJKoZIhvcNAQcCoIIGcjCCBm4CAQExDzANBglghkgBZQMEAgEFADB5Bgor
# BgEEAYI3AgEEoGswaTA0BgorBgEEAYI3AgEeMCYCAwEAAAQQH8w7YFlLCE63JNLG
# KX7zUQIBAAIBAAIBAAIBAAIBADAxMA0GCWCGSAFlAwQCAQUABCCWGAmznSh3ALLc
# s/ifzYYADcg0ENaDktjR/RFQeWZAQqCCA88wggPLMIICs6ADAgECAgEGMA0GCSqG
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
# FjAvBgkqhkiG9w0BCQQxIgQgvJK+4XLsYjl2xxHxaMCNzUfB775dcgu1O0t3Iqnv
# vcowDQYJKoZIhvcNAQEBBQAEggEASW287c6Twkz47gK9aygQZcbEW/vztKuWUqek
# 00ec9uc6p8BX6H8Sz61wwavZLLgINrt0dmVCCfigwpUV5nb8LfnNFPHPLOojIWj/
# 7/V6J6acAHgljza7vCUVR3xtG/rOghj+KWeqP3pse16l4Q+0C4Xx9BULB9ctuXzv
# TSP3Btd9W03+zw9dFU5MIyR2xMsYy1pBnHgFl7Gz5C/egmMJF78B59h6bSXn4cJ/
# fO2Zn8VmVjfUWKZwvXcWGTWTyZ8lkdm2f0GXSek/HaYgqckcE+dVa4U2P9vFiyvX
# /XyTJ3XnzEDHr6tad+1nM1pK58KQWiY8NQHvSnFBL5BILsoohA==
# SIG # End signature block
