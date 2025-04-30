#!/bin/sh

VER='7.0.21'
# PREFIX='/opt/GlassFish'
PREFIX="${HOME}/.local/GlassFish"
DEST="${PREFIX}/${VER}"
GLASSFISH_HOME="${DEST}"
SRC="https://mirror.kakao.com/eclipse/ee4j/glassfish/glassfish-${VER}.zip"
HASH="https://www.eclipse.org/downloads/sums.php?file=/ee4j/glassfish/glassfish-${VER}.zip&type=sha512"
ZIP="/tmp/$( basename "${SRC}" )"
ORIGIN_NAME="glassfish7"
JAVA_HOME="${HOME}/.local/Java/primary"
CA_HOME="${HOME}/.local/CA"

MARIA_CLIENT_VER='3.5.1'
MARIA_CLIENT_SRC="https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/${MARIA_CLIENT_VER}/mariadb-java-client-${MARIA_CLIENT_VER}.jar"

SAFI_PASS='Golden Hammer'
STORE_PASS='changeit'

# Uninstall service
systemctl --user --now disable glassfish


# Download if necessary
if [ ! -e "${ZIP}" ]; then
    r=$( curl -LsS "${HASH}" )
    h=$( sha512sum "${ZIP}" | awk '{split($2, a, "/"); print $1, a[length(a)]}' )

    if [ "${r}" != "${h}" ]; then
        wget -P /tmp "${SRC}"
    fi
fi


# Extract
if [ ! -d "${PREFIX}" ]; then mkdir -p "${PREFIX}"; fi
if [ -d "${PREFIX}/${ORIGIN_NAME}" ]; then rm -r "${PREFIX}/${ORIGIN_NAME}"; fi
if [ -d "${DEST}" ]; then rm -r "${DEST}"; fi
if [ -d "${PREFIX}/primary" ]; then rm "${PREFIX}/primary"; fi

unzip -d "${PREFIX}" -q "${ZIP}"
mv "${PREFIX}/${ORIGIN_NAME}" "${DEST}"
ln -s "${DEST}" "${PREFIX}/primary"


# Configure AS_JAVA(JAVA_HOME)
echo "AS_JAVA=\"${JAVA_HOME}\"" >> "${DEST}/glassfish/config/asenv.conf"


# Download MariaDB Client
wget -P "${DEST}/glassfish/domains/domain1/lib" "${MARIA_CLIENT_SRC}"


# Install service
systemctl --user --now enable glassfish


# Configure JDBC
"${GLASSFISH_HOME}/bin/asadmin" create-jdbc-connection-pool \
--datasourceclassname=org.mariadb.jdbc.MariaDbDataSource \
--restype=javax.sql.DataSource \
--property=user=safi:password="${SAFI_PASS}":sessionVariables='innodb_lock_wait_timeout\=60':sslMode='verify-full':\
trustStore="file\:///${CA_HOME}/mariadb-connector-cacerts":\
trustStoreType='pkcs12':trustStorePassword="${STORE_PASS}":URL='jdbc\:mariadb\://localhost/safi?serverTimezone\=UTC' \
SafiPool

"${GLASSFISH_HOME}/bin/asadmin" set "resources.jdbc-connection-pool.SafiPool.statement-timeout-in-seconds=300"
"${GLASSFISH_HOME}/bin/asadmin" set "resources.jdbc-connection-pool.SafiPool.statement-cache-size=20"
"${GLASSFISH_HOME}/bin/asadmin" set "resources.jdbc-connection-pool.SafiPool.validation-table-name=DUAL"
"${GLASSFISH_HOME}/bin/asadmin" set "resources.jdbc-connection-pool.SafiPool.connection-validation-method=table"
"${GLASSFISH_HOME}/bin/asadmin" set "resources.jdbc-connection-pool.SafiPool.is-connection-validation-required=true"
"${GLASSFISH_HOME}/bin/asadmin" set "resources.jdbc-connection-pool.SafiPool.fail-all-connections=true"
"${GLASSFISH_HOME}/bin/asadmin" set "resources.jdbc-connection-pool.SafiPool.statement-cache-type="

"${GLASSFISH_HOME}/bin/asadmin" create-jdbc-resource --connectionpoolid SafiPool jdbc/safi

# Configure Logging
"${GLASSFISH_HOME}/bin/asadmin" set-log-levels "jp.mydns.projectk.safi=FINEST"

# Configure JVM options
"${GLASSFISH_HOME}/bin/asadmin" create-jvm-options '-Dsafi.home=/, opt, safi'


# Restart service
systemctl --user restart glassfish
