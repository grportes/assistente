package br.com.assistente.infra.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Properties;

public final class UtilVelocity {

    private static final VelocityEngine velocityEngine = new VelocityEngine();

    static {
        try {
            final Properties props = new Properties();
            props.setProperty( "resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader" );
            props.setProperty( "resource.loader.file.path", "src/main/resources/templates" );
            props.setProperty( "resource.loader.file.cache", "true" );

            // Desativar logs do Velocity
            props.setProperty( "runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem" );

            velocityEngine.init( props );
        } catch ( Throwable e ) {
            throw new RuntimeException( "Falha ao inicializar o VelocityEngine", e );
        }
    }

    public static VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public static String exec(
        final VelocityContext context,
        final String arquivoTemplate
    ) {
        final VelocityEngine engine = getVelocityEngine();
        final Template template = engine.getTemplate( arquivoTemplate );
        try ( final StringWriter writer = new StringWriter() ) {
            template.merge( context, writer );
            return writer.toString();
        } catch ( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    public static void gerarArquivo(
        final VelocityContext context,
        final String arquivoTemplate,
        final Path arquivoDestino
    ) {
        final VelocityEngine engine = getVelocityEngine();
        final Template template = engine.getTemplate( arquivoTemplate );

        try ( final Writer writer = new FileWriter( arquivoDestino.toFile() ) ) {
            template.merge( context, writer );
            writer.flush();
        } catch ( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }
}
