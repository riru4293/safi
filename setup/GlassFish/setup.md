## Setup GlassFish

### Pool SafiPool
Pool Name: SafiPool
Resource Type: javax.sql.XADataSource
Database Driver Vendor: MariaDB
User: safi
Url: jdbc:mariadb://localhost/safi?sessionVariables=innodb_lock_wait_timeout=60
Password: Golden Hammer


### JDBC Resource jdbc/safi
JNDI Name: jdbc/safi

### JVM Options

#### Linux
-Dsafi.home=/, opt, safi

#### Windows
-Dsafi.home=C:, Users, Public, safi
