package br.com.assistente.services;

import br.com.assistente.models.DriverCnx;
import br.com.assistente.models.Modelo;
import br.com.assistente.models.ModeloCampo;
import io.vavr.Tuple2;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static br.com.assistente.infra.db.ConnectionFactory.getConnection;
import static br.com.assistente.models.DriverCnx.getQuerySelectTop1;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADERS;

public class MapeamentoService {

    public List<ModeloCampo> extrair( final Modelo modelo )  {

        if ( isNull(modelo) )
            throw new IllegalArgumentException( "É necessário tabela para mapeamento!" );

        if ( isBlank(modelo.getTabela()) )
            throw new IllegalArgumentException( "É necessário tabela para mapeamento!" );

        final Tuple2<Connection, DriverCnx> tuple = getConnection();
        final Connection connection = tuple._1();
        final DriverCnx driverCnx = tuple._2();
        final String query = getQuerySelectTop1( driverCnx, modelo );

        try {

            QueryRunner runner = new QueryRunner();

            ResultSetHandler<Object[]> handler = rs -> {

                if (!rs.next()) return null;

                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();
                Object[] result = new Object[cols];

                for (int i = 0; i < cols; i++) {
                    result[i] = rs.getObject(i + 1);
                }
                return result;
            };

            Object[] result  = runner.query( connection, query, handler );
            System.out.print("Result: " + Arrays.toString(result));

        } catch ( SQLException e ) {
            throw new RuntimeException( format( "Falhou execução de [%s] \n %s", query, e.getMessage() ) );
        }


        return null;
    }

    public void executar( final Modelo modelo ) {

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty( RESOURCE_LOADERS, "classpath" );
        engine.setProperty( "resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        engine.init();

        final Template template = engine.getTemplate("/mapeamento/template.vm");

//        VelocityContext context = new VelocityContext();
//        context.put("nomeAutor", SetupUsuarioRepository.find().map(SetupUsuario::getAutor).orElse("?") );
//        context.put("modelo", modelo);
//        context.put("StringUtils", StringUtils.class);
//
//        try {
//            final StringWriter writer = new StringWriter();
//            template.merge( context, writer );
//            System.out.println(writer.toString());
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
