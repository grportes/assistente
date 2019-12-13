package br.com.assistente;

import br.com.assistente.config.ConexaoDB;
import br.com.assistente.infra.javafx.Dialog;
import br.com.assistente.infra.db.ConnectionFactory;
import br.com.assistente.models.domains.admin.SetupCnxBanco;
import br.com.assistente.models.repository.admin.SetupUsuarioRepository;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import static br.com.assistente.infra.util.UtilArquivo.getResource;

public class Main extends Application {

    @Override
    public void start( final Stage stage ) throws Exception {

        try {

            Thread.setDefaultUncaughtExceptionHandler((t, e) -> Platform.runLater(() -> showErrorDialog(t, e)));
            Thread.currentThread().setUncaughtExceptionHandler(this::showErrorDialog);

            // DI - Guice:
//            Injector injector = Guice.createInjector( new AgenteModule() );

            final FXMLLoader loader = new FXMLLoader();
//            loader.setControllerFactory(injector::getInstance);
            stage.setTitle( "Assistente" );
            loader.setLocation( getResource("/fxml/AssistenteView.fxml") );
            stage.setScene( new Scene(loader.load()) );
            stage.show();



        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {

        ConexaoDB.desconnectar();
    }

    private void showErrorDialog(Thread t, Throwable e) {

        Throwable rootCause = ExceptionUtils.getRootCause(e);
        String message = rootCause.getMessage();

        Dialog.msgErro( message );
//        Platform.exit();
    }


    public static void main(String[] args) {

//        System.out.println( ConnectionFactory.checkCnx() );

        launch( args );
    }


}
