#!/bin/bash

arquivo=$(pwd)/keystore/assistente.jks

echo $arquivo

keytool -genkey -alias assistente -keyalg RSA -keystore $arquivo