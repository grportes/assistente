package br.com.assistente.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static br.com.assistente.infra.util.UtilString.capitalize;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public class ModeloCampo {

    private BooleanProperty pk;
    private StringProperty colunaDB;
    private StringProperty colunaJava;
    private StringProperty tipoDB;
    private StringProperty tipoJava;
    private BooleanProperty colNull;
    private BooleanProperty converter;

    public ModeloCampo() {
    }

    private ModeloCampo( final Builder builder ) {

        this.pk = new SimpleBooleanProperty(builder.pk);
        this.colunaDB = new SimpleStringProperty(builder.colunaDB);
        this.colunaJava = new SimpleStringProperty(builder.colunaJava);
        this.tipoDB = new SimpleStringProperty(builder.tipoDB);
        this.tipoJava = new SimpleStringProperty(builder.tipoJava);
        this.colNull = new SimpleBooleanProperty(builder.colNull);
        this.converter = new SimpleBooleanProperty(builder.converter);
    }

    public boolean isPk() {

        return pk.get();
    }

    public BooleanProperty pkProperty() {

        return pk;
    }

    public String getColunaDB() {

        return colunaDB.get();
    }

    public StringProperty colunaDBProperty() {

        return colunaDB;
    }

    public String getColunaJava() {

        return colunaJava.get();
    }

    public StringProperty colunaJavaProperty() {

        return colunaJava;
    }

    public String getTipoDB() {

        return tipoDB.get();
    }

    public StringProperty tipoDBProperty() {

        return tipoDB;
    }

    public String getTipoJava() {

        return tipoJava.get();
    }

    public StringProperty tipoJavaProperty() {

        return tipoJava;
    }

    public boolean isColNull() {

        return colNull.get();
    }

    public BooleanProperty colNullProperty() {

        return colNull;
    }

    public boolean isConverter() {

        return converter.get();
    }

    public BooleanProperty converterProperty() {

        return converter;
    }

    public static class Builder {

        private boolean pk;
        private String colunaDB;
        private String colunaJava;
        private String tipoDB;
        private String tipoJava;
        private boolean colNull;
        private boolean converter;

        public Builder() {

            this.pk = false;
            this.colunaDB = null;
            this.colunaJava = null;
            this.tipoDB = null;
            this.tipoJava = null;
            this.colNull = false;
            this.converter = false;
        }

        public Builder comPK( final boolean value ) {

            this.pk = value;
            return this;
        }

        public Builder comColunaDB( final String value ) {

            this.colunaDB = lowerCase( value );
            this.colunaJava = capitalize( this.colunaDB );
            return this;
        }

        public Builder comTipoDB( final String value ) {

            this.tipoDB = value;
            return this;
        }

        public Builder comTipoJava( final String value ) {

            this.tipoJava = value;
            return this;
        }

        public Builder comColNull( final boolean value ) {

            this.colNull = value;
            return this;
        }

        public Builder comConverter( final boolean value ) {

            this.converter = value;
            return this;
        }

        public ModeloCampo build() {

            return new ModeloCampo( this );
        }
    }


}
