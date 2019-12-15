package br.com.assistente.models;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Set;

import static br.com.assistente.infra.util.UtilString.capitalize;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class Modelo {

    private String banco;
    private String owner;
    private String tabela;
    private String entidade;
    private Set<ModeloCampo> campos;

    public Modelo( final Builder builder ) {

        this.banco = builder.banco;
        this.owner = builder.owner;
        this.tabela = builder.tabela;
        this.entidade = builder.entidade;
        this.campos = builder.campos;
    }

    public String getBanco() {

        return banco;
    }

    public String getOwner() {

        return owner;
    }

    public String getTabela() {

        return tabela;
    }

    public String getEntidade() {

        return entidade;
    }

    public Set<ModeloCampo> getCampos() {

        return campos;
    }

    public boolean containsLocalDate( ) {

        return isNotEmpty(campos) && campos.stream().anyMatch(c -> equalsIgnoreCase( c.getTipoJava(), "LocalDate" ) );
    }

    public boolean containsLocalDateTime( ) {

        return isNotEmpty(campos) && campos.stream().anyMatch(c -> equalsIgnoreCase( c.getTipoJava(), "LocalDateTime" ) );
    }

    public boolean containsBigDecimal( ) {

        return isNotEmpty(campos) && campos.stream().anyMatch(c -> equalsIgnoreCase( c.getTipoJava(), "BigDecimal" ) );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Modelo)) return false;
        Modelo modelo = (Modelo) o;
        return Objects.equals(getTabela(), modelo.getTabela());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTabela());
    }

    public static class Builder {

        private String banco;
        private String owner;
        private String tabela;
        private String entidade;
        private Set<ModeloCampo> campos;

        public Builder comBanco( final String value ) {

            this.banco = value;
            return this;
        }

        public Builder comOwner( final String value ) {

            this.owner = value;
            return this;
        }

        public Builder comTabela( final String value ) {

            this.tabela = value;
            this.entidade = StringUtils.capitalize( capitalize( value ) );
            return this;
        }

        public Builder comCampos( final Set<ModeloCampo> campos ) {

            this.campos = campos;
            return this;
        }

        public Modelo build() {

            return new Modelo( this );
        }
    }


}
