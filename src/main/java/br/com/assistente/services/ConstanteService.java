package br.com.assistente.services;

import br.com.assistente.infra.util.UtilVelocity;
import br.com.assistente.models.Constante;
import br.com.assistente.models.ResultMapeamento;
import br.com.assistente.models.SetupUsuario;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringJoiner;

import static br.com.assistente.infra.util.UtilArquivo.createDirectoryIfNotExists;
import static br.com.assistente.infra.util.UtilCollections.requireNotEmpty;
import static br.com.assistente.infra.util.UtilString.requireNotBlank;
import static br.com.assistente.models.TipoResult.CONSTANTE;
import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.ArrayUtils.getLength;
import static org.apache.commons.lang3.StringUtils.lastIndexOfIgnoreCase;
import static org.apache.commons.lang3.StringUtils.split;

public class ConstanteService {

    public Set<ResultMapeamento> convTexto(
        final String nomeEnum,
        final Constante.Tipo tipo,
        final Set<Constante> constantes,
        final boolean gerarConverter
    ) {

        requireNotBlank( nomeEnum, "É necessário informar o nome do Enum" );
        requireNotEmpty( constantes, "É necessário informar os atributos do Enum" );

        final String nomeAutor = SetupUsuario.find().map(SetupUsuario::getAutor).orElse("????");
        final Set<ResultMapeamento> results = new LinkedHashSet<>( 2 );

        results.add(
            new ResultMapeamento.Builder()
                .comTipoResult( CONSTANTE )
                .comNomeEntidade( nomeEnum )
                .comConteudoEntidade( gerarMapeamento( nomeAutor, nomeEnum, tipo, constantes,"/templates/constante.vm") )
                .build()
        );

        if ( gerarConverter )
            results.add(
                new ResultMapeamento.Builder()
                    .comTipoResult( CONSTANTE )
                    .comNomeEntidade( nomeEnum + "Converter" )
                    .comConteudoEntidade( gerarMapeamento( nomeAutor, nomeEnum, tipo, constantes,"/templates/converter.vm") )
                    .build()
            );

        return results;
    }

    private String gerarMapeamento(
        final String nomeAutor,
        final String nomeEnum,
        final Constante.Tipo tipo,
        final Set<Constante> constantes,
        final String arquivoTemplate
    ) {

        final VelocityContext context = new VelocityContext();
        context.put( "nomeAutor", nomeAutor );
        context.put( "nomeEnum", nomeEnum );
        context.put( "constantes", constantes );
        context.put( "tipoJava", tipo );
        context.put( "StringUtils", StringUtils.class );
        context.put( "EnumTipoShort", Constante.Tipo.SHORT );
        context.put( "dataHora", now().format( ofPattern( "dd/MM/yyyy" ) ) );

        return UtilVelocity.exec( context, arquivoTemplate );
    }

    public Set<Constante> lerArquivoCSV( final String arquivo ) {

        final Path path = Paths.get( arquivo );
        if ( !exists( path ) )
            throw new IllegalArgumentException( "Arquivo não localizado!" );

        try (
            final InputStream inputStream = new FileInputStream(path.toFile());
            final BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
        ){

            return buffer.lines()
                .map( row -> split( row, "\t" ) )
                .filter( cols -> getLength( cols ) == 2 )
                .map( cols -> new Constante.Builder()
                    .comNome( cols[0] )
                    .comValor( cols[1] )
                    .comDescricao( cols[0] )
                    .build()
                ).collect( toSet() );

        } catch ( final IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public void gravarArquivos( final Set<ResultMapeamento> rsMapeamentos ) {

        final Path rootPath = SetupUsuario.buscarLocalProjeto().resolve( "app" ).resolve( "models" );
        if ( !exists( rootPath ) )
            throw new IllegalArgumentException( format( "Não foi possível localizar caminho: %s", rootPath ) );

        final Path commonsPath = rootPath.resolve( "commons" );
        createDirectoryIfNotExists( commonsPath );

        final StringJoiner existeArquivo = new StringJoiner( "\n" );

        for ( final ResultMapeamento rsMapeamento : rsMapeamentos ) {

            final String pastaBase = lastIndexOfIgnoreCase( rsMapeamento.getNomeEntidade(), "Converter" ) == -1
                ? "constantes"
                : "converters";

            final Path pathBase = commonsPath.resolve( pastaBase );
            createDirectoryIfNotExists( pathBase );

            final Path pathArquivo = pathBase.resolve( rsMapeamento.getNomeEntidade().concat( ".java" ) );
            if ( exists( pathArquivo ) ) {
                existeArquivo.add( pathArquivo.toString() );
                continue;
            }

            try {
                Files.write( pathArquivo, rsMapeamento.getConteudoEntidade().getBytes() );
            } catch ( final IOException e ) {
                throw new UncheckedIOException( format( "Falhou gravação de %s", pathArquivo.toString()), e );
            }

            if ( existeArquivo.length() > 0 )
                throw new RuntimeException( "Não é possivel sobrescrever classe(s) existentes!\n" + existeArquivo );

        }
    }


}
