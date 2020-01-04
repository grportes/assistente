package br.com.assistente.models;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.trim;

public final class ResultMapeamento {

    private final String nomeEntidade;
    private final String conteudoEntidade;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTOR
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMapeamento( final Builder builder ) {

        this.nomeEntidade = builder.nomeEntidade;
        this.conteudoEntidade = builder.conteudoEntidade;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GETTERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getNomeEntidade() {

        return nomeEntidade;
    }

    public String getConteudoEntidade() {

        return conteudoEntidade;
    }

    @Override
    public String toString() {

        return getNomeEntidade();
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
        ResultMapeamento that = (ResultMapeamento) o;
        return Objects.equals( nomeEntidade, that.nomeEntidade );
    }

    @Override
    public int hashCode() {

        return Objects.hash( nomeEntidade );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BUILDER
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Builder {

        private String nomeEntidade;
        private String conteudoEntidade;

        public Builder() {

            this.nomeEntidade = null;
            this.conteudoEntidade = null;
        }

        public Builder comNomeEntidade( final String value ) {

            this.nomeEntidade = trim( value );
            return this;
        }

        public Builder comConteudoEntidade( final String value ) {

            this.conteudoEntidade = trim( value );
            return this;
        }

        public ResultMapeamento build() {

            return new ResultMapeamento( this );
        }

    }

}
