package br.com.assistente;

import br.com.assistente.infra.javafx.Dialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

import static br.com.assistente.infra.db.ConnectionFactory.closeConnection;
import static br.com.assistente.infra.util.UtilArquivo.getResource;

public class Main extends Application {

    private static Logger logger = Logger.getLogger( Main.class );

    @Override
    public void start( final Stage stage ) throws IOException {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Platform.runLater(() -> showErrorDialog(t, e)));
        Thread.currentThread().setUncaughtExceptionHandler(this::showErrorDialog);

        final FXMLLoader loader = new FXMLLoader();
        stage.setTitle( "Assistente" );
        loader.setLocation( getResource("/fxml/AssistenteView.fxml") );
        stage.setScene( new Scene(loader.load()) );
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

        logger.error( "Erro critico:", e );
        Throwable rootCause = ExceptionUtils.getRootCause(e);
        String message = rootCause.getMessage();
        Dialog.msgErro( message );
    }


    public static void main(String[] args) {

        launch( args );
    }
}
