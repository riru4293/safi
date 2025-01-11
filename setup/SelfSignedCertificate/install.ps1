#Requires -Version 7
$ErrorActionPreference = 'Stop'

# Basic variables
${PREFIX} = "${env:LOCALAPPDATA}\Programs\OpenSSL"
${VER} = "3.4.0"
${DEST} = "${PREFIX}\${VER}"
${CONF} = "${DEST}\ssl\openssl.cnf"
${COUNTRY} = 'JP'
${STATION} = 'Osaka'
${ORG} = 'project-k'
${DOMAIN} = 'project-k.mydns.jp'

# Environment variables
${env:PATH} = "${DEST}\$( [System.Environment]::Is64BitProcess ? 'x64' : 'x86' )\bin" + ';' + "${env:PATH}"
${env:CA_HOME} = "${env:LOCALAPPDATA}\CA"

# OpenSSL source
# See: https://kb.firedaemon.com/support/solutions/articles/4000121705
${SRC} = "https://download.firedaemon.com/FireDaemon-OpenSSL/openssl-${VER}.zip"
${SRC_HASH} = 'FFF7C56C1588644A609F63F60FAD15275E2CE6FFC310C320CDCB72554449CCA5'.ToUpper()
${HASH_ALG} = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
${ZIP} = "${env:TMP}\$( Split-Path -Path ${SRC} -Leaf )"

# CA configuration
${CA_PASS} = 'Please change'
${CA_CONF} = "${env:CA_HOME}\openssl.ca.cnf"
${CA_SUBJ} = "/C=${COUNTRY}/ST=${STATION}/O=${ORG}/CN=ca.${DOMAIN}"
${CA_CERT_NAME} = "ca.${ORG}"
${CA_DAYS} = 3650

# Server configuration
${SV_DAYS} = 1000
${SV_NAMES} = @(
  @{ NAME = ${env:COMPUTERNAME}.ToLower() }
  @{ NAME = 'cure'      }
  @{ NAME = 'blizzard'  }
  @{ NAME = 'poison'    }
  @{ NAME = 'toad'      }
  @{ NAME = 'meteor'    }
)
# --------------------------------------------------


# Abort if already exists a CA
if( Test-Path "${env:CA_HOME}" )
{
  Write-Host "Abort because already exists a CA. [ ${env:CA_HOME} ]"
  Read-Host "Press enter to exit."
  exit 9
}


# To be running again as administrators if not administrators.
${u} = [Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()

if ( -Not( ${u} ).IsInRole( 'Administrators' ) ) {
  Start-Process -Verb RunAs -FilePath pwsh -ArgumentList @(
    '-ExecutionPolicy', 'RemoteSigned', '-File', "`"${PSCommandPath}`""
  )
  exit
}


# Download OpenSSL
${d} = -Not( Test-Path "${ZIP}" ) -or -Not( "${SRC_HASH}" -eq ( Get-FileHash -Path "${ZIP}" -Algorithm "${HASH_ALG}" ).Hash )

if( ${d} ) {
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


# Uninstall CA if exists
${c} = Get-ChildItem -Path Cert:\LocalMachine\Root | Where-Object { $_.Subject -like "*CN=ca.${DOMAIN},*" }

if ( ${c} ) {
  ${c} | ForEach-Object{ Remove-Item -Path "Cert:\LocalMachine\Root\$( $_.Thumbprint )" }
}


# Create CA directories
New-Item -Type Directory -Path "${env:CA_HOME}\private"
New-Item -Type Directory -Path "${env:CA_HOME}\crl"
New-Item -Type Directory -Path "${env:CA_HOME}\certs"
New-Item -Type Directory -Path "${env:CA_HOME}\newcerts"
New-Item -Type File -Path "${env:CA_HOME}\index.txt"
Set-Content -Value ( [Text.Encoding]::UTF8.GetBytes( '01' ) ) -AsByteStream -Path "${env:CA_HOME}\serial"
Set-Content -Value ( [Text.Encoding]::UTF8.GetBytes( "${CA_PASS}" ) ) -AsByteStream -Path "${env:CA_HOME}\.capass"


# OpenSSL configuration for CA
$( Get-Content "${CONF}" ) | `
  ForEach-Object { $_ -replace './demoCA', '$ENV::CA_HOME' } | `
  Set-Content "${CA_CONF}"

@"

[ v3_self_ca ]
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid:always,issuer
basicConstraints = CA:true
keyUsage = cRLSign, keyCertSign
"@ | ForEach-Object{ [Text.Encoding]::UTF8.GetBytes( $_ + "`n" ) } | Add-Content -AsByteStream -Path "${CA_CONF}"


# Create CA cert
openssl req -new -x509 -extensions v3_self_ca -config "${CA_CONF}" -subj "${CA_SUBJ}" `
-keyout "${env:CA_HOME}\private\${CA_CERT_NAME}.key" -out "${env:CA_HOME}\certs\${CA_CERT_NAME}.crt" `
-passout file:"${env:CA_HOME}\.capass" -days ${CA_DAYS}

openssl x509 -in "${env:CA_HOME}\certs\${CA_CERT_NAME}.crt" -text


# Install CA
Import-Certificate -FilePath "${env:CA_HOME}\certs\${CA_CERT_NAME}.crt" -CertStoreLocation Cert:\LocalMachine\Root


# Create servers certs
foreach ( ${s} in ${SV_NAMES} ) {
  ${n} = ${s}.NAME

  # OpenSSL configuration for server
  Get-Content "${CONF}" | ForEach-Object { $_ -replace './demoCA', '$ENV::CA_HOME' } `
  | Set-Content -Path "${env:CA_HOME}\openssl.${n}.cnf"

  @"

[ v3_self_server ]
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid,issuer
subjectAltName=DNS:${n}.${DOMAIN},DNS:${n},DNS:localhost,IP:127.0.0.1
"@ `
  | ForEach-Object{ [Text.Encoding]::UTF8.GetBytes( $_ + "`n" ) } `
  | Add-Content -AsByteStream -Path "${env:CA_HOME}\openssl.${n}.cnf"

  ## Create server cert
  openssl genrsa -out "${env:CA_HOME}\private\${n}.${ORG}.key" 2048

  openssl req -new -config "${env:CA_HOME}\openssl.${n}.cnf" -key "${env:CA_HOME}\private\${n}.${ORG}.key" `
  -out "${env:CA_HOME}\certs\${n}.${ORG}.csr" -subj "/C=${COUNTRY}/ST=${STATION}/O=${ORG}/CN=${n}.${DOMAIN}"

  openssl ca -batch -extensions v3_self_server -config "${env:CA_HOME}\openssl.${n}.cnf" `
  -cert "${env:CA_HOME}\certs\${CA_CERT_NAME}.crt" ` -keyfile "${env:CA_HOME}\private\${CA_CERT_NAME}.key" `
  -passin file:"${env:CA_HOME}\.capass" -out "${env:CA_HOME}\certs\${n}.${ORG}.crt" -days ${SV_DAYS} `
  -infiles "${env:CA_HOME}\certs\${n}.${ORG}.csr"
}

Read-Host "Press enter to exit."
