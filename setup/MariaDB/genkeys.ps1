$ErrorActionPreference = "Stop"

$DEST = "$env:TMP\MariaDB-Key.$((Get-Date).ToString("yyyy-MM-dd"))"
$env:PATH = "$env:LOCALAPPDATA\Programs\OpenSSL\primary\x64\bin;" + $env:PATH

if( Test-Path "${DEST}" ) { Remove-Item "${DEST}" -Recurse -Force }
New-Item -ItemType Directory -Path "${DEST}"

Write-Host 'Create encryption keys'
$array = New-Object System.Collections.ArrayList
for ( $i=1; $i -lt 10; $i++ ) {
    $array.Add( ( [string]$i + ";" + ( openssl rand -hex 32 ) ) )
}
$array `
    | Out-String `
    | ForEach-Object { [Text.Encoding]::UTF8.GetBytes($_) } `
    | Set-Content -Path "${DEST}\tdekeys.txt" -Encoding Byte


Write-Host 'Generate random password'
openssl rand -hex 128 `
    | Out-String `
    | ForEach-Object { [Text.Encoding]::UTF8.GetBytes($_) } `
    | Set-Content -Path "${DEST}\tdekeys.key" -Encoding Byte


Write-Host 'Encode the encryption keys to aes-256-cbc'
openssl enc -aes-256-cbc -pbkdf2 -iter 91342 -md sha1 -pass file:"${DEST}\tdekeys.key" -in "${DEST}\tdekeys.txt" -out "${DEST}\tdekeys.enc"

Write-Host 'Generated keys'
Get-Content "${DEST}\tdekeys.txt"

Remove-Item -Path "${DEST}\tdekeys.txt"
