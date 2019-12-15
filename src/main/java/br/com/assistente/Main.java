package br.com.assistente;

import br.com.assistente.config.ConexaoDB;
import br.com.assistente.infra.javafx.Dialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import static br.com.assistente.infra.util.UtilArquivo.getResource;

public class Main extends Application {

    @Override
    public void start( final Stage stage ) {

        try {

            Thread.setDefaultUncaughtExceptionHandler((t, e) -> Platform.runLater(() -> showErrorDialog(t, e)));
            Thread.currentThread().setUncaughtExceptionHandler(this::showErrorDialog);

            final FXMLLoader loader = new FXMLLoader();
            stage.setTitle( "Assistente" );
            loader.setLocation( getResource("/fxml/AssistenteView.fxml") );
            stage.setScene( new Scene(loader.load()) );
            stage.show();

        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

        ConexaoDB.desconnectar();
    }

    private void showErrorDialog(Thread t, Throwable e) {

        Throwable rootCause = ExceptionUtils.getRootCause(e);
        String message = rootCause.getMessage();

        Dialog.msgErro( message );
    }


    public static void main(String[] args) {

        launch( args );
    }

}
