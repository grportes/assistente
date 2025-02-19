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
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lastIndexOfIgnoreCase;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

public class MapeamentoService {

    public Set<ModeloCampo> extrair( final Modelo modelo ) {
        final List<DataType> dataTypes = SetupUsuario.buscarDataTypesCnxSelecionada();

        return getMetaData( modelo )
            .stream()
            .map( m -> {
                final DataType dataType = dataTypes.stream()
                    .filter( type -> equalsIgnoreCase( m.getTipoDB(), type.getDbType() ) )
                    .findFirst()
                    .orElseThrow( () -> new RuntimeException( format(
                        "Coluna [ %s ] do tipo [ %s ] Não localizou tipo Java correspondente",
                        m.getColunaDB(), m.getTipoDB()
                    ) ) );

                // Tratamento p/ execeções!!
                if ( equalsIgnoreCase( dataType.getJavaType(), "java.math.BigDecimal" ) ) {
                    if ( startsWithIgnoreCase( m.getColunaDB(), "cpf" )
                        || startsWithIgnoreCase( m.getColunaDB(), "cgc" )
                        || startsWithIgnoreCase( m.getColunaDB(), "cnpj" ) )
                        return new ModeloCampo.Builder( m )
                            .comDataType( new DataType.Builder( dataType ).comJavaType( "java.lang.Long" ).build() )
                            .build();
                }

                return new ModeloCampo.Builder( m ).comDataType( dataType ).build();
            } ).collect( toSet() );
    }

