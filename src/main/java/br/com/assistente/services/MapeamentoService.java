package br.com.assistente.services;

import br.com.assistente.models.DataType;
import br.com.assistente.models.Modelo;
import br.com.assistente.models.ModeloCampo;
import br.com.assistente.models.ResultMapeamento;
import br.com.assistente.models.SetupUsuario;
import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;

import static br.com.assistente.infra.db.ConnectionFactory.getMetaData;
import static br.com.assistente.infra.util.UtilVelocity.exec;
import static br.com.assistente.infra.util.UtilVelocity.gerarArquivo;
import static br.com.assistente.models.ModeloCampo.buscarImports;
import static br.com.assistente.models.ModeloCampo.buscarPks;
import static br.com.assistente.models.ModeloCampo.orderByPosicao;
import static br.com.assistente.models.ResultMapeamento.buscarNomeEntidadePacote;
import static br.com.assistente.models.TipoResult.MAPEAMENTO;
import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MapeamentoService {

    public Set<ModeloCampo> extrair( final Modelo modelo )  {

        final List<DataType> dataTypes = SetupUsuario.buscarDataTypes();

        return getMetaData( modelo )
            .stream()
            .map( m -> {
                final DataType dataType = dataTypes
                    .stream()
                    .filter( type -> Objects.equals( m.getTipoDB(), type.getDbType() ) )
                    .findFirst()
                    .orElseThrow( () -> new RuntimeException( format(
                        "Coluna [ %s ] do tipo [ %s ] Não localizou tipo Java correspondente",
                        m.getColunaDB(), m.getTipoDB()
                    )));
                return new ModeloCampo.Builder( m ).comDataType( dataType ).build();
            }).collect( toSet() );
    }

    public Set<ResultMapeamento> executar( final Modelo modelo ) {

        if ( isNull( modelo ) || isEmpty( modelo.getCampos() ) ) return emptySet();

        final String nomeAutor = SetupUsuario.find().map(SetupUsuario::getAutor).orElse("????");
        final Set<ModeloCampo> camposPk = buscarPks( modelo.getCampos() );
        final Set<ModeloCampo> campos = modelo.getCampos();
        final Set<ResultMapeamento> results = new LinkedHashSet<>( 2 );

        if ( modelo.isChaveComposta() ) {
            campos.removeAll( camposPk );
            final String embeddableEntity = modelo.getEntidade() + "Id";
            results.add(
                new ResultMapeamento.Builder()
                    .comTipoResult( MAPEAMENTO )
                    .comNomePacote( modelo.getCatalogo() )
                    .comNomeEntidade( modelo.getEntidade() )
                    .comTipoDadosId( embeddableEntity )
                    .comConteudoEntidade( gerarMapeamento( nomeAutor, modelo, campos,"/templates/entidade.vm"))
                    .build()
            );
            results.add(
                new ResultMapeamento.Builder()
                    .comTipoResult( MAPEAMENTO )
                    .comNomePacote( modelo.getCatalogo() )
                    .comNomeEntidade( embeddableEntity )
                    .comTipoDadosId( embeddableEntity )
                    .comConteudoEntidade( gerarMapeamento( nomeAutor, modelo, camposPk,"/templates/entidadeId.vm"))
                    .build()
            );
        } else {
            results.add(
                new ResultMapeamento.Builder()
                    .comTipoResult( MAPEAMENTO )
                    .comNomePacote( modelo.getCatalogo() )
                    .comNomeEntidade( modelo.getEntidade() )
                    .comTipoDadosId( camposPk.stream().map(ModeloCampo::getTipoJava).findFirst().orElse( "??") )
                    .comConteudoEntidade(gerarMapeamento(nomeAutor, modelo, campos, "/templates/entidade.vm"))
                    .build()
            );
        }

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
        return exec( context, arquivoTemplate );
    }

    public void gravarArquivos(
        final Set<ResultMapeamento> mapeamentos,
        final Function<Tuple2<String,String>, Boolean> callbackConfirmacao
    ) {

        final Path rootPath = SetupUsuario.buscarLocalProjeto().resolve( "app" ).resolve( "models" );
        if ( !exists(rootPath) )
            throw new IllegalArgumentException( format( "Não foi possível localizar caminho: %s", rootPath ) );

        final Tuple2<String,String> nomeEntidadePacote =
            buscarNomeEntidadePacote( mapeamentos )
            .orElseThrow( () -> new IllegalArgumentException( "Não localizou resultado" ) );

        final boolean existeModulo = isNotBlank( nomeEntidadePacote._2() );

        // Domain:
        final Path pathDomain = rootPath.resolve( "domains" ).resolve( existeModulo ? nomeEntidadePacote._2() : "" );

        final String pathDomains = mapeamentos.stream()
            .map( mapeamento -> format( "%s.java", mapeamento.getNomeEntidade() ) )
            .map( pathDomain::resolve )
            .map( Path::toString )
            .collect( joining( "\n" ) );

        // Repository:
        final Path rootPathRep = rootPath.resolve( "repository" ).resolve( existeModulo ? nomeEntidadePacote._2() : "" );
        final Path pathRep = rootPathRep.resolve( format( "%sRepository.java", nomeEntidadePacote._1() ) );
        final Path pathRepImpl = rootPathRep.resolve( "impl" ).resolve( format( "JPA%sRepository.java", nomeEntidadePacote._1() ) );

        final StringJoiner pathRepositories = new StringJoiner( "\n" )
            .add( pathRep.toString() )
            .add( pathRepImpl.toString() );

        final Boolean criarRepository = callbackConfirmacao.apply(
            new Tuple2<>( pathDomains, pathRepositories.toString() )
        );

        if ( isNull( criarRepository ) ) return;

        mapeamentos.forEach( m -> {
            final Path p = pathDomain.resolve( format( "%s.java", m.getNomeEntidade() ) );
            try {
                Files.write( p, m.getConteudoEntidade().getBytes() );
            } catch ( IOException e ) {
                throw new UncheckedIOException( format( "Falhou gravação de %s", p.toString()), e );
            }
        });

        if ( criarRepository ) {
            if ( exists( pathRep ) )
                throw new RuntimeException( "Classe(s) domain criada(s) porém não foi possível criar classe(s) Repository, pois já existem!" );

            final String nomeAutor = SetupUsuario.find().map(SetupUsuario::getAutor).orElse("????");
            final String domainId = mapeamentos.stream().map(ResultMapeamento::getTipoDadosId).findFirst().orElse("??");
            final String nomePacote = "";

            VelocityContext context = new VelocityContext();
            context.put( "nomeAutor", nomeAutor );
            context.put( "nomeDomain", nomeEntidadePacote._1() );
            context.put( "domainId", domainId );
            context.put( "nomePacote", nomePacote );
            context.put( "StringUtils", StringUtils.class );
            gerarArquivo( context, "/templates/repository.vm", pathRep );

            context = new VelocityContext();
            context.put( "nomeAutor", nomeAutor );
            context.put( "nomeDomain", nomeEntidadePacote._1() );
            context.put( "domainId", domainId );
            context.put( "nomePacote", nomePacote );
            context.put( "StringUtils", StringUtils.class );
            gerarArquivo( context, "/templates/repositoryImpl.vm", pathRepImpl );
        }
    }


}
