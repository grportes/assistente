package br.com.assistente.models;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static br.com.assistente.infra.util.UtilArquivo.getResource;
import static java.nio.file.Files.newInputStream;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.lowerCase;

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

        return load()
            .entrySet()
            .stream()
            .filter( obj -> equalsIgnoreCase( obj.getKey(), coluna ) )
            .map( Map.Entry::getValue )
            .findFirst();
    }

    private static Map<String, String> load() {

        if ( isNotEmpty( cache ) ) return cache;

        final URL resource = getResource("/templates/colunas_id.yml");

        cache = new HashMap<>(  );

        try ( final InputStream in = newInputStream( Paths.get( resource.toURI() ) ) ) {
            final Yaml yaml = new Yaml( );
            final Map<String, List<Map<String,String>>> ids = yaml.load( in );
            final List<Map<String, String>> dados = ids.get( "Ids" );
            cache = dados
                .stream()
                .collect( toMap( x -> lowerCase( x.get( "coluna" ) ), x -> x.get( "atributo" ) ) );
            return cache;
        } catch ( final IOException | URISyntaxException e ) {
            throw new RuntimeException( e );
        }
    }

}
