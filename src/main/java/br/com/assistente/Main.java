package br.com.assistente;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {

        return primaryStage;
    }

    @Override
    public void start( final Stage stage ) throws Exception {

        try {
            primaryStage = stage;

            // DI - Guice:
//            Injector injector = Guice.createInjector( new AgenteModule() );

            final FXMLLoader loader = new FXMLLoader();
//            loader.setControllerFactory(injector::getInstance);
            loader.setLocation( Main.class.getResource("/fxml/AssistenteView.fxml") );
            stage.setTitle("Assistente");
            stage.setScene( new Scene(loader.load()) );
            stage.show();



        } catch (Throwable e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {

        launch( args );
    }

}
