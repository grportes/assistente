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

import static br.com.assistente.infra.util.UtilString.normalizeJava;
import static br.com.assistente.infra.util.UtilString.requireNotBlank;
import static br.com.assistente.infra.util.UtilVelocity.exec;
import static br.com.assistente.models.DefinicaoDto.buscarImports;
import static br.com.assistente.models.DefinicaoDto.buscarImportsSerializer;
import static br.com.assistente.models.DefinicaoDto.buscarImportsTupleConverter;
import static br.com.assistente.models.DefinicaoDto.buscarTodosAtributoId;
import static br.com.assistente.models.DefinicaoDto.orderByPosicao;
import static br.com.assistente.models.TipoResult.DTO;
import static java.lang.String.format;
import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class QueryService {

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

        results.add(
            new ResultMapeamento.Builder()
                .comNomeEntidade( nomeClasse )
                .comConteudoEntidade( gerarDto( nomeClasse, dtosComIdentity, gerarJsonAnnotations, gerarClasseBuilder ) )
                .comTipoResult( DTO )
                .build()
        );

        results.add(
            new ResultMapeamento.Builder()
                .comNomeEntidade( "XML Query" )
                .comConteudoEntidade( gerarXMLQuery( query, nomeClasse ) )
                .comTipoResult( DTO )
                .build()
        );

        return results;
    }

    public Set<ResultMapeamento> convTexto( final String query ) {

        requireNotBlank( query, "É necessário informar a query!" );
        final Set<DefinicaoDto> campos = extrairColunas( query );
        final Set<ResultMapeamento> results = new LinkedHashSet<>( 2 );

        results.add(
            new ResultMapeamento.Builder()
                .comNomeEntidade( "Frag. Codigo" )
                .comConteudoEntidade( gerarTuple( campos ) )
                .comTipoResult( DTO )
                .build()
        );

        results.add(
            new ResultMapeamento.Builder()
                .comNomeEntidade( "XML Query" )
                .comConteudoEntidade( gerarXMLQuery( query, null ) )
                .comTipoResult( DTO )
                .build()
        );

        return results;
    }

    private Set<DefinicaoDto> extrairColunas( final String query ) {

        final List<DataType> dataTypes = SetupUsuario.buscarDataTypesCnxSelecionada();

        final Set<ModeloCampo> dados = ConnectionFactory.execQuery( query );
        if ( isEmpty( dados ) )
            throw new IllegalArgumentException(
                    "Não foi possivel ler metadados da query - Verifique se query esta retornando valores" );

        return dados.stream()
            .map( m -> {
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
                    .comNomeAtributo( normalizeJava( m.getColunaJava(), false ) )
                    .comTipo( dataType )
                    .build();
            } ).collect( toSet() );
    }

    private String gerarTuple( final Set<DefinicaoDto> campos ) {

        final VelocityContext context = new VelocityContext();
        context.put( "campos", orderByPosicao( campos ) );
        context.put( "importsNecessarios", buscarImports( campos ) );
        context.put( "importsTupleConverter", buscarImportsTupleConverter( campos ) );
        context.put( "StringUtils", StringUtils.class );
        return exec( context, "/templates/query_tuple.vm" );
    }

    private String gerarXMLQuery(
        final String query,
        final String nomeClasse
    ) {

        final VelocityContext context = new VelocityContext();
        context.put( "query", query );
        context.put( "nomeClasse", nomeClasse );
        context.put( "StringUtils", StringUtils.class );
        return exec( context, "/templates/query_xml.vm" );
    }

    private String gerarDto(
        final String nomeClasse,
        final Set<DefinicaoDto> definicoes,
        boolean gerarJsonAnnotations,
        boolean gerarClasseBuilder
    ) {

        final String nomeAutor = SetupUsuario.find().map(SetupUsuario::getAutor).orElse("????");

        final VelocityContext context = new VelocityContext();
        context.put( "nomeAutor", nomeAutor );
        context.put( "nomeClasse", nomeClasse );
        context.put( "definicoes", orderByPosicao( definicoes ) );
        context.put( "gerarJsonAnnotations", gerarJsonAnnotations );
        context.put( "gerarClasseBuilder", gerarClasseBuilder );
        context.put( "importsNecessarios", buscarImports( definicoes ) );
        context.put( "importsNecessariosSerializer", buscarImportsSerializer( definicoes ) );
        context.put( "identificadores", buscarTodosAtributoId( definicoes ) );
        context.put( "StringUtils", StringUtils.class );
        context.put( "dataHora", now().format( ofPattern( "dd/MM/yyyy" ) ) );

        return exec( context, "/templates/query_dto.vm" );
    }
}