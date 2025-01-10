$ErrorActionPreference = 'Stop'

# To be running again as administrators if not administrators.
if (!([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole('Administrators')) { Start-Process powershell.exe "-ExecutionPolicy RemoteSigned -File `"$PSCommandPath`"" -Verb RunAs; exit }

$VER = "3.4.0"
$PREFIX = "${env:LOCALAPPDATA}\Programs\OpenSSL"
$DEST = "${PREFIX}\${VER}"

$ORG = 'project-k'
$DOMAIN = 'project-k.mydns.jp'
$CA_PASS = 'Please change'

# See: https://kb.firedaemon.com/support/solutions/articles/4000121705
$SRC = "https://download.firedaemon.com/FireDaemon-OpenSSL/openssl-${VER}.zip"
$SRC_HASH = 'FFF7C56C1588644A609F63F60FAD15275E2CE6FFC310C320CDCB72554449CCA5'
$HASH_ALG = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
$ZIP = "${env:TMP}\$( ${SRC}.Substring( ${SRC}.LastIndexOf('/') + 1 ) )"

$OPENSSL = "${PREFIX}\primary\$( if( [System.Environment]::Is64BitProcess ) { Write-Output x64 } else { Write-Output x86 } )\bin\openssl.exe"
$OPENSSL_CONF = "${PREFIX}\primary\ssl\openssl.cnf"
$env:CA_HOME = "${env:LOCALAPPDATA}\CA"
$CA_CONF = "${env:CA_HOME}\openssl.ca.cnf"
$CA_SUBJ = "/C=JP/ST=Osaka/O=${ORG}/CN=ca.${DOMAIN}"
$CA_CERT_NAME = "ca.${ORG}"
$CA_DAYS = 3650

$SV_NAME = ${env:COMPUTERNAME}.ToLower()
$SV_SUBJ = "/C=JP/ST=Osaka/O=${ORG}/CN=${SV_NAME}.${DOMAIN}"
$SV_CONF = "${env:CA_HOME}\openssl.${SV_NAME}.cnf"
$SV_CERT_NAME = "${SV_NAME}.${ORG}"
$SV_DAYS = 1000


# Download
$RequireDownload = -Not( Test-Path "${ZIP}" ) `
  -or ${SRC_HASH}.ToUpper() -ne ( Get-FileHash -Path "${ZIP}" -Algorithm "${HASH_ALG}" ).Hash

if( ${RequireDownload} ) {
  Write-Output "Downloading to ${ZIP} from ${SRC}"
  Invoke-WebRequest -Uri "${SRC}" -outfile "${ZIP}"
} else {
  Write-Output 'Skip download OpenSSL. Because already exists.'
}


# Extract
Write-Output "Extracting to ${DEST} from ${ZIP}"
if( Test-Path "${DEST}" ) { Remove-Item "${DEST}" -Recurse -Force }
Expand-Archive -Path "${ZIP}" -DestinationPath "${DEST}"


# Create junctions
if( Test-Path "${PREFIX}\primary" ) { ( Get-Item "${PREFIX}\primary" ).Delete() }
New-Item -Path "${PREFIX}" -Name primary -Value "${DEST}" -ItemType Junction


# Abort if already exists a CA
if( Test-Path "${env:CA_HOME}" )
{
  Write-Output "Abort because already exists a CA. [ ${env:CA_HOME} ]"
  Read-Host "Press enter to exit."
  exit 9
}


# Create CA directories
New-Item -Type Directory -Path "${env:CA_HOME}\private"
New-Item -Type Directory -Path "${env:CA_HOME}\crl"
New-Item -Type Directory -Path "${env:CA_HOME}\certs"
New-Item -Type Directory -Path "${env:CA_HOME}\newcerts"
New-Item -Type File -Path "${env:CA_HOME}\index.txt"
Write-Output '01' | ForEach-Object { [Text.Encoding]::UTF8.GetBytes($_) } `
| Set-Content -Encoding Byte -Path "${env:CA_HOME}\serial"
Write-Output "${CA_PASS}" | ForEach-Object { [Text.Encoding]::UTF8.GetBytes($_) } `
| Set-Content -Encoding Byte -Path "${env:CA_HOME}\.capass"


# Modifying the OpenSSL configuration file
$( Get-Content "${OPENSSL_CONF}" ) | `
  ForEach-Object { $_ -replace './demoCA', '$ENV::CA_HOME' } | `
  Set-Content "${CA_CONF}"

@"

[ v3_self_ca ]
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid:always,issuer
basicConstraints = CA:true
keyUsage = cRLSign, keyCertSign
"@ | ForEach-Object{ $_ + "`n" } | ForEach-Object{ [Text.Encoding]::UTF8.GetBytes($_) } `
| Add-Content -Encoding Byte -Path "${CA_CONF}"

Start-Process -Wait -NoNewWindow -FilePath "${OPENSSL}" -ArgumentList @(
  'req', '-new', '-x509', '-extensions', 'v3_self_ca' `
, '-config', "`"${CA_CONF}`"" `
, '-subj', "`"${CA_SUBJ}`"" `
, '-keyout', "`"${env:CA_HOME}\private\${CA_CERT_NAME}.key`"" `
, '-out', "`"${env:CA_HOME}\certs\${CA_CERT_NAME}.crt`"" `
, '-passout', "file:`"${env:CA_HOME}\.capass`"" `
, '-days', "${CA_DAYS}"
)

Start-Process -Wait -NoNewWindow -FilePath "${OPENSSL}" -ArgumentList @(
  'x509', '-in', "`"${env:CA_HOME}\certs\${CA_CERT_NAME}.crt`"" , '-text'
)


# Modifying the OpenSSL configuration file
$( Get-Content "${OPENSSL_CONF}" ) | `
  ForEach-Object { $_ -replace './demoCA', '$ENV::CA_HOME' } | `
  Set-Content "${SV_CONF}"

@"

[ v3_self_server ]
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid,issuer
subjectAltName=DNS:${SV_NAME}.${DOMAIN},DNS:${SV_NAME},DNS:localhost,IP:127.0.0.1
"@ | ForEach-Object{ $_ + "`n" } | ForEach-Object{ [Text.Encoding]::UTF8.GetBytes($_) } `
| Add-Content -Encoding Byte -Path "${SV_CONF}"

## Create server cert
Start-Process -Wait -NoNewWindow -FilePath "${OPENSSL}" -ArgumentList @(
  'genrsa', '-out', "`"${env:CA_HOME}\private\${SV_CERT_NAME}.key`"", '2048'
)

Start-Process -Wait -NoNewWindow -FilePath "${OPENSSL}" -ArgumentList @(
  'req', '-new' `
 ,'-config', "`"${SV_CONF}`"" `
 ,'-key', "`"${env:CA_HOME}\private\${SV_CERT_NAME}.key`"" `
 ,'-out', "`"${env:CA_HOME}\certs\${SV_CERT_NAME}.csr`"" `
 ,'-subj', "`"${SV_SUBJ}`""
)

Start-Process -Wait -NoNewWindow -FilePath "${OPENSSL}" -ArgumentList @(
  'ca', '-batch', '-extensions', 'v3_self_server' `
, '-config', "`"${SV_CONF}`"" `
, '-cert', "`"${env:CA_HOME}\certs\${CA_CERT_NAME}.crt`"" `
, '-keyfile', "`"${env:CA_HOME}\private\${CA_CERT_NAME}.key`"" `
, '-passin', "file:`"${env:CA_HOME}\.capass`"" `
, '-out', "`"${env:CA_HOME}\certs\${SV_CERT_NAME}.crt`"" `
, '-days', "${SV_DAYS}" `
, '-infiles', "`"${env:CA_HOME}\certs\${SV_CERT_NAME}.csr`""
)

Read-Host "Press enter to exit."

# SIG # Begin signature block
# MIIGgQYJKoZIhvcNAQcCoIIGcjCCBm4CAQExDzANBglghkgBZQMEAgEFADB5Bgor
# BgEEAYI3AgEEoGswaTA0BgorBgEEAYI3AgEeMCYCAwEAAAQQH8w7YFlLCE63JNLG
# KX7zUQIBAAIBAAIBAAIBAAIBADAxMA0GCWCGSAFlAwQCAQUABCAghCYgvacBtUOi
# HxpzN7a3ez93T2xZZN7eehhO+bAjsaCCA88wggPLMIICs6ADAgECAgEGMA0GCSqG
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
# FjAvBgkqhkiG9w0BCQQxIgQgFIuF/8K5uIFqD3sxyNnsk6acO1+HnA4Z+0sTGSyP
# lJcwDQYJKoZIhvcNAQEBBQAEggEAyeq4TVsRqd881UmVUltp6Hc191Ips8rMopwU
# +FllphQaDEdupWa7xUv5uIKkV2HjDHD2zDxSiiI/jsMfr9ggK0BSJJzSntGGrW2l
# BLr2qEwonnB2uz96YnsfBTecaBWJ0dreaYPUNYZTF/T8AaqQDRbvS3p6292AhxMk
# ai1nKP6BNqCT3/QO2mtRLxpaPvhC0HQ+QvGsg5X5thEME1sHxi5w8KtybpJNaOFA
# nxW5qbgEfSihYEBuC1shI8oD69n8GnccWChVDQ2ogvTkvIgMU8mHjMqrGiHoaTvl
# CnuYnbOom+wSmFWG7VT83JBqf4TGCSPvtD5xP2KoHVv0MGh3HA==
# SIG # End signature block
