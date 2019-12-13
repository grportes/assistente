package br.com.assistente.infra.db;

import br.com.assistente.models.domains.admin.SetupCnxBanco;
import br.com.assistente.models.domains.db.DriverCnx;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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

    public static boolean checkCnx() {

        Connection connectionTemp = null;

        try {
            String db = "jdbc:sqlite:/tmp/play/exemplo.db";
            connectionTemp = DriverManager.getConnection( db );
            QueryRunner runner = new QueryRunner();

            ScalarHandler<Object> resultSet = new ScalarHandler<>();
            runner.query(connectionTemp, "select current_date", resultSet);
            return true;
        } catch (  SQLException e ) {
            return false;
        } finally {
            DbUtils.closeQuietly(connectionTemp);
        }
    }

    public static boolean checkCnx( final SetupCnxBanco cnxBanco ) {

        if ( isNull(cnxBanco) || isNull(cnxBanco.getDriver()) )
            return false;


        Connection connectionTemp = null;

        try {
            final DriverCnx driver = cnxBanco.getDriver();
            String db = format( "%s%s", driver.getProtocolo(), cnxBanco.getEndereco() );
            connectionTemp = DriverManager.getConnection( db );
            QueryRunner runner = new QueryRunner();
            ScalarHandler<Object> resultSet = new ScalarHandler<>();
            runner.query(connectionTemp, driver.getSelectDate(), resultSet);
            return true;
        } catch (  SQLException e ) {
            return false;
        } finally {
            DbUtils.closeQuietly(connectionTemp);
        }
    }
}
