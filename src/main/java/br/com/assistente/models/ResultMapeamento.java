package br.com.assistente.models;

import io.vavr.Tuple2;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.empty;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.lastIndexOfIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trim;

public final class ResultMapeamento {

    private final String nomeEntidade;
    private final String conteudoEntidade;
    private final TipoResult tipoResult;
    private String nomePacote;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTOR
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMapeamento( final Builder builder ) {

        this.nomeEntidade = builder.nomeEntidade;
        this.conteudoEntidade = builder.conteudoEntidade;
        this.tipoResult = builder.tipoResult;
        this.nomePacote = builder.nomePacote;
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

    public TipoResult getTipoResult() {

        return tipoResult;
    }

    public String getNomePacote() {

        return nomePacote;
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
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Optional<Tuple2<String, String>> buscarNomeEntidadePacote( final Set<ResultMapeamento> mapeamentos ) {

        return isEmpty( mapeamentos )
            ? empty()
            : mapeamentos.stream()
                .filter( m -> lastIndexOfIgnoreCase( m.getNomeEntidade(), "Id" ) == -1  )
                .findFirst()
                .map( m -> new Tuple2<>( m.getNomeEntidade(), m.getNomePacote() ) );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BUILDER
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Builder {

        private String nomeEntidade;
        private String conteudoEntidade;
        private TipoResult tipoResult;
        private String nomePacote;

        public Builder() {

            this.nomeEntidade = null;
            this.conteudoEntidade = null;
            this.tipoResult = null;
        }

        public Builder comNomeEntidade( final String value ) {

            this.nomeEntidade = trim( value );
            return this;
        }

        public Builder comConteudoEntidade( final String value ) {

            this.conteudoEntidade = trim( value );
            return this;
        }

        public Builder comTipoResult( final TipoResult value ) {

            this.tipoResult = value;
            return this;
        }

        public Builder comNomePacote( final String value ) {

            this.nomePacote = value;
            return this;
        }

        public ResultMapeamento build() {

            return new ResultMapeamento( this );
        }

    }

}
