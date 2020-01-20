package br.com.assistente.services;

import br.com.assistente.infra.db.ConnectionFactory;
import br.com.assistente.models.DataType;
import br.com.assistente.models.Modelo;
import br.com.assistente.models.ModeloCampo;
import br.com.assistente.models.ResultMapeamento;
import br.com.assistente.models.SetupUsuario;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static br.com.assistente.infra.util.UtilVelocity.exec;
import static br.com.assistente.models.ModeloCampo.buscarImports;
import static br.com.assistente.models.ModeloCampo.orderByPosicao;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class QueryService {

    private Set<ModeloCampo> extrairColunas( final String query ) {

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
            return new ModeloCampo.Builder( m ).comDataType( dataType ).build();
        } ).collect( toSet() );
    }


    public Set<ResultMapeamento> convTexto( final String query ) {

        final Set<ModeloCampo> campos = extrairColunas( query );
        final Set<ResultMapeamento> results = new LinkedHashSet<>( 2 );
        final String nomeAutor = SetupUsuario.find().map(SetupUsuario::getAutor).orElse("????");

        results.add(
            new ResultMapeamento.Builder()
                .comNomeEntidade( "Frag. Codigo" )
                .comConteudoEntidade( gerarMapeamento( nomeAutor, null, campos,"/templates/query_tuple.vm"))
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

    private String gerarMapeamento(
        final String nomeAutor,
        final Modelo modelo,
        final Set<ModeloCampo> campos,
        final String arquivoTemplate
    ) {

        final VelocityContext context = new VelocityContext();
        context.put( "nomeAutor", nomeAutor );
        context.put( "modelo", modelo );
        context.put( "campos", orderByPosicao( campos ) );
        context.put( "importsNecessarios", buscarImports( campos ) );
        context.put( "StringUtils", StringUtils.class );
        context.put( "query", StringUtils.class );
        return exec( context, arquivoTemplate );
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

}
