package br.com.assistente.models;

import java.util.Objects;
import java.util.Set;

import static br.com.assistente.infra.util.UtilString.convCamelCase;
import static java.lang.String.format;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class Modelo {

    private String catalogo;
    private String owner;
    private String tabela;
    private String entidade;
    private Set<ModeloCampo> campos;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTOR
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Modelo( final Builder builder ) {

        this.catalogo = builder.banco;
        this.owner = builder.owner;
        this.tabela = builder.tabela;
        this.entidade = builder.entidade;
        this.campos = builder.campos;
    }

    public String getCatalogo() {

        return catalogo;
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

    public String getNomeCompletoTabela() {

        return format(
            "%s%s%s",
            isNotBlank( getCatalogo() ) ? getCatalogo() + "." : "",
            isNotBlank( getOwner() ) ? getOwner() + "." : "",
            getTabela()
        );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EQUALS && HASHCODE
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BUILDER
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
            this.entidade = convCamelCase( value, true );
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
