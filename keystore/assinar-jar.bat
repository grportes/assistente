@echo off

echo "-------------------------------------------------------------------------------"
echo "------------------- executando assinatura das Libs ----------------------------"
echo "-------------------------------------------------------------------------------"

for /r %%a in (target\lib\*jar) do (
    echo "%%a"
    jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ %%a assistente
)