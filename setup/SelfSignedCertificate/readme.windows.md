# Summary

Creates a CA and server certificate for `localhost`.

## Install the OpenSSL

```batch
install.bat
```

Confirm installation.
```batch
SET "PATH=%LOCALAPPDATA%\Programs\OpenSSL\primary\x64\bin;%PATH%"
openssl version
```

If successful, the version will be displayed.
```
OpenSSL 3.4.0 22 Oct 2024 (Library: OpenSSL 3.4.0 22 Oct 2024)
```

## Modifying the OpenSSL configuration file

```batch
"%LOCALAPPDATA%\Programs\OpenSSL\primary\ssl\openssl.cnf"
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
```
[ CA_default ]
dir = $ENV::CA_HOME
```

## Create a working directory

```batch
SET "CA_HOME=%LOCALAPPDATA%\CA"
MD "%CA_HOME%"
MD "%CA_HOME%\private"
MD "%CA_HOME%\crl"
MD "%CA_HOME%\certs"
MD "%CA_HOME%\newcerts"
echo 01 > "%CA_HOME%\serial"
type nul > "%CA_HOME%\index.txt"
```

## Create a CA password

```batch
echo CA password > "%CA_HOME%\.capass"
```

## Create a CA certificate

```batch
; 秘密鍵を生成して自己署証明書を発行する
; -new        新しい明書要求を生成する
; -config     設定ファイルの場所
; -keyout     指定のスで秘密鍵を出力する
; -out        指定のスに出力する（-x509オプョンを指定した場合は自己名証明書）
; -passout    出力す秘密鍵に設定するパスフレズを指定する
; -subj       識別名"/type0=value0type1=value1/type2=.."の形式で指定する
; -x509       署名要を出力する代わりに自己署証明書を出力する
; -days       証明書有効日数を指定する
; -extensions 設定フイルの拡張セクション名を定する
openssl req ^
    -new ^
    -config   "%LOCALAPPDATA%\Programs\OpenSSL\primary\ssl\openssl.cnf" ^
    -keyout   "%CA_HOME%\private\ca.project-k.key" ^
    -out      "%CA_HOME%\certs\ca.project-k.crt" ^
    -passout  "file:%CA_HOME%\.capass" ^
    -subj     "/C=JP/ST=Osaka/O=Project-K/CN=ca.project-k.mydns.jp" ^
    -x509 -days 3650 -extensions v3_ca
```

Confirm the created CA certificate
```batch
openssl x509 -in "%CA_HOME%\certs\ca.project-k.crt" -text
```

## Create a server certificate

```batch
SET "SERVER_NAME=%COMPUTERNAME%"

COPY "%LOCALAPPDATA%\Programs\OpenSSL\primary\ssl\openssl.cnf" "%CA_HOME%/openssl.%SERVER_NAME%.cnf"

"%CA_HOME%/openssl.%SERVER_NAME%.cnf"
```

```
### Append >>>
[ v3_server ]
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid,issuer
subjectAltName=DNS:%SERVER_NAME%,DNS:%SERVER_NAME%.project-k.mydns.jp,DNS:localhost,IP:127.0.0.1
### <<< Append
```

```batch
; genrsaコマンドでパスフレーズなしの秘密鍵を生成する。
;   -out  出力する秘密鍵ファイル
;   2048  秘密鍵のサイズ（ビット数）
openssl genrsa -out "%CA_HOME%\private\%SERVER_NAME%.project-k.key" 2048

# reqコマンドでCSR（証明書署名要求）を出力する。
#   -config     設定ファイルの場所
#   -key  上で生成した秘密鍵ファイル
#   -out  出力するCSRファイル
#   -subj ターゲットの識別名
openssl req -new ^
    -config   "%CA_HOME%/openssl.%SERVER_NAME%.cnf" ^
    -key      "%CA_HOME%\private\%SERVER_NAME%.project-k.key" ^
    -out      "%CA_HOME%\certs\%SERVER_NAME%.project-k.csr" ^
    -subj     "/C=JP/ST=Osaka/O=Project-K/CN=%SERVER_NAME%.project-k.mydns.jp"

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
openssl ca ^
    -config     "%CA_HOME%\openssl.%SERVER_NAME%.cnf" ^
    -cert       "%CA_HOME%\certs\ca.project-k.crt" ^
    -keyfile    "%CA_HOME%\private\ca.project-k.key" ^
    -batch ^
    -extensions v3_server ^
    -out        "%CA_HOME%\certs\%SERVER_NAME%.project-k.crt" ^
    -days       1000 ^
    -passin     "file:%CA_HOME%\.capass" ^
    -infiles    "%CA_HOME%\certs\%SERVER_NAME%.project-k.csr"
```

## Import CA to JDK

```powershell
$JAVA_HOME = "$env:LOCALAPPDATA\Programs\Java\primary"
$CA_HOME = "$env:LOCALAPPDATA\CA"
$STORE_PASS = 'changeit'

# Import CA
Start-Process -FilePath "${JAVA_HOME}\bin\keytool.exe" -ArgumentList "-import","-alias","Project-K","-file","`"${CA_HOME}\certs\ca.project-k.crt`"","-cacerts","-storepass","${STORE_PASS}","-noprompt" -Wait -NoNewWindow
```

