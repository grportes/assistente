package br.com.assistente.controllers;

import br.com.assistente.models.Modelo;
import br.com.assistente.services.ExtrairModeloService;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.StringWriter;

import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADERS;

public class MapeamentoController {

    public void executar() {

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty( RESOURCE_LOADERS, "classpath" );
        engine.setProperty( "resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        engine.init();

        Template template = engine.getTemplate("/mapeamento/template.vm");

        final Modelo modelo = new ExtrairModeloService().extrair().orElseThrow(() -> new RuntimeException(""));

        VelocityContext context = new VelocityContext();
        context.put("nomeAutor", "Não informado");
        context.put("modelo", modelo);
        context.put("StringUtils", StringUtils.class);


        try {
            final StringWriter writer = new StringWriter();
            template.merge( context, writer );
            System.out.println(writer.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
