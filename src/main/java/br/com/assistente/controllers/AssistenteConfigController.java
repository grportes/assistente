package br.com.assistente.controllers;

import br.com.assistente.models.domains.admin.ConfiguracaoDefault;
import br.com.assistente.models.repository.admin.ConfiguracaoDefaultRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Paths;

import static br.com.assistente.infra.javafx.Dialog.msgInfo;
import static br.com.assistente.infra.util.UtilArquivo.getResource;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javafx.collections.FXCollections.observableList;
import static javafx.stage.Modality.WINDOW_MODAL;

public class AssistenteConfigController {

    @FXML private Pane pnContainer;
    @FXML private ComboBox<String> cbxBancoDados;
    @FXML private TextField txfAutor;
    @FXML private TextField txfLocalProjeto;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EVENTOS.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    public void initialize() {

        cbxBancoDados.setItems( observableList( asList("Sybase", "SQLite") ) );
        ConfiguracaoDefaultRepository.find().ifPresent( config -> {
            txfAutor.setText( config.getAutor() );
            txfLocalProjeto.setText( isNull(config.getLocalProjeto()) ? "" : config.getLocalProjeto().toString() );
            cbxBancoDados.setValue( config.getBanco() );
        });

    }

    @FXML
    public void onActionBtnConfirmar() {

        ConfiguracaoDefaultRepository.save(
            new ConfiguracaoDefault.Builder()
                .comAutor( txfAutor.getText() )
                .comBanco( cbxBancoDados.getValue() )
                .comLocalProjeto( Paths.get( txfLocalProjeto.getText() ) )
                .build()
        );

        msgInfo("Configuraçãoes alteradas" );
    }

    @FXML
    public void onActionSelecionarLocalProjeto() {

        final DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle( "Selecione Local do Projeto Play" );
        final File file = dirChooser.showDialog( pnContainer.getScene().getWindow() );
        if ( nonNull(file) )
            txfLocalProjeto.setText( file.getAbsolutePath() );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void openViewConfiguracoes( final Window windowPai ) {

        try {
            final URL resource = getResource( "/fxml/AssistenteConfigView.fxml");

            final Stage stage = new Stage();
            stage.setScene( new Scene(FXMLLoader.load(resource)) );
            stage.setTitle( "Configurações" );
            stage.initModality( WINDOW_MODAL );
            stage.initOwner( windowPai );
            stage.show();
        } catch ( final IOException e ) {
            throw new UncheckedIOException(e);
        }
    }


}
