#Requires -Version 7
${ErrorActionPreference} = 'Stop'

# MariaDB
${PREFIX} = "${env:LOCALAPPDATA}\Programs\MariaDB"
${MARIA_HOME} = "${PREFIX}\primary"


# Environment variables
${env:PATH} = "${MARIA_HOME}\bin" + ';' + "${OPENSSL_PATH}" + ';' + "${env:PATH}"
# --------------------------------------------------


# Create SAFI database
@"
DROP   USER     IF EXISTS 'safi'@'localhost';
DROP   DATABASE IF EXISTS `safi`;
CREATE DATABASE           `safi` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Simple And Flexible IDM';
CREATE USER               'safi'@'localhost' IDENTIFIED BY 'Golden Hammer';
GRANT ALL ON              `safi`.*
                       TO 'safi'@'localhost';
"@ | mariadb --user=root

Read-Host 'Press enter to exit'
