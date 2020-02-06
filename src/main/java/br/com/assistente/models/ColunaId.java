package br.com.assistente.models;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static br.com.assistente.infra.util.UtilString.requireNotBlank;
import static java.lang.String.format;
import static java.nio.file.Files.newInputStream;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.substringBetween;

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

        final URL resource = ColunaId.class.getResource( "/templates" );

        cache = StringUtils.equalsIgnoreCase( resource.getProtocol(), "jar" )
            ? loadCacheFromJar( resource )
            : loadCacheFromFile( resource );

        return cache;
    }


    private static Map<String, String> loadCacheFromJar( final URL resource ) {

        final String jarFile = requireNotBlank(
            substringBetween( resource.getFile(), "file:", ".jar!" ),
            "Arquivo jar não localizado!"
        ).concat( ".jar" );

        try ( final ZipFile zipFile = new ZipFile( jarFile ) ) {
            final Enumeration<? extends ZipEntry> e = zipFile.entries();
            while ( e.hasMoreElements() ) {
                final ZipEntry entry = e.nextElement();
                if ( equalsIgnoreCase( entry.getName(), "templates/colunas_id.yml" )  ) {
                    try ( final BufferedInputStream in = new BufferedInputStream( zipFile.getInputStream(entry) ) ) {
                        return readYaml( in );
                    }
                }
            }
            throw new RuntimeException( "Não localizou arquivo templates/colunas_id.yml" );
        } catch ( final IOException e) {
            throw new UncheckedIOException( format( "Falha ao ler arquivo %s", jarFile ), e );
        }
    }

    private static Map<String, String> loadCacheFromFile( final URL resource ) {

        try ( final InputStream in = newInputStream( Paths.get( resource.toURI() ).resolve( "colunas_id.yml" ) ) ) {
            return readYaml( in );
        } catch ( final IOException | URISyntaxException e ) {
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
