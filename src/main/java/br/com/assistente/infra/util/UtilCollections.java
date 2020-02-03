package br.com.assistente.infra.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
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

    public static <T> Collection<T> requireNotEmpty(
        final Collection<T> lista,
        final String mensagem
    ) {

        if ( isEmpty( lista ) )
            throw new IllegalArgumentException( isNotBlank( mensagem ) ? mensagem : "Lista vazia!!" );

        return lista;
    }

}
