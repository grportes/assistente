package br.com.assistente.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Objects;

import static br.com.assistente.infra.util.UtilString.convCamelCase;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class DefinicaoDto {

    private final ObjectProperty<DefinicaoDto.Tipo> tipo;
    private final StringProperty nomeAtributo;
    private final BooleanProperty atributoId;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTOR
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public DefinicaoDto( final Builder builder ) {

        this.tipo = new SimpleObjectProperty<>( builder.tipo );
        this.nomeAtributo = new SimpleStringProperty( builder.nomeAtributo );
        this.atributoId = new SimpleBooleanProperty( builder.atributoId );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GETTERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Tipo getTipo() {

        return tipo.get();
    }

    public ObjectProperty<DefinicaoDto.Tipo> tipoProperty() {

        return tipo;
    }

    public String getNomeAtributo() {

        return nomeAtributo.get();
    }

    public StringProperty nomeAtributoProperty() {

        return nomeAtributo;
    }

    public Boolean isAtributoId() {

        return atributoId.get();
    }

    public BooleanProperty atributoIdProperty() {

        return atributoId;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EQUALS && HASHCODE
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals( Object o ) {

        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        DefinicaoDto that = (DefinicaoDto) o;
        return Objects.equals( getNomeAtributo(), that.getNomeAtributo() );
    }

    @Override
    public int hashCode() {

        return Objects.hash( getNomeAtributo() );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTANTES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public enum Tipo {

        SHORT("Short") {
            @Override
            public String aplicarCast( final Object valor ) {

                return format("(Short) %s", valor);
            }
        },

        INTEGER("Integer") {
            @Override
            public String aplicarCast( final Object valor ) {

                return isNull(valor) ? "null" : valor.toString();
            }
        },

        LONG("Long") {
            @Override
            public String aplicarCast( final Object valor ) {

                return format("%sL", valor);
            }
        },

        BIGDECIMAL( "BigDecimal" ) {

            @Override
            public String aplicarCast( Object valor ) {

                return null;
            }
        },

        LOCAL_DATE( "LocalDate" ) {
            @Override
            public String aplicarCast( Object valor ) {
                return null;
            }
        },

        LOCAL_DATETIME( "LocalDateTime") {
            @Override
            public String aplicarCast( Object valor ) {
                return null;
            }
        },

        LOCAL_TIME( "LocalTime") {
            @Override
            public String aplicarCast( Object valor ) {
                return null;
            }
        },

        CHAR( "Char" ) {
            @Override
            public String aplicarCast( Object valor ) {
                return null;
            }
        },

        STRING("String") {
            @Override
            public String aplicarCast( final Object valor ) {

                return format("\"%s\"", valor);
            }
        };

        private String nome;

        Tipo( final String nome ) {

            this.nome = nome;
        }

        public String getNome() {

            return nome;
        }

        @Override
        public String toString() {

            return getNome();
        }

        public abstract String aplicarCast( final Object valor );

        public static List<Tipo> buscarTipos() {

            return asList( values() );
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BUILDER
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Builder {

        private Tipo tipo;
        private String nomeAtributo;
        private Boolean atributoId;

        public Builder() {

            this.tipo = null;
            this.nomeAtributo = null;
        }

        public Builder comTipo( final Tipo value ) {

            requireNonNull( value, "Obrigatório informar o tipo do atributo" );
            this.tipo = value;
            return this;
        }

        public Builder comNomeAtributo( final String value ) {

            if ( isBlank( value ) ) throw new IllegalArgumentException( "Obrigatório informar o nome do atributo" );
            this.nomeAtributo = convCamelCase( value, false );
            return this;
        }

        public Builder comAtributoId( final Boolean value ) {

            this.atributoId = value;
            return this;
        }

        public DefinicaoDto build() {

            return new DefinicaoDto(this );
        }
    }

}
