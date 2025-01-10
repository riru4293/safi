#!/bin/sh

ORG='project-k'
CA_HOME="${HOME}/.local/CA"
CA_CERT_NAME="ca.${ORG}"
JDK_PREFIX="${HOME}/.local/Java"

# Register CA to JDK
for jhome in $( \find "${JDK_PREFIX}" -maxdepth 1 -name "jdk-*" -type d ); do
  echo $(basename "${jhome}")
  ${jhome}/bin/keytool -delete -cacerts -alias "${ORG}" -storepass 'changeit'
  ${jhome}/bin/keytool -import -cacerts -alias "${ORG}" -storepass 'changeit' -noprompt \
    -file "${CA_HOME}/certs/${CA_CERT_NAME}.crt"
  ${jhome}/bin/keytool -list -cacerts -alias "${ORG}" -v -storepass 'changeit'
done
