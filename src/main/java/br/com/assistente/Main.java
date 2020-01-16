package br.com.assistente;

import br.com.assistente.infra.javafx.Dialog;
import br.com.assistente.models.Constante;
import br.com.assistente.models.DefinicaoDto;
import br.com.assistente.models.ResultMapeamento;
import br.com.assistente.services.ConstanteService;
import br.com.assistente.services.DefinicaoDtoService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static br.com.assistente.infra.db.ConnectionFactory.closeConnection;
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

        closeConnection();
    }

    private void showErrorDialog(
        final Thread t,
        final Throwable e
    ) {

        Throwable rootCause = ExceptionUtils.getRootCause(e);
        String message = rootCause.getMessage();
        Dialog.msgErro( message );
    }


    public static void main(String[] args) {

        launch( args );
//        testar();
    }

    private static void testar() {

        Set<DefinicaoDto> dtos = new LinkedHashSet<>(  );
        dtos.add( new DefinicaoDto.Builder().comTipo( DefinicaoDto.Tipo.INTEGER ).comNomeAtributo( "id_cliente" ).comAtributoId( true ).build() );
        dtos.add( new DefinicaoDto.Builder().comTipo( DefinicaoDto.Tipo.SHORT ).comNomeAtributo( "id empresa" ).comAtributoId( true ).build() );
        dtos.add( new DefinicaoDto.Builder().comTipo( DefinicaoDto.Tipo.STRING ).comNomeAtributo( "RazaoSocial" ).comAtributoId( false ).build() );
        dtos.add( new DefinicaoDto.Builder().comTipo( DefinicaoDto.Tipo.LOCAL_DATE ).comNomeAtributo( "implantacao" ).comAtributoId( false ).build() );
        dtos.add( new DefinicaoDto.Builder().comTipo( DefinicaoDto.Tipo.LOCAL_DATETIME ).comNomeAtributo( "alteracao" ).comAtributoId( false ).build() );
        dtos.add( new DefinicaoDto.Builder().comTipo( DefinicaoDto.Tipo.BIGDECIMAL ).comNomeAtributo( "vlrLimiteCredito" ).comAtributoId( false ).build() );
        DefinicaoDtoService d = new DefinicaoDtoService();
        Set<ResultMapeamento> results = d.convTexto( "ClienteDto", dtos, true, true );
        for ( ResultMapeamento result : results ) {
            System.out.println(result.getConteudoEntidade());
            System.out.println("\n\n");
        }


    }

}
