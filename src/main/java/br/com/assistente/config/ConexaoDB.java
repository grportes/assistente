package br.com.assistente.config;

import org.apache.commons.dbutils.DbUtils;

import java.sql.*;

import static br.com.assistente.infra.util.UtilYaml.load;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
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
