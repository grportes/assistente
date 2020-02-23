package br.com.assistente.models;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.startsWith;

public final class ColunaId {


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTANTES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static Map<String, String> cache;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Optional<String> getNomeAtributo( final String coluna ) {

        return loadCache()
            .entrySet()
            .stream()
            .filter( obj -> equalsIgnoreCase( obj.getKey(), coluna ) )
            .map( Map.Entry::getValue )
            .findFirst();
    }

    private static Map<String, String> loadCache() {

        if ( isNotEmpty( cache ) ) return cache;
        final URL resource = ColunaId.class.getResource( "" );
        cache = startsWith( resource.getFile(), "http:" )
            ? loadCacheFromJar( resource )
            : loadCacheFromFile( );
        return cache;
    }


    private static Map<String, String> loadCacheFromJar( final URL url ) {

        try {
            final JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            final JarFile jarFile = jarURLConnection.getJarFile();
            final Enumeration<JarEntry> entries = jarFile.entries();
            while ( entries.hasMoreElements() ) {
                final JarEntry jarEntry = entries.nextElement();
                final String nomeArquivo = jarEntry.getName();
                final boolean arquivoYml = startsWith( nomeArquivo, "templates/" )
                        && endsWithIgnoreCase( nomeArquivo, "colunas_id.yml" );
                if ( arquivoYml ) {
                    final JarEntry fileEntry = jarFile.getJarEntry( nomeArquivo );
                    try ( final BufferedInputStream bis = new BufferedInputStream( jarFile.getInputStream( fileEntry ) ) ) {
                        return readYaml( bis );
                    }
                }
            }
            throw new RuntimeException( "Não localizou arquivo colunas_id.yml!" );
        } catch ( final IOException e ) {
            throw new RuntimeException( e );
        }
    }

    private static Map<String, String> loadCacheFromFile( ) {

        final ClassLoader classLoader = DriverCnx.class.getClassLoader();
        final URL drivers =  requireNonNull(
            classLoader.getResource( "templates/colunas_id.yml" ),
            "Pasta drivers não localizada!"
        );

        try ( final InputStream in = drivers.openStream() )  {
            return readYaml( in );
        } catch ( final IOException e ) {
            throw new RuntimeException( e );
        }
    }

    private static Map<String, String> readYaml( final InputStream in ) {

        final Yaml yaml = new Yaml( );
        final Map<String, List<Map<String,String>>> ids = yaml.load( in );
        final List<Map<String, String>> dados = ids.get( "Ids" );
        return dados.stream()
                .collect( toMap( x -> lowerCase( x.get( "coluna" ) ), x -> x.get( "atributo" ) ) );
    }

}
