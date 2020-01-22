package br.com.assistente.services;

import br.com.assistente.infra.db.ConnectionFactory;
import br.com.assistente.models.DataType;
import br.com.assistente.models.DefinicaoDto;
import br.com.assistente.models.ModeloCampo;
import br.com.assistente.models.ResultMapeamento;
import br.com.assistente.models.SetupUsuario;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static br.com.assistente.infra.util.UtilString.convCamelCase;
import static br.com.assistente.infra.util.UtilString.requireNotBlank;
import static br.com.assistente.infra.util.UtilVelocity.exec;
import static br.com.assistente.models.DefinicaoDto.buscarImports;
import static br.com.assistente.models.DefinicaoDto.buscarImportsTupleConverter;
import static br.com.assistente.models.DefinicaoDto.orderByPosicao;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class QueryService {

    private Set<DefinicaoDto> extrairColunas( final String query ) {

        final List<DataType> dataTypes = SetupUsuario.buscarDataTypes();

        final Set<ModeloCampo> dados = ConnectionFactory.execQuery( query );
        if ( isEmpty( dados ) )
            throw new IllegalArgumentException(
                    "Não foi possivel ler metadados da query - Verifique se query esta retornando valores" );

        return dados.stream().map( m -> {
            final DataType dataType = dataTypes
                    .stream()
                    .filter( type -> Objects.equals( m.getTipoDB(), type.getDbType() ) )
                    .findFirst()
                    .orElseThrow( () -> new RuntimeException( format(
                            "Coluna [ %s ] do tipo [ %s ] Não localizou tipo Java correspondente",
                            m.getColunaDB(), m.getTipoDB()
                    ) ) );

            return new DefinicaoDto.Builder()
                    .comPosicao( m.getPosicao() )
                    .comNomeAtributo( convCamelCase( m.getColunaJava(), false ) )
                    .comTipo( dataType )
                    .build();
        } ).collect( toSet() );
    }

    public Set<ResultMapeamento> convTexto( final String query ) {

        final Set<DefinicaoDto> campos = extrairColunas( query );
        final Set<ResultMapeamento> results = new LinkedHashSet<>( 2 );

        results.add(
            new ResultMapeamento.Builder()
                .comNomeEntidade( "Frag. Codigo" )
                .comConteudoEntidade( gerarMapeamento( campos ) )
                .build()
        );

        results.add(
            new ResultMapeamento.Builder()
                .comNomeEntidade( "XML Query" )
                .comConteudoEntidade( gerarXMLQuery( query, false ) )
                .build()
        );

        return results;
    }

    private String gerarMapeamento( final Set<DefinicaoDto> campos ) {

        final VelocityContext context = new VelocityContext();
        context.put( "campos", orderByPosicao( campos ) );
        context.put( "importsNecessarios", buscarImports( campos ) );
        context.put( "importsTupleConverter", buscarImportsTupleConverter( campos ) );
        context.put( "StringUtils", StringUtils.class );
        context.put( "query", StringUtils.class );
        return exec( context, "/templates/query_tuple.vm" );
    }

    private String gerarXMLQuery(
        final String query,
        final boolean exibirResultClass
    ) {

        final VelocityContext context = new VelocityContext();
        context.put( "query", query );
        context.put( "exibirResultClass", exibirResultClass );
        return exec( context, "/templates/query_xml.vm" );
    }

    public Set<ResultMapeamento> convTexto(
        final String nomeClasse,
        final String query,
        boolean gerarJsonAnnotations,
        boolean gerarClasseBuilder,
        final Function<Set<DefinicaoDto>, Set<DefinicaoDto>> callback
    ) {

        requireNotBlank( nomeClasse, "Nome da DTO vazio!" );
        requireNotBlank( query, "Query vazia!" );
        requireNonNull( callback, "Callback necessário para gerar DTO/Query!" );

        final Set<DefinicaoDto> dtos = extrairColunas( query );

        final Set<DefinicaoDto> dtosComIdentity = callback.apply( dtos );

        final Set<ResultMapeamento> results = new LinkedHashSet<>( 2 );

        results.addAll(
            new DefinicaoDtoService()
            .convTexto( nomeClasse, dtosComIdentity, gerarJsonAnnotations, gerarClasseBuilder )
        );

        results.add(
            new ResultMapeamento.Builder()
            .comNomeEntidade( "XML Query" )
            .comConteudoEntidade( gerarXMLQuery( query, false ) )
            .build()
        );

        return results;
    }
}