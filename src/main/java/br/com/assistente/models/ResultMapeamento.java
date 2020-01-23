package br.com.assistente.models;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static org.apache.commons.lang3.StringUtils.trim;

public final class ResultMapeamento {

    private final String nomeEntidade;
    private final String conteudoEntidade;
    private final TipoResult tipoResult;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTOR
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMapeamento( final Builder builder ) {

        this.nomeEntidade = builder.nomeEntidade;
        this.conteudoEntidade = builder.conteudoEntidade;
        this.tipoResult = builder.tipoResult;
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

    public static void gravarArquivo( final Set<ResultMapeamento> results ) {

        final Optional<ResultMapeamento> possivelTipo = results
            .stream()
            .filter( r -> Objects.nonNull( r.getTipoResult() ) )
            .findFirst();

        if ( !possivelTipo.isPresent() ) return;

        final Path localProjeto = SetupUsuario
            .buscarLocalProjeto()
            .orElseThrow( () -> new IllegalArgumentException( "Favor informar o local do projeto!" ) );

        if ( !exists(localProjeto) )
            throw new IllegalArgumentException( format( "Não foi possível localizar [%s]", localProjeto ) );

        switch ( possivelTipo.get().getTipoResult() ) {
            case MAPEAMENTO:
                break;
            case DTO:
                break;
            case CONSTANTE:
                break;
        }


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

            this.tipoResult = tipoResult;
            return this;
        }

        public ResultMapeamento build() {

            return new ResultMapeamento( this );
        }

    }

}
