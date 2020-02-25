#!/bin/bash

echo "-------------------------------------------------------------------------------"
echo "------------------- executando assinatura das Libs ----------------------------"
echo "-------------------------------------------------------------------------------"

jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/commons-collections4-4.4.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/commons-dbutils-1.7.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/commons-lang3-3.9.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/commons-text-1.8.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/javafx-base-13-linux.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/javafx-base-13.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/javafx-controls-13-linux.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/javafx-controls-13.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/javafx-fxml-13-linux.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/javafx-fxml-13.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/javafx-graphics-13-linux.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/javafx-graphics-13.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/org.everit.osgi.bundles.net.sourceforge.jtds-1.3.1.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/slf4j-api-1.7.28.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/slf4j-simple-1.7.28.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/snakeyaml-1.25.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/sqlite-jdbc-3.28.0.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/vavr-0.10.2.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/vavr-match-0.10.2.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/velocity-engine-core-2.1.jar assistente
jarsigner -keystore "keystore/assistente.jks" -storepass Arcom10$ -keypass Arcom10$ target/lib/log4j-1.2.17.jar assistente
