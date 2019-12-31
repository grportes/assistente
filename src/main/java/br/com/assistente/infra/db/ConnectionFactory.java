package br.com.assistente.infra.db;

import br.com.assistente.models.DriverCnx;
import br.com.assistente.models.Modelo;
import br.com.assistente.models.ModeloCampo;
import br.com.assistente.models.SetupCnxBanco;
import br.com.assistente.models.SetupUsuario;
import io.vavr.Tuple2;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static br.com.assistente.infra.util.UtilCrypto.descriptografar;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.dbutils.DbUtils.closeQuietly;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class ConnectionFactory {

    private static Connection connection;
    private static DriverCnx driverCnx;

    public static Tuple2<Connection, DriverCnx> getConnection() {

        if ( isNull( connection ) ) {
            final SetupCnxBanco cnxBanco = SetupUsuario.buscarCnxAtivaDoUsuario()
                    .orElseThrow( () -> new RuntimeException("Não localizou conexão ativa") );

            driverCnx = DriverCnx.findById( cnxBanco.getIdDriver() )
                    .orElseThrow(() -> new RuntimeException( "Não localizou driver de conexão") );

            final String jdbcUrl = format( "%s%s", driverCnx.getProtocolo(), cnxBanco.getHost() )
                    .concat( nonNull( cnxBanco.getPorta() ) ? ":" + cnxBanco.getPorta() : ""  );

            try {
                connection = isNotBlank( cnxBanco.getUserName() )
                        ? DriverManager.getConnection( jdbcUrl, cnxBanco.getUserName(), descriptografar(cnxBanco.getPassword()) )
                        : DriverManager.getConnection( jdbcUrl );
            } catch ( final SQLException e ) {
                e.printStackTrace();
                throw new RuntimeException( format(
                    "Falhou conexão com %s \n%s", cnxBanco.getIdDriver(), e.getMessage()
                ));
            }
        }

        return new Tuple2<>( connection, driverCnx );
    }

    public static void closeConnection() {

        if ( nonNull(connection) ) closeQuietly( connection );
    }

    public static boolean checkCnx( final SetupCnxBanco cnxBanco ) {

        if ( isNull(cnxBanco) || isNull(cnxBanco.getIdDriver()) ) return false;

        final DriverCnx driverCnx = DriverCnx.findById(cnxBanco.getIdDriver())
                .orElseThrow(() -> new RuntimeException("Não localizou driver de conexão"));

        Connection connectionTemp = null;

        try {
            String db = format( "%s%s", driverCnx.getProtocolo(), cnxBanco.getHost() );
            if ( nonNull( cnxBanco.getPorta() ) ) db = format( "%s:%s", db, cnxBanco.getPorta() );
            connectionTemp = isNotBlank( cnxBanco.getUserName() )
                ? DriverManager.getConnection( db, cnxBanco.getUserName(), descriptografar(cnxBanco.getPassword()) )
                : DriverManager.getConnection( db );
            QueryRunner runner = new QueryRunner();
            ScalarHandler<Object> resultSet = new ScalarHandler<>();
            runner.query(connectionTemp, driverCnx.getSelectDate(), resultSet);
            return true;
        } catch ( SQLException e ) {
            return false;
        } finally {
            if ( nonNull(connectionTemp) ) closeQuietly( connectionTemp );
        }
    }

    public static Set<ModeloCampo> getMetaData( final Modelo modelo )  {

        if ( isNull(modelo) )
            throw new IllegalArgumentException( "Modelo de dados não informado!!" );

        if ( isBlank(modelo.getTabela()) )
            throw new IllegalArgumentException( "Tabela não informada!" );

        final Tuple2<Connection, DriverCnx> tuple = getConnection();
        final Connection con = tuple._1();

        try {
            final DatabaseMetaData metaData = con.getMetaData();
            final Set<String> pks = new LinkedHashSet<>();
            final Set<ModeloCampo> campos = new LinkedHashSet<>();

            // Pks;
            ResultSet rs = metaData.getPrimaryKeys( null, null, modelo.getTabela() );
            while ( rs.next() ) pks.add( rs.getString("COLUMN_NAME" ) );

            // Colunas:
            rs = metaData.getColumns(null, null, modelo.getTabela(), null );
            while ( rs.next() ) {
                final String columnName = rs.getString( "COLUMN_NAME" );
                campos.add(
                    new ModeloCampo.Builder()
                        .comColunaDB( columnName )
                        .comPK( pks.contains( columnName ) )
                        .comColNull( Objects.equals( rs.getString( "NULLABLE" ), "1" ) )
                        .comTipoDB( rs.getString( "TYPE_NAME" ) )
                        .build()
                );
            }

            return campos;

        } catch ( final SQLException e ) {
            e.printStackTrace();
            throw new RuntimeException( e.getMessage() );
        }
    }

}
