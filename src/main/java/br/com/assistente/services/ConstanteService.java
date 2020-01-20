package br.com.assistente.services;

import br.com.assistente.infra.util.UtilVelocity;
import br.com.assistente.models.Constante;
import br.com.assistente.models.ResultMapeamento;
import br.com.assistente.models.SetupUsuario;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;

public class ConstanteService {

    public Set<ResultMapeamento> convTexto(
        final String nomeEnum,
        final Constante.Tipo tipo,
        final Set<Constante> constantes,
        final boolean gerarConverter
    ) {

        final String nomeAutor = SetupUsuario.find().map(SetupUsuario::getAutor).orElse("????");
        final Set<ResultMapeamento> results = new LinkedHashSet<>( 2 );

        results.add(
            new ResultMapeamento.Builder()
                .comNomeEntidade( nomeEnum )
                .comConteudoEntidade( gerarMapeamento( nomeAutor, nomeEnum, tipo, constantes,"/templates/constante.vm") )
                .build()
        );

        if ( gerarConverter )
            results.add(
                new ResultMapeamento.Builder()
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
        if ( !Files.exists( path ) )
            throw new IllegalArgumentException( "Arquivo não localizado!" );

        final File fileHandle = path.toFile();

        try (
            final InputStream inputStream = new FileInputStream(fileHandle);
            final BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
        ){

            final Function<String, Constante> fnMapToConstante = row -> {
                final String[] cols = row.split( "\t" );
                if ( cols.length == 2 )
                    return new Constante.Builder()
                            .comNome( cols[0] )
                            .comValor( cols[1] )
                            .comDescricao( cols[0] )
                            .build();
                throw new RuntimeException( format( "Linha inválida: %s", row ) );
            };

            return buffer.lines().skip(1).map( fnMapToConstante ).collect( Collectors.toSet() );

        } catch ( final IOException e ) {
            throw new RuntimeException( e );
        }
    }
}
