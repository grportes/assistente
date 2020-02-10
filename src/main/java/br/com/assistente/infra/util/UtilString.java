package br.com.assistente.infra.util;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Set;

import static java.lang.String.join;
import static java.text.Normalizer.Form.NFD;
import static java.text.Normalizer.normalize;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isAllUpperCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.apache.commons.lang3.StringUtils.replaceIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trim;


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

    public static String removerAcentosECaracteresEspeciais( final String str ) {

        return isNotBlank( str )
            ? normalize( str, NFD ).replaceAll("[^\\p{ASCII}]", "" )
            : str;
    }

    public static String replaceAll(
        final String str,
        final String replacement,
        final String... searchString
    ) {

        if ( isBlank(str) || ArrayUtils.isEmpty( searchString ) ) return str;

        String tmp = str;
        for ( final String search : searchString ) tmp = replaceIgnoreCase( tmp, search, replacement );

        return tmp;
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

    public static String normalizeJava(
        final String str,
        final boolean capitalizeFirst
    ) {
        if ( isBlank( str ) ) return str;

        String newStr = removerAcentosECaracteresEspeciais( normalizeSpace( trim( str ) ) );
        if ( newStr.contains("_") ) newStr = newStr.replaceAll( "_", " " );

        final String[] sArray = newStr.split( " " );
        newStr = isAllUpperCase( sArray[0] ) ? lowerCase( sArray[0] ) : sArray[0];
        for ( int index = 1; index < sArray.length; index++ ) {
            final String tmp = isAllUpperCase( sArray[index] ) ? lowerCase( sArray[index] ) : sArray[index];
            newStr = newStr.concat( capitalize( tmp ) );
        }

        newStr = convPluralToSingular( newStr );

        return capitalizeFirst ? capitalize( newStr ) : newStr;
    }

    public static String convDbToJava( final String str ) {

        if ( isBlank( str ) ) return str;

        final String[] tmp = normalizeJava( str.toLowerCase(), false ).split( "_" );
        String newStr = tmp[0];
        for ( int index = 1; index < tmp.length; index++ )
            newStr = newStr.concat( capitalize( tmp[index] ) );
        return newStr;
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
