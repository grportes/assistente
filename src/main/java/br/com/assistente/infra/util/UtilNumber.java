package br.com.assistente.infra.util;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.math.NumberUtils.createInteger;

public final class UtilNumber {

    public static Integer toInteger( final String str ) {

        try {
            return isBlank(str) ? null : createInteger( str );
        } catch ( final Exception e ) {
            return null;
        }
    }
}
