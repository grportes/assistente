module assistente {
    requires javafx.controls;
    requires javafx.fxml;
    requires log4j;
    requires org.apache.commons.lang3;
    requires velocity.engine.core;
    requires org.apache.commons.collections4;
    requires vavr;
    requires org.yaml.snakeyaml;
    requires commons.dbutils;
    requires java.sql;

    opens br.com.assistente.controllers to javafx.fxml;
    exports br.com.assistente;
    exports br.com.assistente.models;
}