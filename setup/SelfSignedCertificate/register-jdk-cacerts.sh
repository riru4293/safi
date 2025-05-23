#!/bin/bash

ORG='Project-K'
CA_HOME="${CA_HOME:-${HOME}/.local/CA}"
CA_CERT="${CA_HOME}/certs/ca.${ORG,,}.crt"

declare -A JDK_HOMES STORE_PASSES

# JDK_HOMES[jdk8]="${HOME}/.local/Java/jdk8"
# JDK_HOMES[jdk11]="${HOME}/.local/Java/jdk11"
# JDK_HOMES[jdk17]="${HOME}/.local/Java/jdk17"
JDK_HOMES[jdk21]="${HOME}/.local/Java/jdk21"
# JDK_HOMES[jdk24]="${HOME}/.local/Java/jdk24"

# STORE_PASSES[jdk8]="changeit"
# STORE_PASSES[jdk11]="changeit"
# STORE_PASSES[jdk17]="changeit"
STORE_PASSES[jdk21]="changeit"
# STORE_PASSES[jdk24]="changeit"
# -------------------------------------

for k in "${!JDK_HOMES[@]}"; do
  h="${JDK_HOMES[$k]}"
  p="${STORE_PASSES[$k]}"

  if [ $(which "${h}/bin/java" ) ]; then
    v=$( "${h}/bin/java" -version 2>&1 | awk -F '"' '/version/ {print $2}' )

    if [[ $v == 1.* ]]; then
      m=$( echo "$v" | cut -d '.' -f 2 )
    else
      m=$( echo "$v" | cut -d '.' -f 1 )
    fi

    if [[ ${m} -le 8 ]]; then
      "${h}/bin/keytool" -delete -alias "${ORG}" -storepass "${p}" \
      -keystore "${h}/jre/lib/security/cacerts"

      "${h}/bin/keytool" -import -alias "${ORG}" -storepass "${p}" \
      -keystore "${h}/jre/lib/security/cacerts" \
      -noprompt -trustcacerts -file "${CA_CERT}"
    else
      "${h}/bin/keytool" -delete -cacerts -alias "${ORG}" -storepass "${p}"

      "${h}/bin/keytool" -import -cacerts -alias "${ORG}" -storepass "${p}" \
      -noprompt -file "${CA_CERT}"
    fi
  fi
done
