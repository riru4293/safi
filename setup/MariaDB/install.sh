#!/bin/sh

# Execute as root
if [ $(id -u) -ne 0 ]; then
  sudo $0
  exit $?
fi


# Configuration
ORG='project-k'
CA_CERT_NAME="ca.${ORG}"
SV_CERT_NAME="$(hostname).${ORG}"


# For latest
if [ ! -e /etc/apt/sources.list.d/mariadb.list ]; then
    curl -LsS https://downloads.mariadb.com/MariaDB/mariadb_repo_setup \
    | sudo bash -s -- --os-type=ubuntu --os-version=jammy
fi


# Install MariaDB
systemctl --now disable mariadb.service
sudo apt update
sudo apt install mariadb-server


# Generate TDE keys
if [ ! -d /etc/mysql/tde-keys ]; then
    TDE_KEYS="/etc/mysql/tde-keys"

    if [ ! -d "${TDE_KEYS}" ]; then mkdir "${TDE_KEYS}"; fi

    for i in 1 2 3 4 5 6 7 8 9; do echo $i';'$(openssl rand -hex 32); done \
    | tee "${TDE_KEYS}/tdekeys.txt"

    openssl rand -hex 128 > "${TDE_KEYS}/tdekeys.key"

    openssl enc -aes-256-cbc -md sha1 -pass file:$(readlink -f "${TDE_KEYS}/tdekeys.key") \
    -in "${TDE_KEYS}/tdekeys.txt" -out "${TDE_KEYS}/tdekeys.enc"

    rm "${TDE_KEYS}/tdekeys.txt"

    chmod 0550 /etc/mysql/tde-keys
    chmod 0440 /etc/mysql/tde-keys/*
    chown -R mysql:mysql /etc/mysql/tde-keys

    usermod -aG ssl-cert mysql

    # Reason: Traditional data is not encrypted.
    if [ -d /var/lib/mysql ]; then
        mv /var/lib/mysql /tmp/mysql.$(date +%Y%m%d_%H%M%S_%3N)
        mariadb-install-db --user=mysql --datadir=/var/lib/mysql
    fi
fi


# Create configurations
tee /etc/mysql/mariadb.conf.d/77-timeout.cnf <<'EOF'
[mysqld]
wait_timeout=1800
EOF

tee /etc/mysql/mariadb.conf.d/77-tls.cnf <<EOF
[mysqld]
ssl-ca   = /etc/ssl/certs/${CA_CERT_NAME}.crt
ssl-key  = /etc/ssl/private/${SV_CERT_NAME}.key
ssl-cert = /etc/ssl/certs/${SV_CERT_NAME}.crt
EOF

tee /etc/mysql/mariadb.conf.d/77-tde.cnf <<'EOF'
[mysqld]
plugin-load-add = file_key_management.so
file_key_management = FORCE_PLUS_PERMANENT
file_key_management_filename = /etc/mysql/tde-keys/tdekeys.enc
file_key_management_filekey = FILE:/etc/mysql/tde-keys/tdekeys.key
file_key_management_encryption_algorithm = AES_CBC

innodb_encrypt_tables = FORCE
innodb_encrypt_log = ON
innodb_encrypt_temporary_tables = ON
innodb_encryption_threads = 4
innodb_encryption_rotate_key_age = 1

encrypt_tmp_disk_tables = ON
encrypt_tmp_files = ON
encrypt_binlog = ON
EOF

tee /etc/mysql/mariadb.conf.d/77-global.cnf <<'EOF'
[mysqld]
transaction-isolation = READ-COMMITTED
innodb_buffer_pool_size=4G
innodb_rollback_on_timeout = TRUE
general_log=1
general_log_file=/var/log/mysql/general-query.log
EOF

tee /etc/mysql/mariadb.conf.d/77-charset.cnf <<'EOF'
[mysqld]
character-set-client-handshake = FALSE
character-set-server = utf8mb4
collation-server = utf8mb4_general_ci
[client]
default-character-set = utf8mb4
EOF

# Reason: Timezone doesn't exist yet
if [ -e /etc/mysql/mariadb.conf.d/77-timezone.cnf ]; then
    rm /etc/mysql/mariadb.conf.d/77-timezone.cnf
fi


# Install service
systemctl --now enable mariadb.service


# Import timezone info
mariadb-tzinfo-to-sql /usr/share/zoneinfo | mariadb mysql


# Configure timezone to GMT
tee /etc/mysql/mariadb.conf.d/77-timezone.cnf <<EOF
[mysqld]
default-time-zone='GMT'
EOF

systemctl restart mariadb.service
