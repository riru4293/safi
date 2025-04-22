#!/bin/sh

# Execute as root
if [ $(id -u) -ne 0 ]; then
  sudo $0
  exit $?
fi

ORG_NAME='project-k'
SERVER_NAME="$(hostname)"
CERTFILE_NAME="${SERVER_NAME}$(if [ -n "$ORG_NAME" ]; then echo .; fi)${ORG_NAME}"

apt update && apt install nginx

rm /etc/nginx/sites-enabled/default
mkdir /etc/nginx/conf.d/tls-server
mkdir /etc/nginx/conf.d/upstream

tee /etc/nginx/conf.d/base.conf <<'EOF'
include /etc/nginx/conf.d/upstream/*.conf;

server {
    listen 80 default_server;
    listen [::]:80 default_server;
    return 301 https://$host$request_uri;
}

server {
    client_max_body_size 100M;
    proxy_read_timeout 1800;
    listen 443 ssl default_server;
    listen [::]:443 ssl default_server;
    root /var/www/html;
    index index.html;
    server_name _;
    location / {
        # First attempt to serve request as file, then
        # as directory, then fall back to displaying a 404.
        try_files $uri $uri/ =404;
    }
    include /etc/nginx/conf.d/tls-server/*.conf;
}
EOF

tee /etc/nginx/conf.d/tls-server/cert.conf <<EOF
ssl_certificate     /etc/ssl/certs/${CERTFILE_NAME}.crt;
ssl_certificate_key /etc/ssl/private/${CERTFILE_NAME}.key;
EOF

tee /etc/nginx/conf.d/tls-server/safi-tls-server.conf <<'EOF'
location /safi/ {
    proxy_pass http://safi-host/safi/;
    proxy_redirect default;

    proxy_set_header Forwarded "for=`$proxy_add_x_forwarded_for; proto=`$scheme; by=`$server_addr";

    proxy_set_header X-Forwarded-For `$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto `$scheme;
    proxy_set_header X-Forwarded-Host `$host;
}
EOF

tee /etc/nginx/conf.d/upstream/safi-upstream.conf <<'EOF'
upstream safi-host {
    server 127.0.0.1:8080;
}
EOF

systemctl enable nginx.service
systemctl restart nginx.service
