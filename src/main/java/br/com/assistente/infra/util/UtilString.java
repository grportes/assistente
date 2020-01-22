package br.com.assistente.infra.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.lang.String.join;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isAllUpperCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.text.CaseUtils.toCamelCase;

public final class UtilString {

    public static <T extends Number> String createString( final T value ) {

        return nonNull(value) ?  value.toString() : null;
    }

    public static String createString(
        final Set<String> lista,
        final Character separador
    ) {

        return isNotEmpty( lista ) && nonNull(separador)
                ? join( separador.toString(), lista )
                : "";
    }

    public static String removerEspacosEntre( final String str ) {

        return isNotBlank( str ) ? str.replaceAll("\\s+","") : str;
    }

    public static String convPluralToSingular( final String substantivo ) {

        if ( isEmpty( substantivo ) ) return substantivo;
        int tamanho = substantivo.length();
        if ( tamanho == 1 ) return substantivo;

        if ( tamanho > 2 ) {
            final String novaString = substantivo.substring( 0, tamanho - 2 );
            final String tmp = substantivo.substring( tamanho - 2 );
            switch ( tmp ) {
                case "is":
                    return novaString + "il";
                case "ns":
                    return novaString + "m";
            }
        }

        if ( tamanho > 3 ) {
            final String novaString = substantivo.substring( 0, tamanho - 3 );
            final String tmp = substantivo.substring( tamanho - 3 );
            switch ( tmp ) {
                case "ais":
                    return novaString + "al";
                case "eis":
                    return novaString + "el";
                case "ois":
                    return novaString + "ol";
                case "uis":
                    return novaString + "ul";
                case "ões":
                    return novaString + "ão";
                case "oes":
                    return novaString + "ao";
                case "ses":
                    return novaString + "s";
            }
        }

        if ( tamanho > 4 ) {
            final String tmp = substantivo.substring( tamanho - 4 );
            if ( tmp.equalsIgnoreCase( "eses" ))
                return substantivo.substring( 0, tamanho - 4 ) + "es";
        }

        if ( substantivo.substring( tamanho - 1 ).equalsIgnoreCase( "s" ) )
            return substantivo.substring( 0, tamanho -1 );

        return substantivo;
    }

    public static String convCamelCase(
        final String str,
        final boolean capitalizeFirst
    ) {

        if ( isEmpty( str ) ) return str;
        String newStr = str.trim();
        if ( newStr.contains( " " ) ) newStr = newStr.replaceAll( " ", "_" );
        if ( newStr.contains( "_" ) ) {
            newStr = Arrays.stream( newStr.split( "_" ) )
                .map( String::toLowerCase )
                .map( UtilString::convPluralToSingular )
                .collect( joining( "_" ) );
        } else if ( Objects.equals( newStr, str.trim() ) && !isAllUpperCase( newStr ) ) {
            char firstLetter = newStr.charAt( 0 );
            return ( !capitalizeFirst && isUpperCase( firstLetter ) )
                ? StringUtils.join( toLowerCase( firstLetter ), newStr.substring( 1 ) )
                : StringUtils.capitalize( newStr );
        }
        return toCamelCase( newStr, capitalizeFirst, '_' );
    }

    public static String requireNotBlank( final String str ) {

        return requireNotBlank( str, "String vazia não permitida" );
    }

    public static String requireNotBlank(
        final String str,
        final String message
    ) {

        if ( isBlank( str ) )
            throw new NullPointerException( message );

        return str;
    }



}
