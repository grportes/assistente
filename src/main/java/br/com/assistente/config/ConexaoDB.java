package br.com.assistente.config;

import br.com.assistente.infra.util.UtilYaml;
import br.com.assistente.models.domains.admin.SetupCnxBanco;
import br.com.assistente.models.domains.db.DriverCnx;
import org.apache.commons.dbutils.DbUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.assistente.infra.util.UtilArquivo.getResource;
import static br.com.assistente.infra.util.UtilYaml.load;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.dbutils.DbUtils.loadDriver;

public final class ConexaoDB {

    private static Connection connection;

    public static Connection getConnection() {

        return connection;
    }

    public static final void conectar() {

        if ( nonNull(connection) ) return;

        loadDriver("org.sqlite.JDBC");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:/tmp/play/exemplo.db");
        } catch ( final SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    public static final void desconnectar() {

        if ( nonNull(connection) ) {
            try {
                DbUtils.close( connection );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void checkCnx( final SetupCnxBanco setupCnxBanco ) {

        requireNonNull( setupCnxBanco, "Conexão inválida" );

        final DriverCnx driverCnx = DriverCnx
            .findById( setupCnxBanco.getDriverCnx() )
            .orElseThrow(() -> new RuntimeException(""));

        if ( !loadDriver( driverCnx.getDriver() ) ) {
            throw new RuntimeException( format( "Falhou conexão em [%s]", driverCnx.getDriver() ) );
        }

        try {
            String jdbc = driverCnx.getUrl() + setupCnxBanco.getUrl();
            connection = DriverManager.getConnection( jdbc );
        } catch ( final SQLException e ) {
            throw new RuntimeException(e);
        }
    }




/*
    public static void execQuery(
            final TabelaMapeamento tabelaMapeamento,
            final Consumer<Object[]> block
    ) {

        try {
            ConexaoDB.conectar();

            List<String> primaryKeys = getPrimaryKeys( tabelaMapeamento );

            ResultSetHandler<Object[]> handler = rs -> {

                if (!rs.next())
                    return null;

                final ResultSetMetaData meta = rs.getMetaData();

                int cols = meta.getColumnCount();
                Object[] result = new Object[cols];

                for (int i = 0; i < cols; i++) {
                    int posicao = i + 1;
                    final JDBC4ResultSet jdbcRs = (JDBC4ResultSet) rs;
                    final String columnName = jdbcRs.getColumnName(posicao);
                    final int columnType = jdbcRs.getColumnType(posicao);
                    boolean b = jdbcRs.wasNull();
                }
                return result;
            };

            QueryRunner runner = new QueryRunner();
            Object[] result  = runner.query(ConexaoDB.getConnection(), tabelaMapeamento.getSelectTop1(), handler );
            block.accept( result );

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConexaoDB.desconnectar();
        }
    }

    private static List<String> getPrimaryKeys( final TabelaMapeamento tabela ) throws SQLException {

        List<String> pks = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getPrimaryKeys(tabela.getCatalogo(), tabela.getSchema(), tabela.getTableName() );
        while ( rs.next() ) pks.add( rs.getString(4) );
        return pks;
    }
*/


}
