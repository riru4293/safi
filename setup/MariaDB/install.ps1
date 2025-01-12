${ErrorActionPreference} = "Stop"

${PREFIX} = "${env:LOCALAPPDATA}\Programs\MariaDB"
${VER} = '11.6.2'
${ORIGIN_NAME} = "mariadb-${VER}-winx64"
${NAME} = "${VER}"
${DEST} = "${PREFIX}\${NAME}"
${SRC} = "https://mirrors.xtom.jp/mariadb//mariadb-${VER}/winx64-packages/mariadb-${VER}-winx64.zip"
${SRC_HASH} = 'FDA36DC30BD10979BA1496D08C2AEE4549833A09BF26AA914B653AB792A9ACBE'
${HASH_ALG} = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
${ZIP} = "${env:TMP}\$(${SRC}.Substring(${SRC}.LastIndexOf("/") + 1))"

# https://mirror.mariadb.org/zoneinfo/
${TZ_SRC} = "https://mirrors.xtom.jp/mariadb/zoneinfo/zoneinfo.zip"
${TZ_SRC_HASH} = '2bac8646c92314f8d6c30a0e05bce8355091a7bbb0b6b7b058c9363378c9597a'
${TZ_HASH_ALG} = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
${TZ_ZIP} = "${env:TMP}\$(${TZ_SRC}.Substring(${TZ_SRC}.LastIndexOf("/") + 1))"
${TZ_SQL} = "${DEST}\zoneinfo.sql"


# Download
${RequireDownload} = -Not( Test-Path "${ZIP}" ) `
  -or ${SRC_HASH}.ToUpper() -ne ( Get-FileHash -Path "${ZIP}" -Algorithm ${HASH_ALG} ).Hash

if( ${RequireDownload} ) {
  Write-Output "Downloading to ${ZIP} from ${SRC}"
  Invoke-WebRequest -Uri "${SRC}" -outfile "${ZIP}"
} else {
  Write-Output "Skip download MariaDB. Because already exists."
}


# Download time zones
${RequireDownload} = -Not( Test-Path "${TZ_ZIP}" ) `
  -or ${TZ_SRC_HASH}.ToUpper() -ne ( Get-FileHash -Path "${TZ_ZIP}" -Algorithm ${TZ_HASH_ALG} ).Hash

if( ${RequireDownload} ) {
  Write-Output "Downloading to ${TZ_ZIP} from ${TZ_SRC}"
  Invoke-WebRequest -Uri "${TZ_SRC}" -outfile "${TZ_ZIP}"
} else {
  Write-Output "Skip download time zones. Because already exists."
}


# Extract
Write-Output "Extracting to ${PREFIX} from ${ZIP}"
if( Test-Path "${PREFIX}\${ORIGIN_NAME}" ) { Remove-Item "${PREFIX}\${ORIGIN_NAME}" -Recurse -Force }
if( Test-Path "${DEST}" ) { Remove-Item "${DEST}" -Recurse -Force }
Expand-Archive -Path "${ZIP}" -DestinationPath "${PREFIX}"
Rename-Item -Path "${PREFIX}\${ORIGIN_NAME}" -NewName "${NAME}"


# Extract time zones
Write-Output "Extracting to ${DEST} from ${TZ_ZIP}"
if( Test-Path "${TZ_SQL}" ) { Remove-Item "${TZ_SQL}" -Force }
Expand-Archive -Path "${TZ_ZIP}" -DestinationPath "${DEST}"


# Create junctions
if( Test-Path "${PREFIX}\primary" ) { ( Get-Item "${PREFIX}\primary" ).Delete() }
New-Item -Path "${PREFIX}" -Name primary -Value "${DEST}" -ItemType Junction
