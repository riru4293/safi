$ErrorActionPreference = 'Stop'

$JAVA_HOME = "$env:LOCALAPPDATA\Programs\Java\primary"
$CA_HOME = "$env:LOCALAPPDATA\CA"
$STORE_PASS = 'changeit'

# Import CA
Start-Process -FilePath "${JAVA_HOME}\bin\keytool.exe" -ArgumentList "-import","-alias","Project-K","-file","`"${CA_HOME}\projectk-ca.crt`"","-cacerts","-storepass","${STORE_PASS}","-noprompt" -Wait -NoNewWindow
