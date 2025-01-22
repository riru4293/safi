#!/bin/sh


# Execute as root
if [ $(id -u) -ne 0 ]; then
  sudo $0
  exit $?
fi


# Create SAFI database
mariadb <<'EOF'
DROP   USER     IF EXISTS 'safi'@'localhost';
DROP   DATABASE IF EXISTS `safi`;
CREATE DATABASE           `safi` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Simple And Flexible IDM';
CREATE USER               'safi'@'localhost' IDENTIFIED BY 'Golden Hammer';
GRANT ALL ON              `safi`.*
                       TO 'safi'@'localhost';
EOF
