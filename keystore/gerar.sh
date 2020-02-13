#!/bin/bash

arquivo=$(pwd)/assistente.jks

echo $arquivo

keytool -genkey -alias assistente -keyalg RSA -keystore $arquivo