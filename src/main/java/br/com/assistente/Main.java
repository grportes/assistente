package br.com.assistente;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static br.com.assistente.infra.db.ConnectionFactory.closeConnection;
import static br.com.assistente.infra.javafx.Dialog.msgErro;
import static br.com.assistente.infra.util.UtilArquivo.getResource;
import static br.com.assistente.infra.util.UtilJar.getInputStream;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.setDefaultUncaughtExceptionHandler;
import static javafx.application.Platform.runLater;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;

public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);

    @Override
    public void start( final Stage stage ) throws IOException {
        setDefaultUncaughtExceptionHandler( (t, e) -> runLater(() -> showErrorDialog(t, e)) );
        currentThread().setUncaughtExceptionHandler( this::showErrorDialog );

        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation( getResource("/fxml/AssistenteView.fxml") );
        stage.setTitle( "Assistente - 1.0.5" );
        stage.setScene( new Scene(loader.load()) );
        stage.setOnCloseRequest( e -> Platform.exit() );
        setIconeApp( stage );
        stage.show();
    }

    @Override
    public void stop() {
        closeConnection();
    }

    private void setIconeApp( final Stage stage ) {
        try {
            getInputStream(
                "icons/ideia.png",
                inputStream -> stage.getIcons().add( new Image( inputStream ) )
            );
        } catch ( Throwable e ) {
            logger.error( e );
        }
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
