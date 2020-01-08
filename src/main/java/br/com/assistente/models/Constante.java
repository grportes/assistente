package br.com.assistente.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

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
    // BUILDER
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Builder {

        private String nome;
        private String valor;
        private String descricao;

        public Builder comNome( final String value ) {

            this.nome = value;
            return this;
        }

        public Builder comValor( final String value ) {

            this.valor = value;
            return this;
        }

        public Builder comDescricao( final String value ) {

            this.descricao = value;
            return this;
        }

        public Constante build() {

            return new Constante( this );
        }

    }
}