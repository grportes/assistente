package br.com.assistente.infra.util;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static br.com.assistente.infra.util.UtilArquivo.createDirectoryIfNotExists;
import static br.com.assistente.infra.util.UtilArquivo.excluir;
import static br.com.assistente.infra.util.UtilArquivo.gravar;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.newInputStream;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public final class UtilYaml {

    public static <T> Optional<T> load(
        final Class<T> clazz,
        final Path arquivo
    ) {
        requireNonNull(clazz);
        requireNonNull(arquivo);
        if (!exists(arquivo)) return empty();
        try ( final InputStream in = newInputStream(arquivo) ) {
            final Yaml yaml = new Yaml( new Constructor(clazz));
            return ofNullable( yaml.load(in) );
        } catch ( final IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    public static <T> Optional<T> load(
        final Class<T> clazz,
        final URL resource
    ) {
        requireNonNull(clazz);
        requireNonNull(resource);
        try {
            return load( clazz, Paths.get( resource.toURI() ) );
        } catch ( URISyntaxException e ) {
            throw new RuntimeException( "Falhou leitura de: " + resource );
        }
    }

    public static void dump(
        final Object data,
        final Path arquivo
    ) {
        final DumperOptions options = new DumperOptions();
        options.setAllowReadOnlyProperties(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        final Yaml yaml = new Yaml(options);
        final StringWriter writer = new StringWriter();
        yaml.dump( data, writer );
        excluir( arquivo );
        gravar( arquivo, writer );
    }

    public static Path getArquivoYaml()  {
        final String userHomePath = System.getProperty( "user.home" );
        final Path assistentePath = Paths.get( userHomePath ).resolve( "assistente" );
        createDirectoryIfNotExists( assistentePath );
        return assistentePath.resolve( "assistente.yml" );
    }
}
