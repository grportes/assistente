package br.com.assistente.models;

import java.util.LinkedHashSet;
import java.util.Set;

public final class ResultMapeamento {

    private final String nomeEntidade;
    private final String conteudoEntidade;
    private final String nomeEntidadeId;
    private final String conteudoEntidadeId;
    private final Set<String> arquivos;

    public ResultMapeamento( final Builder builder ) {

        this.nomeEntidade = builder.nomeEntidade;
        this.conteudoEntidade = builder.conteudoEntidade;
        this.nomeEntidadeId = builder.nomeEntidadeId;
        this.conteudoEntidadeId = builder.conteudoEntidadeId;
        this.arquivos = builder.arquivos;
    }

    public String getNomeEntidade() {

        return nomeEntidade;
    }

    public String getConteudoEntidade() {

        return conteudoEntidade;
    }

    public String getNomeEntidadeId() {

        return nomeEntidadeId;
    }

    public String getConteudoEntidadeId() {

        return conteudoEntidadeId;
    }

    public Set<String> getArquivos() {

        return arquivos;
    }

    public static class Builder {

        private String nomeEntidade;
        private String conteudoEntidade;
        private String nomeEntidadeId;
        private String conteudoEntidadeId;
        private Set<String> arquivos;

        public Builder() {

            this.nomeEntidade = null;
            this.conteudoEntidade = null;
            this.nomeEntidadeId = null;
            this.conteudoEntidadeId = null;
            this.arquivos = new LinkedHashSet<>();
        }

        public Builder comNomeEntidade( final String value ) {

            this.nomeEntidade = value;
            this.arquivos.add( value );
            return this;
        }

        public Builder comConteudoEntidade( final String value ) {

            this.conteudoEntidade = value;
            return this;
        }

        public Builder comNomeEntidadeId( final String value ) {

            this.nomeEntidadeId = value;
            this.arquivos.add( value );
            return this;
        }

        public Builder comConteudoEntidadeId( final String value ) {

            this.conteudoEntidadeId = value;
            return this;
        }

        public ResultMapeamento build() {

            return new ResultMapeamento( this );
        }

    }

}
