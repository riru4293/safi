#Requires -Version 7
${ErrorActionPreference} = 'Continue'

# OpenSSL configuration
${VER} = '3.5.1'
${PREFIX} = "${env:LocalOpt}\OpenSSL"
${DEST} = "${PREFIX}\${VER}"


# See: https://kb.firedaemon.com/support/solutions/articles/4000121705
${SRC} = "https://download.firedaemon.com/FireDaemon-OpenSSL/openssl-${VER}.zip"
${ZIP} = "${env:TMP}\$( Split-Path -Path ${SRC} -Leaf )"
${SRC_HASH} = '8C79BB13DE52EB90840664CC84E458CA983C961932E042157C5FBA8DFCC6C1C8'.ToUpper()
${HASH_ALG} = 'SHA256' # [MD5|SHA1|SHA256|SHA512]


# To be running again as administrator if not administrator.
function IsAdmin { ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([System.Security.Principal.WindowsBuiltInRole]::Administrator) }
if ( -not ( IsAdmin ) ) { Start-Process -Verb RunAs -FilePath pwsh -ArgumentList @('-File', "`"${PSCommandPath}`""); exit }`


:mainLoop do {

    Write-Host "Install the OpenSSL-${VER} to [ ${DEST} ]."
    ${res} = Read-Host 'Continue? (Y/N)'
    if ( -not ( ( ${res} -eq 'Y' ) -or ( ${res} -eq 'y' ) ) ) { Write-Host 'Aborted.'; break :mainLoop }

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
