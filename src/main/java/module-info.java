module assistente {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
//    requires java.logging;
//    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;
    requires velocity.engine.core;
    requires commons.dbutils;
    requires org.apache.commons.collections4;
    requires io.vavr;
    requires org.yaml.snakeyaml;
    requires java.sql;

    opens br.com.assistente.controllers to javafx.fxml;
    exports br.com.assistente;
    exports br.com.assistente.models;
}