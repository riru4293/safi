#!/bin/sh

sudo apt-get install -y ca-certificates
sudo cp ${HOME}/CA/projectk-ca.crt /usr/local/share/ca-certificates
sudo update-ca-certificates