    public Set<ResultMapeamento> executar( final Modelo modelo ) {

        if ( isNull( modelo ) || isEmpty( modelo.getCampos() ) )
            throw new IllegalArgumentException( "É necessário informar a tabela!!" );

        final String nomeAutor = SetupUsuario.find().map( SetupUsuario::getAutor ).orElse( "????" );
        final Set<ModeloCampo> camposPk = buscarPks( modelo.getCampos() );
        final Set<ModeloCampo> campos = modelo.getCampos();
        final Set<ResultMapeamento> results = new LinkedHashSet<>( 2 );

        if ( modelo.isChaveComposta() ) {
            campos.removeAll( camposPk );
            final String embeddableEntity = modelo.getEntidade() + "Id";
            final String entidadeId = gerarMapeamento( nomeAutor, modelo, camposPk, "entidadeId.vm" );
            final String entidade = gerarMapeamento( nomeAutor, modelo, campos, "entidade.vm" );

            results.add(
                new ResultMapeamento.Builder()
                    .comTipoResult( MAPEAMENTO )
                    .comNomePacote( modelo.getCatalogo() )
                    .comNomeEntidade( modelo.getEntidade() )
                    .comTipoDadosId( embeddableEntity )
                    .comConteudoEntidade( entidade )
                    .build()
            );
            results.add(
                new ResultMapeamento.Builder()
                    .comTipoResult( MAPEAMENTO )
                    .comNomePacote( modelo.getCatalogo() )
                    .comNomeEntidade( embeddableEntity )
                    .comTipoDadosId( embeddableEntity )
                    .comConteudoEntidade( entidadeId )
                    .build()
            );
        } else {
            results.add(
                new ResultMapeamento.Builder()
                    .comTipoResult( MAPEAMENTO )
                    .comNomePacote( modelo.getCatalogo() )
                    .comNomeEntidade( modelo.getEntidade() )
                    .comTipoDadosId( camposPk.stream().map( ModeloCampo::getTipoJava ).findFirst().orElse( "??" ) )
                    .comConteudoEntidade( gerarMapeamento( nomeAutor, modelo, campos, "entidade.vm" ) )
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
        final Set<ResultMapeamento> rsMapeamentos,
        final Function<Tuple2<String, String>, Boolean> callbackConfirmacao
    ) {
        final Path rootPath = SetupUsuario.buscarLocalProjeto().resolve( "app" ).resolve( "models" );
        if ( !exists( rootPath ) )
            throw new IllegalArgumentException( format( "Não foi possível localizar caminho: %s", rootPath ) );

        final Tuple2<String, String> nomeEntidadePacote =
            buscarNomeEntidadePacote( rsMapeamentos )
                .orElseThrow( () -> new IllegalArgumentException( "Não localizou resultado" ) );

        final String nomeModulo = isNotBlank( nomeEntidadePacote._2() ) ? nomeEntidadePacote._2() : "";

        // Domain:
        final Path pathDomain = rootPath.resolve( "domains" ).resolve( nomeModulo );

        final String pathDomains = rsMapeamentos.stream()
            .map( mapeamento -> format( "%s.java", mapeamento.getNomeEntidade() ) )
            .map( pathDomain::resolve )
            .map( Path::toString )
            .collect( joining( "\n" ) );

        // Repository:
        final Path rootPathRep = rootPath.resolve( "repository" ).resolve( nomeModulo );
        final Path pathRep = rootPathRep.resolve( format( "%sRepository.java", nomeEntidadePacote._1() ) );
        final Path pathRepImpl = rootPathRep.resolve( "impl" ).resolve( format( "JPA%sRepository.java", nomeEntidadePacote._1() ) );

        final StringJoiner pathRepositories = new StringJoiner( "\n" )
            .add( pathRep.toString() )
            .add( pathRepImpl.toString() );

        final Boolean criarRepository = callbackConfirmacao.apply(
            new Tuple2<>( pathDomains, pathRepositories.toString() )
        );

        if ( isNull( criarRepository ) ) return;

        rsMapeamentos.forEach( m -> {
            final Path p = pathDomain.resolve( format( "%s.java", m.getNomeEntidade() ) );
            final StringJoiner texto = new StringJoiner( "\n" );
            texto.add( format( "package models.domains%s;\n", isNotBlank( nomeModulo ) ? ".".concat( nomeModulo ) : "" ) );
            texto.add( m.getConteudoEntidade() );
            try {
                Files.write( p, texto.toString().getBytes() );
            } catch ( IOException e ) {
                throw new UncheckedIOException( format( "Falhou gravação de %s", p.toString() ), e );
            }
        } );

        if ( criarRepository ) {
            if ( exists( pathRep ) )
                throw new RuntimeException( "Classe(s) domain criada(s) porém não foi possível criar classe(s) Repository, pois já existem!" );

            final String nomeAutor = SetupUsuario.find().map( SetupUsuario::getAutor ).orElse( "????" );
            final String nomePacote = "models.repository." + nomeModulo;
            final Tuple2<String, String> tuple = extrarImport( rsMapeamentos, nomeModulo );

            VelocityContext context = new VelocityContext();
            context.put( "nomePacote", nomePacote );
            context.put( "nomeAutor", nomeAutor );
            context.put( "nomeDomain", nomeEntidadePacote._1() );
            context.put( "tipoJavaId", tuple._1() );
            context.put( "importJavaId", tuple._2() );
            context.put( "StringUtils", StringUtils.class );
            gerarArquivo( context, "repository.vm", pathRep );

            context = new VelocityContext();
            context.put( "nomePacote", nomePacote );
            context.put( "nomeAutor", nomeAutor );
            context.put( "nomeDomain", nomeEntidadePacote._1() );
            context.put( "tipoJavaId", tuple._1() );
            context.put( "importJavaId", tuple._2() );
            context.put( "StringUtils", StringUtils.class );
            gerarArquivo( context, "repositoryImpl.vm", pathRepImpl );
        }
    }

    private Tuple2<String, String> extrarImport(
        final Set<ResultMapeamento> rsMapeamentos,
        final String nomeModulo
    ) {
        if ( rsMapeamentos.size() == 2 )
            return rsMapeamentos.stream()
                .findFirst()
                .map( rs -> {
                    final String tipoJava = lastIndexOfIgnoreCase( rs.getNomeEntidade(), "Id" ) == -1
                        ? rs.getNomeEntidade().concat( "Id" ) : rs.getNomeEntidade();
                    final String importJava = isNotBlank( nomeModulo )
                        ? "models.domains.".concat( nomeModulo )
                        : "models.domains";
                    return new Tuple2<>( tipoJava, importJava );
                } )
                .orElseThrow( () -> new RuntimeException( "Falhou extração dos imports" ) );

        final String tipo = rsMapeamentos.stream()
            .map( ResultMapeamento::getTipoDadosId )
            .findFirst()
            .orElseThrow( () -> new RuntimeException( "Não localizou ID da classe" ) );

        if ( startsWith( tipo, "java.lang" ) )
            return new Tuple2<>( substringAfter( tipo, "java.lang." ), "" );

        final String tipoBase = substringAfterLast( tipo, "." );
        final String importPacote = removeEnd( tipo, "." + tipoBase );
        return new Tuple2<>( tipoBase, importPacote );
    }
}
