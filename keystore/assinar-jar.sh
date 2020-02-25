#!/bin/bash

echo "-------------------------------------------------------------------------------"
echo "------------------- executando assinatura das Libs ----------------------------"
echo "-------------------------------------------------------------------------------"

DIR='target/lib/*jar'
for FILE in ls $DIR
do
    echo $FILE
    jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ $FILE assistente
done