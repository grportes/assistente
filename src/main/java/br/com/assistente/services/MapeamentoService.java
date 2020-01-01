package br.com.assistente.services;

import br.com.assistente.models.DataType;
import br.com.assistente.models.DriverCnx;
import br.com.assistente.models.Modelo;
import br.com.assistente.models.ModeloCampo;
import br.com.assistente.models.SetupCnxBanco;
import br.com.assistente.models.SetupUsuario;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static br.com.assistente.infra.db.ConnectionFactory.getMetaData;
import static br.com.assistente.models.SetupUsuario.buscarCnxAtivaDoUsuario;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADERS;

public class MapeamentoService {

    public Set<ModeloCampo> extrair( final Modelo modelo )  {

        final List<DataType> dataTypes = buscarCnxAtivaDoUsuario()
            .map( SetupCnxBanco::getIdDriver )
            .flatMap( DriverCnx::findById )
            .map( DriverCnx::getDataTypes )
            .orElseThrow( () -> new RuntimeException( "Não foi possivel localizar driver de conexão!!" ) );

        return getMetaData( modelo )
            .stream().map( m -> {
                final String tipoJava = dataTypes
                    .stream()
                    .filter( type -> Objects.equals( m.getTipoDB(), type.getDbType() ) )
                    .findFirst()
                    .map( DataType::getJavaType )
                    .orElseThrow( () -> new RuntimeException( format(
                        "%s - %s: não localizou tipo Java correspondente", m.getColunaDB(), m.getTipoDB()
                    )));
                return new ModeloCampo.Builder( m ).comTipoJava( tipoJava ).build();
            }).collect( toSet() );
    }

    public String executar( final Modelo modelo ) {

        if ( isNull( modelo ) ) return "";

        final VelocityContext context = new VelocityContext();
        context.put( "nomeAutor", SetupUsuario.find().map( SetupUsuario::getAutor ).orElse( "????" ) );
        context.put( "modelo", modelo );
        context.put( "StringUtils", StringUtils.class );

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty( RESOURCE_LOADERS, "classpath" );
        engine.setProperty( "resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        engine.init();

        final Template template = engine.getTemplate("/mapeamento/template.vm");

        try ( final StringWriter writer = new StringWriter() ){
            template.merge( context, writer );
            return writer.toString();
        } catch ( IOException e) {
            throw new UncheckedIOException( e );
        }
    }
}
