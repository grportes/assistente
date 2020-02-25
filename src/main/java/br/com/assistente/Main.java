package br.com.assistente;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

import static br.com.assistente.infra.db.ConnectionFactory.closeConnection;
import static br.com.assistente.infra.javafx.Dialog.msgErro;
import static br.com.assistente.infra.util.UtilArquivo.getResource;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.setDefaultUncaughtExceptionHandler;
import static javafx.application.Platform.runLater;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;

public class Main extends Application {

    private static Logger logger = Logger.getLogger( Main.class );

    @Override
    public void start( final Stage stage ) throws IOException {

        setDefaultUncaughtExceptionHandler( (t, e) -> runLater(() -> showErrorDialog(t, e)) );
        currentThread().setUncaughtExceptionHandler( this::showErrorDialog );

        final FXMLLoader loader = new FXMLLoader();
        stage.setTitle( "Assistente" );
        loader.setLocation( getResource("/fxml/AssistenteView.fxml") );
        stage.setScene( new Scene(loader.load()) );
        stage.setOnCloseRequest( e -> Platform.exit() );
        stage.show();
    }

    @Override
    public void stop() {

        closeConnection();
    }

    private void showErrorDialog(
        final Thread t,
        final Throwable e
    ) {

        final Throwable rootCause = getRootCause(e);
        if ( !( rootCause instanceof IllegalArgumentException ) ) logger.error( "Erro critico:", e );
        final String message = rootCause.getMessage();
        msgErro( message );
    }


    public static void main(String[] args) {

        launch( args );
    }
}
