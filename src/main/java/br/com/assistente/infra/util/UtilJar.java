package br.com.assistente.infra.util;

import br.com.assistente.Main;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

public class UtilJar {

    private static final URL url = UtilJar.class.getResource( "" );

    public static void getInputStream(
        final String resourceFile,
        final Consumer<InputStream> exec
    ) {

        if ( !startsWith( url.getFile(), "http:" ) ) return;

        try {
            final JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            final JarFile jarFile = jarURLConnection.getJarFile();
            final Enumeration<JarEntry> entries = jarFile.entries();
            while ( entries.hasMoreElements() ) {
                final JarEntry jarEntry = entries.nextElement();
                final String nomeArquivo = jarEntry.getName();
                if ( startsWithIgnoreCase( nomeArquivo, resourceFile ) ) {
                    final JarEntry fileEntry = jarFile.getJarEntry( nomeArquivo );
                    try ( final InputStream inputStream = jarFile.getInputStream( fileEntry ) ) {
                        exec.accept( inputStream );
                    }
                }
            }
        } catch ( final IOException e ) {
            throw new RuntimeException( e );
        }

    }


}
