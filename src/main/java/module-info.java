module assistente {
    requires java.sql;
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.lang3;
    requires org.apache.commons.collections4;
    requires org.slf4j.simple;
    requires org.yaml.snakeyaml;
    requires io.vavr;
    requires velocity.engine.core;
    requires commons.dbutils;

    opens br.com.assistente.controllers to javafx.fxml;
    opens br.com.assistente.models to org.yaml.snakeyaml, velocity.engine.core;
    exports br.com.assistente;
}