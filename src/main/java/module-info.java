module assistente {
    requires java.sql;
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;
    requires org.apache.commons.collections4;
    requires org.yaml.snakeyaml;
    requires io.vavr;
    requires velocity.engine.core;
    requires commons.dbutils;

    opens br.com.assistente.controllers to javafx.fxml;
    exports br.com.assistente;
    exports br.com.assistente.models to org.yaml.snakeyaml;
}