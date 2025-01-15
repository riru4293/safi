#!/bin/bash

# Basic variables
PREFIX='/opt'
CONF="$(openssl version -a | awk -F'\"' '/OPENSSLDIR/ { print $2 }')"'/openssl.cnf'
COUNTRY='JP'
STATION='Osaka'
ORG='Project-K'
DOMAIN='project-k.mydns.jp'

# Environment variables
export CA_HOME=${CA_HOME:=${PREFIX}/CA}

# CA configuration
CA_PASS="${CA_PASS:=Please change}"
CA_CONF="${CA_HOME}/openssl.ca.cnf"
CA_SUBJ="/C=${COUNTRY}/ST=${STATION}/O=${ORG}/CN=ca.${DOMAIN}"
CA_CERT_NAME="ca.${ORG,,}"
CA_DAYS=3650

# Server configuration
SV_DAYS=1000
SV_NAMES=("
  $(hostname)
")
# --------------------------------------------------


# Execute as root
if [ $(id -u) -ne 0 ]; then sudo -E "$0"; exit $?; fi


# Install OpenSSL if not exists
if [ ! $(which openssl) ]; then 
  apt update && apt install openssl
fi


# Install ssl-cert if not exists
if [ ! $(which make-ssl-cert) ]; then
  apt update && apt install ssl-cert
fi


# Uninstall CA if exists
if [ -e "/usr/local/share/ca-certificates/${CA_CERT_NAME}.crt" ]; then
  rm "/usr/local/share/ca-certificates/${CA_CERT_NAME}.crt"
  update-ca-certificates
fi

if [ -e "/etc/ssl/certs/${CA_CERT_NAME}.crt" ]; then
  rm "/etc/ssl/certs/${CA_CERT_NAME}.crt"
fi

if [ -e "/etc/ssl/private/${CA_CERT_NAME}.key" ]; then
  rm "/etc/ssl/private/${CA_CERT_NAME}.key"
fi


# Create CA directories
dir="${CA_HOME}";          [ ! -e "${dir}"  ] && mkdir -p "${dir}"
dir="${CA_HOME}/private";  [ ! -e "${dir}"  ] && mkdir -p "${dir}"
dir="${CA_HOME}/crl";      [ ! -e "${dir}"  ] && mkdir -p "${dir}"
dir="${CA_HOME}/certs";    [ ! -e "${dir}"  ] && mkdir -p "${dir}"
dir="${CA_HOME}/newcerts"; [ ! -e "${dir}"  ] && mkdir -p "${dir}"
touch "${CA_HOME}/index.txt"
file="${CA_HOME}/serial";  [ ! -e "${file}" ] && echo 01 > "${file}"
file="${CA_HOME}/capass";  [ ! -e "${file}" ] && echo ${CA_PASS} > "${file}"


if [ ! -e "${CA_HOME}/certs/${CA_CERT_NAME}.crt" ]; then

  # OpenSSL configuration for CA
  sed -e 's@./demoCA@$ENV::CA_HOME@' "${CONF}" > "${CA_CONF}"

  tee -a "${CA_CONF}" <<'EOF'

[ v3_self_ca ]
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid:always,issuer
basicConstraints = CA:true
keyUsage = cRLSign, keyCertSign
EOF

  # Create CA cert
  openssl req -new -x509 -extensions v3_self_ca -config "${CA_CONF}" -subj "${CA_SUBJ}" \
    -keyout "${CA_HOME}/private/${CA_CERT_NAME}.key" -out "${CA_HOME}/certs/${CA_CERT_NAME}.crt" \
    -passout file:"${CA_HOME}/.capass" -days ${CA_DAYS}

  openssl x509 -in "${CA_HOME}/certs/${CA_CERT_NAME}.crt" -text
fi


# Install CA
cp "${CA_HOME}/certs/${CA_CERT_NAME}.crt" '/usr/local/share/ca-certificates/'
update-ca-certificates

if [ -e "${CA_HOME}/private/${CA_CERT_NAME}.key" ]; then
  cp "${CA_HOME}/certs/${CA_CERT_NAME}.crt" "/etc/ssl/certs/${CA_CERT_NAME}.crt"
  chmod 444 "/etc/ssl/certs/${CA_CERT_NAME}.crt"
  chown root:ssl-cert "/etc/ssl/certs/${CA_CERT_NAME}.crt"

  cp "${CA_HOME}/private/${CA_CERT_NAME}.key" "/etc/ssl/private/${CA_CERT_NAME}.key"
  chmod 440 "/etc/ssl/private/${CA_CERT_NAME}.key"
  chown root:ssl-cert "/etc/ssl/private/${CA_CERT_NAME}.key"
fi


# Create servers cert
for n in ${SV_NAMES[@]}; do

  if [ ! -e "${CA_HOME}/certs/${n}.${ORG,,}.crt" ]; then

    # OpenSSL configuration for server
    c="${CA_HOME}/openssl.${n}.cnf"

    sed -e 's@./demoCA@$ENV::CA_HOME@' "${CONF}" > "${c}"

    tee -a "${c}" <<EOF

[ v3_self_server ]
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment, keyAgreement
extendedKeyUsage = serverAuth, clientAuth
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid,issuer
subjectAltName=DNS:${n}.${DOMAIN},DNS:${n},DNS:localhost,IP:127.0.0.1

EOF

    ## Create server cert
    openssl genrsa -out "${CA_HOME}/private/${n}.${ORG,,}.key" 2048

    openssl req -new -config "${c}" -key "${CA_HOME}/private/${n}.${ORG,,}.key" \
      -out "${CA_HOME}/certs/${n}.${ORG,,}.csr" -subj "/C=${COUNTRY}/ST=${STATION}/O=${ORG}/CN=${n}.${DOMAIN}"

    openssl ca -batch -extensions v3_self_server -config "${c}" \
      -cert "${CA_HOME}/certs/${CA_CERT_NAME}.crt" -keyfile "${CA_HOME}/private/${CA_CERT_NAME}.key" \
      -passin file:"${CA_HOME}/.capass" -out "${CA_HOME}/certs/${n}.${ORG,,}.crt" -days ${SV_DAYS} \
      -infiles "${CA_HOME}/certs/${n}.${ORG,,}.csr"
  fi

  # Install server cert
  cp "${CA_HOME}/certs/${n}.${ORG,,}.crt" "/etc/ssl/certs/${n}.${ORG,,}.crt"
  chmod 444 "/etc/ssl/certs/${n}.${ORG,,}.crt"
  chown root:ssl-cert "/etc/ssl/certs/${n}.${ORG,,}.crt"

  cp "${CA_HOME}/private/${n}.${ORG,,}.key" "/etc/ssl/private/${n}.${ORG,,}.key"
  chmod 440 "/etc/ssl/private/${n}.${ORG,,}.key"
  chown root:ssl-cert "/etc/ssl/private/${n}.${ORG,,}.key"
done

