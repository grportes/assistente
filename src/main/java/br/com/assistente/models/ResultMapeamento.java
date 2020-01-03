package br.com.assistente.models;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class ResultMapeamento {

    private final String nomeEntidade;
    private final String conteudoEntidade;
    private final String nomeEntidadeId;
    private final String conteudoEntidadeId;
    private final Map<String,String> arquivos;

    public ResultMapeamento( final Builder builder ) {

        this.nomeEntidade = builder.nomeEntidade;
        this.conteudoEntidade = builder.conteudoEntidade;
        this.arquivos = new HashMap<>( 2 );
        this.arquivos.put( this.nomeEntidade, this.conteudoEntidade );

        if ( isNotBlank( builder.conteudoEntidadeId ) ) {
            this.nomeEntidadeId = builder.nomeEntidade + "Id";
            this.conteudoEntidadeId = builder.conteudoEntidadeId;
            this.arquivos.put( this.nomeEntidadeId, this.conteudoEntidadeId );
        } else {
            this.nomeEntidadeId = null;
            this.conteudoEntidadeId = null;
        }

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

        return arquivos.keySet();
    }

    public static class Builder {

        private String nomeEntidade;
        private String conteudoEntidade;
        private String conteudoEntidadeId;

        public Builder() {

            this.nomeEntidade = null;
            this.conteudoEntidade = null;
            this.conteudoEntidadeId = null;
        }

        public Builder comNomeEntidade( final String value ) {

            this.nomeEntidade = value;
            return this;
        }

        public Builder comConteudoEntidade( final String value ) {

            this.conteudoEntidade = value;
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
