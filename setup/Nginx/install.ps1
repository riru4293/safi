#Requires -Version 7
${ErrorActionPreference} = 'Stop'

${VER} = '1.26.2'
${WSW_VER} = '2.12.0'
${SVC_NAME} = 'SAFI.Nginx'
${ORG} = 'project-k'

${PREFIX} = "${env:LOCALAPPDATA}\Programs\Nginx"
${ORIGIN_NAME} = "nginx-${VER}"
${NAME} = "${VER}"
${DEST} = "${PREFIX}\${NAME}"

${SRC} = "https://nginx.org/download/nginx-${VER}.zip"
${SRC_HASH} = '942638CC31C836FE429FDE22E4C46E497F89D3C2BBCE46CD2C8800854FD71409'.ToUpper()
${HASH_ALG} = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
${ZIP} = "${env:TMP}\$( Split-Path -Path ${SRC} -Leaf )"

# https://github.com/winsw/winsw/releases
${WSW_NAME} = 'WinSW.NET461.exe'
${WSW_ASSETS} = "https://api.github.com/repos/winsw/winsw/releases/tags/v${WSW_VER}"
${WSW_SRC} = 'Get it later with GitHub API'
${WSW_SRC_HASH} = 'B5066B7BBDFBA1293E5D15CDA3CAAEA88FBEAB35BD5B38C41C913D492AADFC4F'.ToUpper()
${WSW_HASH_ALG} = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
${WSW_EXE} = "${env:TMP}\${WSW_NAME}"

${NGINX_HOME} = "${PREFIX}\primary"
${CA_HOME} = "${env:LOCALAPPDATA}\CA"
${SV_CERT_NAME} = "$( ${env:COMPUTERNAME}.ToLower() ).${ORG}"
# --------------------------------------------------


