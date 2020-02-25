package br.com.assistente.models;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static br.com.assistente.infra.util.UtilJar.getInputStream;
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

        return loadCache()
            .entrySet()
            .stream()
            .filter( obj -> equalsIgnoreCase( obj.getKey(), coluna ) )
            .map( Map.Entry::getValue )
            .findFirst();
    }

    private static Map<String, String> loadCache() {

        if ( isNotEmpty( cache ) ) return cache;

        getInputStream("templates/colunas_id.yml", inputStream -> cache = readYaml( inputStream ) );
        return cache;
    }

    private static Map<String, String> readYaml( final InputStream in ) {

        final Yaml yaml = new Yaml( );
        final Map<String, List<Map<String,String>>> ids = yaml.load( in );
        final List<Map<String, String>> dados = ids.get( "Ids" );
        return dados.stream()
                .collect( toMap( x -> lowerCase( x.get( "coluna" ) ), x -> x.get( "atributo" ) ) );
    }

}
