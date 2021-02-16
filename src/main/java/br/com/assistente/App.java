package br.com.assistente;

import br.com.assistente.services.VersaoService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import static br.com.assistente.infra.db.ConnectionFactory.closeConnection;
import static br.com.assistente.infra.javafx.Dialog.msgErro;
import static br.com.assistente.infra.util.UtilArquivo.getResource;
import static br.com.assistente.infra.util.UtilJar.getInputStream;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.setDefaultUncaughtExceptionHandler;
import static javafx.application.Platform.runLater;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;

public class App extends Application {

    @Override
    public void start( final Stage stage ) {
        setDefaultUncaughtExceptionHandler( (t, e) -> runLater(() -> showErrorDialog(t, e)) );
        currentThread().setUncaughtExceptionHandler( this::showErrorDialog );

        try {
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation( getResource("/fxml/AssistenteView.fxml") );
            stage.setTitle( format( "Assistente - %s", VersaoService.VERSAO) );
            stage.setScene( new Scene(loader.load()) );
            stage.setOnCloseRequest( e -> Platform.exit() );
            setIconeApp( stage );
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        closeConnection();
    }

    private void setIconeApp( final Stage stage ) {
        try {
            getInputStream(
            "icons/ideia.png",inputStream -> stage.getIcons().add( new Image( inputStream ) )
            );
        } catch ( final Throwable e ) {
            e.printStackTrace();
//            logger.error( e );
        }
    }

    private void showErrorDialog(
        final Thread t,
        final Throwable e
    ) {
        e.printStackTrace();
        final Throwable rootCause = getRootCause(e);
//        if ( !( rootCause instanceof IllegalArgumentException ) ) logger.error( "Erro critico:", e );
        final String message = rootCause.getMessage();
        msgErro( message );
    }

    public static void main(String[] args) {
        launch( args );
    }

}
