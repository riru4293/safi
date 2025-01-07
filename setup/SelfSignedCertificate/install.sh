#!/bin/sh

# Execute as root
if [ $(id -u) -ne 0 ]; then
  sudo $0
  exit $?
fi

PREFIX="$(getent passwd $(logname) | cut -d: -f6)/.local"
export CA_HOME="${PREFIX}/CA"

CA_PASS='CA password'
ORG_NAME='project-k'
DOMAIN="project-k.mydns.jp"
CA_NAME="ca$(if [ -n "$DOMAIN" ]; then echo .; fi)${DOMAIN}"
SERVER_NAME="$(hostname)"
SERVER_FULLNAME="${SERVER_NAME}$(if [ -n "$DOMAIN" ]; then echo .; fi)${DOMAIN}"
CA_CERT_NAME="ca$(if [ -n "$ORG_NAME" ]; then echo .; fi)${ORG_NAME}"
CA_SUBJ="/C=JP/ST=Osaka$(if [ -n "$ORG_NAME" ]; then echo /O=; fi)${ORG_NAME}/CN=${CA_NAME}"
SV_CERT_NAME="${SERVER_NAME}$(if [ -n "$ORG_NAME" ]; then echo .; fi)${ORG_NAME}"
SV_SUBJ="/C=JP/ST=Osaka$(if [ -n "$ORG_NAME" ]; then echo /O=; fi)${ORG_NAME}/CN=${SERVER_FULLNAME}"
OPENSSL_HOME="$(openssl version -a | awk -F'\"' '/OPENSSLDIR/ { print $2 }')"

apt update && apt install openssl

dir=${PREFIX}; [ ! -e $dir ] && mkdir -p "$dir"
sed -e 's@./demoCA@$ENV::CA_HOME@' ${OPENSSL_HOME}/openssl.cnf > ${PREFIX}/openssl.cnf
chown $(logname):$(id -gn $(logname)) ${PREFIX}/openssl.cnf

tee -a ${PREFIX}/openssl.cnf <<'EOF'
[ v3_self_ca ]
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid:always,issuer
basicConstraints = CA:true
keyUsage = cRLSign, keyCertSign
EOF

mkdir "${CA_HOME}"
mkdir "${CA_HOME}/private"
mkdir "${CA_HOME}/crl"
mkdir "${CA_HOME}/certs"
mkdir "${CA_HOME}/newcerts"
echo 01 > "${CA_HOME}/serial"
touch "${CA_HOME}/index.txt"

echo ${CA_PASS} > "${CA_HOME}/.capass"

openssl req \
    -new \
    -config   "${PREFIX}/openssl.cnf" \
    -keyout   "${CA_HOME}/private/${CA_CERT_NAME}.key" \
    -out      "${CA_HOME}/certs/${CA_CERT_NAME}.crt" \
    -passout  "file:${CA_HOME}/.capass" \
    -subj     "${CA_SUBJ}" \
    -x509 -days 3650 -extensions v3_self_ca

cp "${PREFIX}/openssl.cnf" "${CA_HOME}/openssl.${SERVER_NAME}.cnf"

tee -a ${CA_HOME}/openssl.${SERVER_NAME}.cnf <<EOF
[ v3_server ]
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid,issuer
subjectAltName=DNS:${SERVER_NAME}$(if [ -n "${DOMAIN}" ]; then echo ,DNS:${SERVER_NAME}.${DOMAIN}; fi),DNS:localhost,IP:127.0.0.1
EOF

openssl genrsa -out "${CA_HOME}/private/${SV_CERT_NAME}.key" 2048

openssl req -new \
    -key      "${CA_HOME}/private/${SV_CERT_NAME}.key" \
    -out      "${CA_HOME}/certs/${SV_CERT_NAME}.csr" \
    -subj     "${SV_SUBJ}"

openssl ca \
    -config     "${CA_HOME}/openssl.${SERVER_NAME}.cnf" \
    -cert       "${CA_HOME}/certs/${CA_CERT_NAME}.crt" \
    -keyfile    "${CA_HOME}/private/${CA_CERT_NAME}.key" \
    -batch \
    -extensions v3_server \
    -out        "${CA_HOME}/certs/${SV_CERT_NAME}.crt" \
    -days       1000 \
    -passin     "file:${CA_HOME}/.capass" \
    -infiles    "${CA_HOME}/certs/${SV_CERT_NAME}.csr"

chown -R $(logname):$(id -gn $(logname)) "${CA_HOME}"
