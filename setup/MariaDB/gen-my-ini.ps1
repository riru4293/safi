$ErrorActionPreference = "Stop"

$CA_HOME = "$env:LOCALAPPDATA\CA"
$SERVER_NAME = "$($($env:COMPUTERNAME).ToLower())"
$MARIA_HOME = "$env:LOCALAPPDATA\Programs\MariaDB\primary"
$MARIA_DATA = "${MARIA_HOME}\data"

@"
[mysqld]
transaction_isolation = READ-COMMITTED
default-time-zone='GMT'

## TLS
#
ssl-ca   = "$($(${CA_HOME}).Replace('\', '/'))/projectk-ca.crt"
ssl-key  = "$($(${CA_HOME}).Replace('\', '/'))/private/${SERVER_NAME}.key"
ssl-cert = "$($(${CA_HOME}).Replace('\', '/'))/${SERVER_NAME}.crt"

## timeout
#
wait_timeout=1800
interactive_timeout=1800


## innodb
#
innodb_rollback_on_timeout = TRUE

## charset
#
character-set-server=utf8mb4
collation-server=utf8mb4_general_ci

## encryption
#
plugin_load_add = file_key_management.dll
file_key_management = FORCE_PLUS_PERMANENT
file_key_management_filename = $($(${MARIA_HOME}).Replace('\', '/'))/tdekeys.enc
file_key_management_filekey = FILE:$($(${MARIA_HOME}).Replace('\', '/'))/tdekeys.key
file_key_management_encryption_algorithm = AES_CBC

innodb_encrypt_tables = FORCE
innodb_encrypt_log = ON
innodb_encrypt_temporary_tables = ON
innodb_encryption_threads = 4
innodb_encryption_rotate_key_age = 1

encrypt_tmp_disk_tables = ON
encrypt_tmp_files = ON
encrypt_binlog = ON


[client]
default-character-set = utf8mb4
"@ `
    | Out-String `
    | ForEach-Object { [Text.Encoding]::UTF8.GetBytes($_) } `
    | Set-Content -Path "${MARIA_DATA}\my.ini" -Encoding Byte
