package br.com.assistente.infra.util;

import static br.com.assistente.infra.util.UtilOS.OSType.OUTROS;

public final class UtilOS {

    public static final OSType SO_CORRENTE;

    static {

        String so = System.getProperty( "os.name" );

        if ( so != null ) {
            so = so.toLowerCase();
            if( so.contains( "mac" ) || so.contains( "darwin" ) )
                SO_CORRENTE = OSType.MAC_OS;
            else if ( so.contains( "win" ) )
                SO_CORRENTE = OSType.WINDOWS;
            else if ( so.contains( "nux" ) )
                SO_CORRENTE = OSType.LINUX;
            else
                SO_CORRENTE = OUTROS;
        } else {
            SO_CORRENTE = OUTROS;
        }
    }

    public enum OSType {
        WINDOWS,
        LINUX,
        MAC_OS,
        OUTROS
    }
}
