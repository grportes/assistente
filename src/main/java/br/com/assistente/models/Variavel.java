package br.com.assistente.models;

import java.util.Objects;

public class Variavel {

    private String nome;
    private TipoJava tipo;

    public Variavel( final Builder builder ) {

        this.nome = builder.nome;
        this.tipo = builder.tipo;
    }

    public String getNome() {

        return nome;
    }

    public TipoJava getTipo() {

        return tipo;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variavel)) return false;
        Variavel variavel = (Variavel) o;
        return Objects.equals(getNome(), variavel.getNome()) &&
            Objects.equals(getTipo(), variavel.getTipo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNome(), getTipo());
    }

    public static class Builder {

        private String nome;
        private TipoJava tipo;

        public Builder comNome( final String nome ) {

            this.nome = nome;
            return this;
        }

        public Builder comTipo( final TipoJava tipo ) {

            this.tipo = tipo;
            return this;
        }

        public Variavel build() {

            return new Variavel( this );
        }
    }

}
