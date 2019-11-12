package br.com.assistente.infra.util;

import static org.apache.commons.lang3.StringUtils.isBlank;

public final class UtilString {

    public static String capitalize( final String str ) {

        if ( isBlank(str) ) return str;

        final StringBuilder b = new StringBuilder(str);
        int i = 0;
        do {
            String tmp = b.substring(i,i + 1);
            b.replace( i, i + 1, i == 0 ? tmp : tmp.toUpperCase() );
            i = b.indexOf("_", i) + 1;
        } while (i > 0 && i < b.length());

        return b.toString().replaceAll("_","");
    }

}
