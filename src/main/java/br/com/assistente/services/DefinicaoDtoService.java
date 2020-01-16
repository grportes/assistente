package br.com.assistente.services;

import br.com.assistente.models.DefinicaoDto;
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

import static br.com.assistente.models.DefinicaoDto.buscarImports;
import static br.com.assistente.models.DefinicaoDto.buscarImportsSerializer;
import static br.com.assistente.models.DefinicaoDto.buscarTodosAtributoId;
import static br.com.assistente.models.DefinicaoDto.orderByPosicao;
import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.singleton;
import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADERS;

public class DefinicaoDtoService {

    public Set<ResultMapeamento> convTexto(
        final String nomeClasse,
        final Set<DefinicaoDto> definicoes,
        boolean gerarJsonAnnotations,
        boolean gerarClasseBuilder
    ) {

        final String nomeAutor = SetupUsuario.find().map(SetupUsuario::getAutor).orElse("????");

        final VelocityContext context = new VelocityContext();
        context.put( "nomeAutor", nomeAutor );
        context.put( "nomeClasse", nomeClasse );
        context.put( "definicoes", orderByPosicao( definicoes ) );
        context.put( "gerarJsonAnnotations", gerarJsonAnnotations );
        context.put( "gerarClasseBuilder", gerarClasseBuilder );
        context.put( "importsNecessarios", buscarImports( definicoes ) );
        context.put( "importsNecessariosSerializer", buscarImportsSerializer( definicoes ) );
        context.put( "identificadores", buscarTodosAtributoId( definicoes ) );
        context.put( "StringUtils", StringUtils.class );
        context.put( "dataHora", now().format( ofPattern( "dd/MM/yyyy" ) ) );

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty( RESOURCE_LOADERS, "classpath" );
        engine.setProperty( "resource.loader.classpath.class", ClasspathResourceLoader.class.getName() );
        engine.init();

        final Template template = engine.getTemplate( "/templates/definicao_dto.vm" );

        try ( final StringWriter writer = new StringWriter() ){
            template.merge( context, writer );
            return singleton( new ResultMapeamento.Builder()
                .comNomeEntidade( nomeClasse )
                .comConteudoEntidade( writer.toString() )
                .build()
            );
        } catch ( IOException e) {
            throw new UncheckedIOException( e );
        }
    }
}
