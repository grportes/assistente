package br.com.assistente.services;

import br.com.assistente.models.Constante;
import br.com.assistente.models.ResultMapeamento;
import br.com.assistente.models.SetupUsuario;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADERS;

public class ConstanteService {

    public Set<ResultMapeamento> convTexto(
        final String nomeEnum,
        final Constante.Tipo tipo,
        final Set<Constante> constantes
    ) {

        final String nomeAutor = SetupUsuario.find().map(SetupUsuario::getAutor).orElse("????");

        final VelocityContext context = new VelocityContext();
        context.put( "nomeAutor", nomeAutor );
        context.put( "nomeEnum", nomeEnum );
        context.put( "constantes", constantes );
        context.put( "tipoJava", tipo );
//        context.put( "campos", orderByPosicao( campos ) );
//        context.put( "importsNecessarios", buscarImports( campos ) );
        context.put( "StringUtils", StringUtils.class );

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty( RESOURCE_LOADERS, "classpath" );
        engine.setProperty( "resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        engine.init();

        try ( final StringWriter writer = new StringWriter() ) {
            final Template template = engine.getTemplate( "/templates/constante.vm" );
            template.merge( context, writer );
            return singleton(
                new ResultMapeamento.Builder()
                    .comNomeEntidade( "" )
                    .comConteudoEntidade( writer.toString() )
                    .build()
            );
        } catch ( IOException e) {
            throw new UncheckedIOException( e );
        }
    }
}
