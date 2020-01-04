package br.com.assistente.models;

import java.util.Objects;
import java.util.Set;

import static br.com.assistente.infra.util.UtilString.convCamelCase;
import static br.com.assistente.models.ModeloCampo.chaveComposta;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class Modelo {

    private final String catalogo;
    private final String owner;
    private final String tabela;
    private final String entidade;
    private final Set<ModeloCampo> campos;
    private final boolean chaveComposta;


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
        this.chaveComposta = chaveComposta( this.campos );
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

    public boolean isChaveComposta() {

        return chaveComposta;
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
