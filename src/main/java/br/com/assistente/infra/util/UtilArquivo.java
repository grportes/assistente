package br.com.assistente.infra.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.notExists;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static org.apache.commons.lang3.StringUtils.toEncodedString;

public final class UtilArquivo {

    public static URL getResource( final String recurso ) {

        return UtilArquivo.class.getResource( recurso );
    }

    public static Path buscarLocalApp() {

        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath();
    }

    public static boolean gravarBinario(
        final Path arquivo,
        final String dados
    ) {

        try {
            byte[] bytes = dados.getBytes( UTF_8 );
            Files.write( arquivo, bytes );
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Optional<String> lerArquivoBinario( final Path arquivo ) {

        try {
            byte[] bytes = Files.readAllBytes(arquivo);
            String dados = toEncodedString( bytes, UTF_8 );
            return StringUtils.isBlank( dados ) ? empty() : Optional.of( dados );
        } catch (IOException e) {
            return empty();
        }
    }

    public static boolean excluir( final Path path ) {

        if ( isNull(path) || notExists(path) ) return true;

        try {
            Files.delete( path );
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
