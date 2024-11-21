#!/bin/bash

STORE_PASS='changeit'

# Import CA
keytool -import -alias Project-K -cacerts -storepass ${STORE_PASS} -noprompt \
  -file "$(getent passwd ${SUDO_USER:-$USER} | cut -d: -f6)/CA/projectk-ca.crt"
