$ErrorActionPreference = "Stop"

if (!([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole("Administrators")) { Start-Process powershell.exe "-ExecutionPolicy RemoteSigned -File `"$PSCommandPath`"" -Verb RunAs; exit }

$VER = '1.26.2'
$WSW_VER = '2.12.0'

$PREFIX = "$env:LOCALAPPDATA\Programs\Nginx"
$ORIGIN_NAME = "nginx-${VER}"
$NAME = "${VER}"
$DEST = "${PREFIX}\${NAME}"

$SVC_NAME = 'SAFI.Nginx'
$SVC = $null

$SRC = "https://nginx.org/download/nginx-${VER}.zip"
$SRC_HASH = '942638CC31C836FE429FDE22E4C46E497F89D3C2BBCE46CD2C8800854FD71409'
$HASH_ALG = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
$ZIP = "$env:TMP\$(${SRC}.Substring(${SRC}.LastIndexOf("/") + 1))"

# https://github.com/winsw/winsw/releases
$WSW_NAME = 'WinSW.NET461.exe'
$WSW_ASSETS = "https://api.github.com/repos/winsw/winsw/releases/tags/v${WSW_VER}"
$WSW_SRC = 'Get it later with GitHub API'
$WSW_SRC_HASH = 'B5066B7BBDFBA1293E5D15CDA3CAAEA88FBEAB35BD5B38C41C913D492AADFC4F'
$WSW_HASH_ALG = 'SHA256' # [MD5|SHA1|SHA256|SHA512]
$WSW_EXE = "$env:TMP\${WSW_NAME}"

$NGINX_HOME = "${PREFIX}\primary"

# Remove an existing service
$ErrorActionPreference = "Continue"
$Svc = Get-Service -Name ${SVC_NAME} 2> $null
# $ErrorActionPreference = "Stop"


if( ${Svc} -ne $null ) {
  
  if ( ${Svc}.Status -eq 'Running' ) {
    Stop-Service -Name ${SVC_NAME}
  }
  $S = Get-WmiObject win32_service | Where-Object {$_.Name -eq ${SVC_NAME}}
  $S.delete()
}


# Download Nginx
$RequireDownload = -Not( Test-Path "${ZIP}" ) `
  -or ${SRC_HASH}.ToUpper() -ne ( Get-FileHash -Path "${ZIP}" -Algorithm ${HASH_ALG} ).Hash

if( ${RequireDownload} ) {
  Write-Output "Downloading to ${ZIP} from ${SRC}"
  Invoke-WebRequest -Uri "${SRC}" -outfile "${ZIP}"
} else {
  Write-Output "Skip download Nginx. Because already exists."
}

# Download WinSW
$WswR = Invoke-RestMethod -Uri ${WSW_ASSETS} -Headers @{ "User-Agent" = "PowerShell" }
$WswA = ${WswR}.assets | Where-Object { $_.name -eq ${WSW_NAME} }
$WSW_SRC = ${WswA}.browser_download_url

$RequireDownload = -Not( Test-Path "${WSW_EXE}" ) `
  -or ${WSW_SRC_HASH}.ToUpper() -ne ( Get-FileHash -Path "${WSW_EXE}" -Algorithm ${WSW_HASH_ALG} ).Hash

if( ${RequireDownload} ) {
  Write-Output "Downloading to ${WSW_EXE} from ${WSW_SRC}"
  Invoke-WebRequest -Uri "${WSW_SRC}" -outfile "${WSW_EXE}" -Headers @{ "User-Agent" = "PowerShell" }
} else {
  Write-Output "Skip download WinSW. Because already exists."
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
    include $(${NGINX_HOME}.Replace( '\', '/' ) )/conf/conf.d/*.conf;
}
"@ | Out-File -enc default "${NGINX_HOME}\conf\nginx.conf"

@"
include $(${NGINX_HOME}.Replace( '\', '/' ) )/conf/conf.d/upstream/*.conf;

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
ssl_certificate     $(${env:LOCALAPPDATA}.Replace( '\', '/' ) )/CA/certs/$( ${env:COMPUTERNAME}.ToLower() ).project-k.crt;
ssl_certificate_key $(${env:LOCALAPPDATA}.Replace( '\', '/' ) )/CA/private/$( ${env:COMPUTERNAME}.ToLower() ).project-k.key;
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

Read-Host XXX
