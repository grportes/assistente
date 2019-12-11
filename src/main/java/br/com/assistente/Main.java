package br.com.assistente;

import br.com.assistente.config.ConexaoDB;
import br.com.assistente.infra.javafx.Dialog;
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

        launch( args );
    }

    public static void main2(String[] args) {

        SetupCnxBanco setupCnxBanco = new SetupCnxBanco();
        setupCnxBanco.setId( 1148323176 );
        setupCnxBanco.setDescricao("TESTE 2");
        setupCnxBanco.setDriverCnx("Sybase");
        setupCnxBanco.setUrl( "terra.com.br" );
        setupCnxBanco.setPorta(4100);
        setupCnxBanco.setUserName("grportes");
        setupCnxBanco.setPassword("123456789");

        SetupUsuarioRepository.save( setupCnxBanco );

//        SetupCnxBanco s1 = new SetupCnxBanco();
//        s1.setFornecedorDB(FornecedorDB.SQLITE);
//        s1.setUrl("\\tmp\\teste.db");
//        SetupUsuarioRepository.save(s1);
//
//        SetupCnxBanco s2 = new SetupCnxBanco();
//        s2.setFornecedorDB(FornecedorDB.SQLITE);
//        s2.setUrl("\\tmp\\outro_teste.db");
//        SetupUsuarioRepository.save(s2);
//
//        SetupCnxBanco s3 = new SetupCnxBanco();
//        s3.setFornecedorDB(FornecedorDB.SYBASE);
//        s3.setUrl("terra.com.br");
//        s3.setPorta(9030);
//        s3.setUserName("teste");
//        s3.setPassword("123");
//        SetupUsuarioRepository.save(s3);
//
//        SetupCnxBanco s4 = new SetupCnxBanco();
//        s4.setFornecedorDB(FornecedorDB.SYBASE);
//        s4.setUrl("saturno.com.br");
//        s4.setPorta(9031);
//        s4.setUserName("teste");
//        s4.setPassword("123");
//        SetupUsuarioRepository.save(s4);
//
//        SetupUsuario setupUsuario = new SetupUsuario();
//        setupUsuario.setAutor("gportes");
//        setupUsuario.setLocalProjeto("\\home\\gportes\\develop\\play\\teste");
//        setupUsuario.setIdCnxAtual(123456);
//        SetupUsuarioRepository.save(setupUsuario);
    }


}