# To be running again as administrators if not administrators.
${u} = [Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()

if ( -Not( ${u} ).IsInRole( 'Administrators' ) ) {
  Start-Process -Verb RunAs -FilePath pwsh -ArgumentList @(
    '-ExecutionPolicy', 'RemoteSigned', '-File', "`"${PSCommandPath}`""
  )
  exit
}


# Remove an existing service
if ( ${s} = Get-Service -Name ${SVC_NAME} -ErrorAction SilentlyContinue ) {
  
  if ( ${s}.Status -eq 'Running' ) {
    Stop-Service -Name ${SVC_NAME}
  }

  Remove-Service -Name ${SVC_NAME}
  Write-Host 'Removed existng service'
}


# Download Nginx
${d} = -Not( Test-Path "${ZIP}" ) -or -Not( "${SRC_HASH}" -eq ( Get-FileHash -Path "${ZIP}" -Algorithm "${HASH_ALG}" ).Hash )

if( ${d} ) {
  Write-Host "Downloading to ${ZIP} from ${SRC}"
  Invoke-WebRequest -Uri "${SRC}" -outfile "${ZIP}"
} else {
  Write-Host 'Skip download Nginx. Because already exists.'
}


# Download WinSW
${r} = Invoke-RestMethod -Uri ${WSW_ASSETS} -Headers @{ 'User-Agent' = 'PowerShell' }
${a} = ${r}.assets | Where-Object { $_.name -eq ${WSW_NAME} }
${WSW_SRC} = ${a}.browser_download_url

${d} = -Not( Test-Path "${WSW_EXE}" ) `
-or -Not( "${WSW_SRC_HASH}" -eq ( Get-FileHash -Path "${WSW_EXE}" -Algorithm "${WSW_HASH_ALG}" ).Hash )

if( ${d} ) {
  Write-Host "Downloading to ${WSW_EXE} from ${WSW_SRC}"
  Invoke-WebRequest -Uri "${WSW_SRC}" -outfile "${WSW_EXE}" -Headers @{ 'User-Agent' = 'PowerShell' }
} else {
  Write-Host 'Skip download WinSW. Because already exists.'
}


# Extract Nginx
Write-Output "Extracting to ${PREFIX} from ${ZIP}"
if( Test-Path "${PREFIX}\${ORIGIN_NAME}" ) { Remove-Item "${PREFIX}\${ORIGIN_NAME}" -Recurse -Force }
if( Test-Path "${DEST}" ) { Remove-Item "${DEST}" -Recurse -Force }
Expand-Archive -Path "${ZIP}" -DestinationPath "${PREFIX}"
Rename-Item -Path "${PREFIX}\${ORIGIN_NAME}" -NewName "${NAME}"


# Copy WinSW
Write-Output "Copy to ${DEST}\nginx_service.exe from ${WSW_EXE}"
Copy-Item -Path ${WSW_EXE} -Destination ${DEST}\nginx_service.exe


# Create junctions
if( Test-Path "${PREFIX}\primary" ) { ( Get-Item "${PREFIX}\primary" ).Delete() }
New-Item -Path "${PREFIX}" -Name primary -Value "${DEST}" -ItemType Junction


# Configuration
New-Item -ItemType Directory -Path "${NGINX_HOME}\conf\conf.d\tls-server"
New-Item -ItemType Directory -Path "${NGINX_HOME}\conf\conf.d\upstream"

@"
worker_processes  1;

events {
    worker_connections  1024;
}

http {
    include         mime.types;
    default_type    application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    ##
    # Virtual Host Configs
    ##
    include $( ${NGINX_HOME}.Replace( '\', '/' ) )/conf/conf.d/*.conf;
}
"@ | Out-File -enc default "${NGINX_HOME}\conf\nginx.conf"

@"
include $( ${NGINX_HOME}.Replace( '\', '/' ) )/conf/conf.d/upstream/*.conf;

server {
    listen 80 default_server;
    listen [::]:80 default_server;
    return 301 https://`$host`$request_uri;
}

server {
    client_max_body_size 100M;
    proxy_read_timeout 1800;
    listen 443 ssl default_server;
    listen [::]:443 ssl default_server;
    root /var/www/html;
    index index.html;
    server_name _;
    location / {
        # First attempt to serve request as file, then
        # as directory, then fall back to displaying a 404.
        try_files `$uri `$uri/ =404;
    }
    include $(${NGINX_HOME}.Replace( '\', '/' ) )/conf/conf.d/tls-server/*.conf;
}
"@ | Out-File -enc default "${NGINX_HOME}\conf\conf.d\base.conf"

@"
ssl_certificate     $( ${CA_HOME}.Replace( '\', '/' ) )/certs/${SV_CERT_NAME}.crt;
ssl_certificate_key $( ${CA_HOME}.Replace( '\', '/' ) )/private/${SV_CERT_NAME}.key;
"@ | Out-File -enc default "${NGINX_HOME}\conf\conf.d\tls-server\cert.conf"

@"
location /safi/ {
    proxy_pass http://safi-host/safi/;
}
"@ | Out-File -enc default "${NGINX_HOME}\conf\conf.d\tls-server\safi-tls-server.conf"

@"
upstream safi-host {
    server 127.0.0.1:8080;
}
"@ | Out-File -enc default "${NGINX_HOME}\conf\conf.d\upstream\safi-upstream.conf"

# Install Service
@"
<service>
  <id>${SVC_NAME}</id>
  <name>Nginx</name>
  <description>Web Server, Reverse Proxy</description>
  <executable>nginx.exe</executable>
  <logmode>rotate</logmode>
  <startarguments></startarguments>
  <stoparguments>-s stop</stoparguments>
  <startmode>Automatic</startmode>
</service>
"@ | Out-File -enc default "${NGINX_HOME}\nginx_service.xml"

& "${NGINX_HOME}\nginx_service.exe" install
Start-Service "${SVC_NAME}"

Read-Host "Press enter to exit."

# SIG # Begin signature block
# MIIGgQYJKoZIhvcNAQcCoIIGcjCCBm4CAQExDzANBglghkgBZQMEAgEFADB5Bgor
# BgEEAYI3AgEEoGswaTA0BgorBgEEAYI3AgEeMCYCAwEAAAQQH8w7YFlLCE63JNLG
# KX7zUQIBAAIBAAIBAAIBAAIBADAxMA0GCWCGSAFlAwQCAQUABCAJmXzSCEz3rW+L
# OBRZn+wyqropw8v/muWwz3AlEha5p6CCA88wggPLMIICs6ADAgECAgEGMA0GCSqG
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
# FjAvBgkqhkiG9w0BCQQxIgQgCFJjwIhcV8u3OmF7Eq5dcELzyyhhiA6o5e8qbUfC
# uYgwDQYJKoZIhvcNAQEBBQAEggEAQtGhln2ACCyDOX1Ci6enFDRnnZF6BjSIp+dU
# vRBcBeh6JW4qZSMpAdinN9ypMNBAj1VrCOWdxzRoYppzcrCqr53RDrDNhXaOyI8W
# rIycqi8lIAh4WgWHTMMxk/bQeIFDame3Z/WmiSawpFyeYXFCHO5ApaZT/patkLAi
# 9CjqXXSliZzfxIbiWqEPPP0w0qCHzoGKFT7kRdzYhQ/XtkhC/RestPRVsN6G5JNo
# q5DrZ2mH1EwsBSbIi2HluxFd7pDrDzNHX6oOHv9WBb9RJdJPT0VTAPzuLzEaZrvB
# qOh/yQQ4h/WF4MTnYoUSqZi6CSc0Bljrv9wxKQWd+gSs41EXAw==
# SIG # End signature block
