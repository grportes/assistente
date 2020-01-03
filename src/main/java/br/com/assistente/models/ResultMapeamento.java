package br.com.assistente.models;

public final class ResultMapeamento {

    private final String nomeEntidade;
    private final String conteudoEntidade;

    public ResultMapeamento( final Builder builder ) {

        this.nomeEntidade = builder.nomeEntidade;
        this.conteudoEntidade = builder.conteudoEntidade;
    }

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

    public static class Builder {

        private String nomeEntidade;
        private String conteudoEntidade;

        public Builder() {

            this.nomeEntidade = null;
            this.conteudoEntidade = null;
        }

        public Builder comNomeEntidade( final String value ) {

            this.nomeEntidade = value;
            return this;
        }

        public Builder comConteudoEntidade( final String value ) {

            this.conteudoEntidade = value;
            return this;
        }

        public ResultMapeamento build() {

            return new ResultMapeamento( this );
        }

    }

}
