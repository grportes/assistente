package br.com.assistente.models.domains.mapeamento;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static br.com.assistente.infra.util.UtilString.capitalize;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public class ModeloCampo {

    private StringProperty colunaDB;
    private StringProperty colunaJava;
    private StringProperty tipoDB;
    private StringProperty tipoJava;
    private BooleanProperty colNull;
    private StringProperty converter;

    public ModeloCampo() {
    }

    private ModeloCampo( final Builder builder ) {

        this.colunaDB = new SimpleStringProperty(builder.colunaDB);
        this.colunaJava = new SimpleStringProperty(builder.colunaJava);
        this.tipoDB = new SimpleStringProperty(builder.tipoDB);
        this.tipoJava = new SimpleStringProperty(builder.tipoJava);
        this.colNull = new SimpleBooleanProperty(builder.colNull);
        this.converter = new SimpleStringProperty(builder.converter);
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

    public String getConverter() {

        return converter.get();
    }

    public StringProperty converterProperty() {

        return converter;
    }

    public static class Builder {

        private String colunaDB;
        private String colunaJava;
        private String tipoDB;
        private String tipoJava;
        private boolean colNull;
        private String converter;

        public Builder() {

            this.colunaDB = null;
            this.colunaJava = null;
            this.tipoDB = null;
            this.tipoJava = null;
            this.colNull = false;
            this.converter = null;
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

        public Builder comConverter( final String value ) {

            this.converter = value;
            return this;
        }

        public ModeloCampo build() {

            return new ModeloCampo( this );
        }
    }


}
