package br.com.assistente.infra.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.deleteIfExists;
import static java.util.Objects.requireNonNull;

public final class UtilArquivo {

    public static URL getResource( final String recurso ) {

        return UtilArquivo.class.getResource( recurso );
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

}
