package br.com.assistente.infra.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;

import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADERS;

public final class UtilVelocity {

    public static String exec(
        final VelocityContext context,
        final String arquivoTemplate
    ) {

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty( RESOURCE_LOADERS, "classpath" );
        engine.setProperty( "resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        engine.init();

        final Template template = engine.getTemplate( arquivoTemplate );

        try ( final StringWriter writer = new StringWriter() ) {
            template.merge( context, writer );
            return writer.toString();
        } catch ( IOException e) {
            throw new UncheckedIOException( e );
        }

    }
}
