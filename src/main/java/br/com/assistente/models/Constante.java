package br.com.assistente.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;

import static br.com.assistente.infra.util.UtilString.normalizeJava;
import static br.com.assistente.infra.util.UtilString.removerAcentosECaracteresEspeciais;
import static br.com.assistente.infra.util.UtilString.replaceAll;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.upperCase;

public final class Constante {

    private final StringProperty nome;
    private final StringProperty valor;
    private final StringProperty descricao;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTOR
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Constante( final Builder builder ) {

        this.nome = new SimpleStringProperty( builder.nome );
        this.valor = new SimpleStringProperty( builder.valor );
        this.descricao = new SimpleStringProperty( builder.descricao );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GETTERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getNome() {

        return nome.get();
    }

    public StringProperty nomeProperty() {

        return nome;
    }

    public String getValor() {

        return valor.get();
    }

    public StringProperty valorProperty() {

        return valor;
    }

    public String getDescricao() {

        return descricao.get();
    }

    public StringProperty descricaoProperty() {

        return descricao;
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
        Constante constante = (Constante) o;
        return Objects.equals( getValor(), constante.getValor() );
    }

    @Override
    public int hashCode() {

        return Objects.hash( getValor() );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXLIARES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String convPadraoNomeEnum( final String nomeClasse ) {

        if ( isBlank( nomeClasse ) ) throw new IllegalArgumentException( "Favor informar o nome do Enum" );
        return normalizeJava( nomeClasse, true );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTANTES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public enum Tipo {

        SHORT( "Short" ) {
            @Override
            public boolean checkTipo( final String valor ) {

                return permiteConverter(() -> Short.parseShort( valor ) );
            }

            @Override
            public String aplicarCast( final Object valor ) {

                return format( "(short) %s", valor );
            }
        },

        INTEGER( "Integer" ) {
            @Override
            public boolean checkTipo( final String valor ) {

                return permiteConverter(() -> Integer.parseInt( valor ) );
            }

            @Override
            public String aplicarCast( final Object valor ) {

                return isNull( valor ) ? "null" : valor.toString();
            }
        },

        LONG( "Long" ) {
            @Override
            public boolean checkTipo( final String valor ) {

                return permiteConverter(() -> Long.parseLong( valor ) );
            }

            @Override
            public String aplicarCast( final Object valor ) {

                return format( "%sL", valor );
            }
        },

        STRING( "String" ) {
            @Override
            public boolean checkTipo( final String valor ) {

                return true;
            }

            @Override
            public String aplicarCast( final Object valor ) {

                return format( "\"%s\"", valor );
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

        public abstract boolean checkTipo( final String valor );

        public abstract String aplicarCast( final Object valor );

        protected boolean permiteConverter( final Supplier<Object> check ) {

            try {
                check.get();
                return true;
            } catch ( final NumberFormatException e ) {
                return false;
            }
        }

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

        private String nome;
        private String valor;
        private String descricao;

        public Builder comNome( final String value ) {

            this.nome = replaceAll(
                removerAcentosECaracteresEspeciais( normalizeSpace( upperCase( trim( value ) ) ) ),
                "_"," ", ".", "-"
            );
            return this;
        }

        public Builder comValor( final String value ) {

            this.valor = trim( value );
            return this;
        }

        public Builder comDescricao( final String value ) {

            this.descricao = upperCase( trim( value ) );
            return this;
        }

        public Constante build() {

            final StringJoiner msg = new StringJoiner( " " );
            if ( isBlank( nome ) ) msg.add( "Nome" );
            if ( isBlank( valor ) ) msg.add( "Valor" );
            if ( isBlank( descricao ) ) msg.add( "Descricao" );
            if ( msg.length() > 0 )
                throw new IllegalArgumentException( format( "É necessário informar [%s]", msg.toString() ) );

            return new Constante( this );
        }

    }
}
