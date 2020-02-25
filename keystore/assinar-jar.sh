#!/bin/bash

echo "-------------------------------------------------------------------------------"
echo "------------------- executando assinatura das Libs ----------------------------"
echo "-------------------------------------------------------------------------------"

DIR='target/lib/*jar'
for JAR_FILE in ls $DIR
do
    echo $JAR_FILE
    jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ $JAR_FILE assistente
done