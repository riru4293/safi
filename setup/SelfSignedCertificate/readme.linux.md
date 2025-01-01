# Summary

Creates a CA and server certificate for `localhost`.

## Install the OpenSSL

```sh
apt instal openssl
```

Confirm installation.
```sh
openssl version
```

If successful, the version will be displayed.
```
OpenSSL 3.0.2 15 Mar 2022 (Library: OpenSSL 3.0.2 15 Mar 2022)
```

## Check the location of the OpenSSL configuration file.

```sh
openssl version -d
```

The location of openssl.cnf will be displayed.
```
OPENSSLDIR: "/usr/lib/ssl"
```

## Modifying the OpenSSL configuration file

Edit a copy of the original.
```sh
cp /usr/lib/ssl/openssl.cnf ~/.local/
vi ~/.local/openssl.cnf
```

Add settings for CA.

```
[ v3_ca ]
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid:always,issuer
basicConstraints = CA:true
### Append >>>
keyUsage = cRLSign, keyCertSign
### <<< Append
```

Change the directory.
```sh
[ CA_default ]
dir = $ENV::CA_HOME
```

## Create a working directory

```sh
export CA_HOME="${HOME}/.local/CA"
mkdir "${CA_HOME}"
mkdir "${CA_HOME}/private"
mkdir "${CA_HOME}/crl"
mkdir "${CA_HOME}/certs"
mkdir "${CA_HOME}/newcerts"
echo 01 > "${CA_HOME}/serial"
touch "${CA_HOME}/index.txt"
```

## Create a CA password

```sh
echo 'CA password' > "${CA_HOME}/.capass"
```

## Create a CA certificate

```sh
# 秘密鍵を生成して自己署証明書を発行する
# -new        新しい明書要求を生成する
# -config     設定ファイルの場所
# -keyout     指定のスで秘密鍵を出力する
# -out        指定のスに出力する（-x509オプョンを指定した場合は自己名証明書）
# -passout    出力す秘密鍵に設定するパスフレズを指定する
# -subj       識別名"/type0=value0type1=value1/type2=.."の形式で指定する
# -x509       署名要を出力する代わりに自己署証明書を出力する
# -days       証明書有効日数を指定する
# -extensions 設定フイルの拡張セクション名を定する
openssl req \
    -new \
    -config   "${HOME}/.local/openssl.cnf" \
    -keyout   "${CA_HOME}/private/ca.project-k.key" \
    -out      "${CA_HOME}/certs/ca.project-k.crt" \
    -passout  "file:${CA_HOME}/.capass" \
    -subj     "/C=JP/ST=Osaka/O=Project-K/CN=ca.project-k.mydns.jp" \
    -x509 -days 3650 -extensions v3_ca
```

Confirm the created CA certificate
```sh
openssl x509 -in "${CA_HOME}/certs/ca.project-k.crt" -text
```

## Create a server certificate

```sh
SERVER_NAME="$(hostname)"

cp ${HOME}/.local/openssl.cnf ${CA_HOME}/openssl.${SERVER_NAME}.cnf
tee -a ${CA_HOME}/openssl.${SERVER_NAME}.cnf <<EOF
[ v3_server ]
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid,issuer
subjectAltName=DNS:${SERVER_NAME},DNS:${SERVER_NAME}.project-k.mydns.jp,DNS:localhost,IP:127.0.0.1
EOF

# genrsaコマンドでパスフレーズなしの秘密鍵を生成する。
#   -out  出力する秘密鍵ファイル
#   2048  秘密鍵のサイズ（ビット数）
openssl genrsa -out "${CA_HOME}/private/${SERVER_NAME}.project-k.key" 2048

# reqコマンドでCSR（証明書署名要求）を出力する。
#   -key  上で生成した秘密鍵ファイル
#   -out  出力するCSRファイル
#   -subj ターゲットの識別名
openssl req -new \
    -key      "${CA_HOME}/private/${SERVER_NAME}.project-k.key" \
    -out      "${CA_HOME}/certs/${SERVER_NAME}.project-k.csr" \
    -subj     "/C=JP/ST=Osaka/O=Project-K/CN=${SERVER_NAME}.project-k.mydns.jp"

# caコマンドで署名して証明書を出力する。
#   -config     設定ファイルの場所
#   -cert       CAの証明書
#   -keyfile    CAのキーファイル
#   -batch      対話入力なしで自動処理を行う
#   -extensions 設定ファイル内の拡張セクション名
#   -out        出力する証明書ファイル
#   -days       証明書を認証する日数
#   -passin     CAの秘密鍵のパスフレーズ入力
#   -infiles    上で生成したCSRファイル
openssl ca \
    -config     "${CA_HOME}/openssl.${SERVER_NAME}.cnf" \
    -cert       "${CA_HOME}/certs/ca.project-k.crt" \
    -keyfile    "${CA_HOME}/private/ca.project-k.key" \
    -batch \
    -extensions v3_server \
    -out        "${CA_HOME}/certs/${SERVER_NAME}.project-k.crt" \
    -days       1000 \
    -passin     "file:${CA_HOME}/.capass" \
    -infiles    "${CA_HOME}/certs/${SERVER_NAME}.project-k.csr"
```

## Install CA certificate

```sh
sudo apt-get install -y ca-certificates
sudo cp ${HOME}/.local/CA/certs/ca.project-k.crt /usr/local/share/ca-certificates
sudo update-ca-certificates
```

## Import CA to JDK

```sh
STORE_PASS='changeit'
keytool -import -alias Project-K -cacerts -storepass ${STORE_PASS} -noprompt \
  -file "$(getent passwd ${SUDO_USER:-$USER} | cut -d: -f6)/.local/CA/certs/ca.project-k.crt"
```
