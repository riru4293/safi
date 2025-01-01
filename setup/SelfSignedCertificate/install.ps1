$ErrorActionPreference = 'Stop'

$VER = "3.4.0"
$PREFIX = "$env:LOCALAPPDATA\Programs\OpenSSL"
$DEST = "${PREFIX}\${VER}"
$SRC = "https://download.firedaemon.com/FireDaemon-OpenSSL/openssl-${VER}.zip"
# See: https://kb.firedaemon.com/support/solutions/articles/4000121705
$SRC_HASH = 'FFF7C56C1588644A609F63F60FAD15275E2CE6FFC310C320CDCB72554449CCA5'
$HASH_ALG = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
$ZIP = "$env:TMP\$(${SRC}.Substring(${SRC}.LastIndexOf("/") + 1))"
$OPENSSL_CONF = "${PREFIX}\primary\ssl\openssl.cnf"
$CA_DIR = "$env:LOCALAPPDATA\CA".Replace('\', '/')


# Download
$RequireDownload = -Not( Test-Path "${ZIP}" ) `
  -or $SRC_HASH.ToUpper() -ne ( Get-FileHash -Path "${ZIP}" -Algorithm ${HASH_ALG} ).Hash

if( ${RequireDownload} ) {
  Write-Output "Downloading to ${ZIP} from ${SRC}"
  Invoke-WebRequest -Uri "${SRC}" -outfile "${ZIP}"
} else {
  Write-Output "Skip download. Because already exists."
}


# Extract
Write-Output "Extracting to ${DEST} from ${ZIP}"
if( Test-Path "${DEST}" ) { Remove-Item "${DEST}" -Recurse -Force }
Expand-Archive -Path "${ZIP}" -DestinationPath "${DEST}"


# Create junctions
if( Test-Path "${PREFIX}\primary" ) { ( Get-Item "${PREFIX}\primary" ).Delete() }
New-Item -Path "${PREFIX}" -Name primary -Value "${DEST}" -ItemType Junction
