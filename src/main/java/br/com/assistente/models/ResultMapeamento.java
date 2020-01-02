package br.com.assistente.models;

public final class ResultMapeamento {

    private final String nomeEntidade;
    private final String conteudoEntidade;
    private final String nomeEntidadeId;
    private final String conteudoEntidadeId;

    public ResultMapeamento( final Builder builder ) {

        this.nomeEntidade = builder.nomeEntidade;
        this.conteudoEntidade = builder.conteudoEntidade;
        this.nomeEntidadeId = builder.nomeEntidadeId;
        this.conteudoEntidadeId = builder.conteudoEntidadeId;
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

    public static class Builder {

        private String nomeEntidade;
        private String conteudoEntidade;
        private String nomeEntidadeId;
        private String conteudoEntidadeId;

        public Builder comNomeEntidade( final String value ) {

            this.nomeEntidade = value;
            return this;
        }

        public Builder comConteudoEntidade( final String value ) {

            this.conteudoEntidade = value;
            return this;
        }

        public Builder comNomeEntidadeId( final String value ) {

            this.nomeEntidadeId = value;
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
