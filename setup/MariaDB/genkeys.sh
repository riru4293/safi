#!/bin/sh

DEST="/tmp/MariaDB-Keys.$(date +%F)"

if [ ! -d "${DEST}" ]; then mkdir "${DEST}"; fi
for i in 1 2 3 4 5 6 7 8 9; do echo $i';'$(openssl rand -hex 32); done | tee "${DEST}/tdekeys.txt"
openssl rand -hex 128 > "${DEST}/tdekeys.key"
openssl enc -aes-256-cbc -md sha1 \
  -pass file:$(readlink -f "${DEST}/tdekeys.key") \
  -in "${DEST}/tdekeys.txt" -out "${DEST}/tdekeys.enc"
rm "${DEST}/tdekeys.txt"

