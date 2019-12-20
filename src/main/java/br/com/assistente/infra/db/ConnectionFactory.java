package br.com.assistente.infra.db;

import br.com.assistente.models.DriverCnx;
import br.com.assistente.models.SetupCnxBanco;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static br.com.assistente.infra.util.UtilCrypto.descriptografar;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class ConnectionFactory {

    private static Connection connection;

    public static Connection getConnection() {

        if ( isNull(connection) ) {
            try {
                connection = DriverManager.getConnection("");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() {

        if ( nonNull(connection) ) DbUtils.closeQuietly(connection);
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
            if ( nonNull(connectionTemp) ) DbUtils.closeQuietly( connectionTemp );
        }
    }
}
