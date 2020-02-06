package br.com.assistente.infra.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static br.com.assistente.infra.util.UtilString.requireNotBlank;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.notExists;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.substringBetween;

public final class UtilArquivo {

    public static URL getResource( final String recurso ) {

        requireNotBlank( recurso, "Argumento inválido para UtilArquivo.getResource" );
        return UtilArquivo.class.getResource( recurso.startsWith("/") ? recurso : "/".concat( recurso ) );
    }

    public static Path buscarPastaAplicacao() {

        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath();
    }

    public static void gravar(
        final Path arquivo,
        final String dados
    ) {

        try {
            byte[] bytes = dados.getBytes( UTF_8 );
            Files.write( arquivo, bytes );
        } catch ( final IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    public static void gravar(
        final Path arquivo,
        final StringWriter dados
    ) {

        gravar( arquivo, dados.toString() );
    }

    public static boolean excluir( final Path path ) {

        requireNonNull( path );

        try {
            return deleteIfExists( path );
        } catch ( final IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    public static Path getPathSetup()  {

        final Path pasta = buscarPastaAplicacao();
        if ( notExists(pasta) )
            throw new RuntimeException( "Não foi possivel localizar pasta do aplicativo" );

        return pasta;
    }

    public static void createDirectoryIfNotExists( final Path path ) {

        try {
            if ( !exists( path )) createDirectory( path );
        } catch ( IOException e ) {
            throw new UncheckedIOException( "Falhou criar pasta" + path.toString(), e );
        }
    }

    public static String buscarNomeArquivoAplicacaoJar() {

        final URL resource = UtilArquivo.class.getResource( "" );

        return requireNotBlank(
                substringBetween( resource.getFile(), "file:", ".jar!" ),
                "Arquivo jar não localizado!"
        ).concat( ".jar" );

    }

}
