package br.com.assistente.infra.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static br.com.assistente.infra.util.UtilOS.OSType.WINDOWS;
import static br.com.assistente.infra.util.UtilOS.SO_CORRENTE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.notExists;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class UtilArquivo {

    public static URL getResource( final String recurso ) {

        if ( isNotBlank( recurso ) )
            return UtilArquivo.class.getResource( recurso.startsWith("/") ? recurso : "/".concat( recurso ) );

        throw new IllegalArgumentException( "Argumento inválido para UtilArquivo.getResource" );
    }

    public static Path getResourceFolder( final String folder ) {

        final String path = getResource( folder ).getPath();
        if ( Objects.equals( SO_CORRENTE, WINDOWS ) ) {
            final String substring = path.substring(1);
            return Paths.get( substring );
        }
        return Paths.get( path );
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

}
