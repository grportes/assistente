package br.com.assistente.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class DefinicaoDto {

    private final StringProperty tipo;
    private final StringProperty nomeAtributo;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTOR
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public DefinicaoDto( final Builder builder ) {

        this.tipo = new SimpleStringProperty( builder.tipo.getNome() );
        this.nomeAtributo = new SimpleStringProperty( builder.nomeAtributo );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GETTERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getTipo() {

        return tipo.get();
    }

    public StringProperty tipoProperty() {

        return tipo;
    }

    public String getNomeAtributo() {

        return nomeAtributo.get();
    }

    public StringProperty nomeAtributoProperty() {

        return nomeAtributo;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EQUALS && HASHCODE
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals( Object o ) {

        if ( this == o ) return true;
        if ( !(o instanceof DefinicaoDto) ) return false;
        DefinicaoDto that = (DefinicaoDto) o;
        return Objects.equals(getTipo(), that.getTipo()) &&
            Objects.equals(getNomeAtributo(), that.getNomeAtributo());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getTipo(), getNomeAtributo());
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

            this.nomeAtributo = value;
            return this;
        }

        public DefinicaoDto build() {

            return new DefinicaoDto(this );
        }
    }
}
