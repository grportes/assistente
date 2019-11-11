package br.com.assistente.models;

import java.util.Objects;
import java.util.Set;

public class Modelo {

    private String nomeEntidade;
    private String nomeTabela;
    private String nomeCompletoTabela;
    private Set<Variavel> variaveis;

    public Modelo( final Builder builder ) {

        this.nomeEntidade = builder.nomeEntidade;
        this.nomeTabela = builder.nomeTabela;
        this.nomeCompletoTabela = builder.nomeCompletoTabela;
        this.variaveis = builder.variaveis;
    }

    public String getNomeEntidade() {

        return nomeEntidade;
    }

    public String getNomeTabela() {

        return nomeTabela;
    }

    public String getNomeCompletoTabela() {

        return nomeCompletoTabela;
    }

    public Set<Variavel> getVariaveis() {

        return variaveis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Modelo)) return false;
        Modelo modelo = (Modelo) o;
        return Objects.equals(getNomeEntidade(), modelo.getNomeEntidade());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNomeEntidade());
    }

    public static class Builder {

        private String nomeEntidade;
        private String nomeTabela;
        private String nomeCompletoTabela;
        private Set<Variavel> variaveis;

        public Builder comNomeEntidade( final String nomeEntidade ) {

            this.nomeEntidade = nomeEntidade;
            return this;
        }

        public Builder comNomeTabela( final String nomeTabela ) {

            this.nomeTabela = nomeTabela;
            return this;
        }

        public Builder comNomeCompletoTabela( final String nomeCompletoTabela ) {

            this.nomeCompletoTabela = nomeCompletoTabela;
            return this;
        }

        public Builder comVariaveis( final Set<Variavel> variaveis ) {

            this.variaveis = variaveis;
            return this;
        }

        public Modelo build() {

            return new Modelo( this );
        }
    }



}
