package br.com.assistente.infra.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class UtilCollections {

    public static Set<String> createSet(
        final String str,
        final Character separador
    ) {

        return isNotBlank( str ) && nonNull(separador)
            ? Arrays.stream( str.split( separador.toString() ) ).collect( toSet() )
            : emptySet();
    }

    public static int getTamanho( final Collection collections ) {

        return nonNull( collections ) ? collections.size() : 0;
    }

}
