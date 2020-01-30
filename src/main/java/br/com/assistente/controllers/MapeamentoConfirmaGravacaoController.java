package br.com.assistente.controllers;

import io.vavr.Tuple2;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import static br.com.assistente.infra.util.UtilArquivo.getResource;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javafx.application.Platform.runLater;
import static javafx.stage.Modality.WINDOW_MODAL;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MapeamentoConfirmaGravacaoController {

    @FXML private AnchorPane rootContainer;
    @FXML private TextArea txaClasse;
    @FXML private CheckBox cbxRepository;
    @FXML private TextArea txaRepository;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EVENTOS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    public void initialize() {

        runLater(() -> {

            final Stage stage = getStage();
            if ( isNull( stage ) ) return;

            final Tuple2<String, String> msgConfirmacao = (Tuple2<String, String>) stage.getUserData();

            txaClasse.setText( msgConfirmacao._1() );

            final String msgRepository = msgConfirmacao._2();
            if ( isNotBlank( msgRepository ) ) {
                cbxRepository.setDisable( false );
                txaRepository.setText( msgRepository );
            }

            stage.setUserData( false );
        });

    }

    @FXML
    public void onActionView( final ActionEvent event ) {

        if ( isNull(event) || isNull(event.getSource()) ) return;

        final Control source = (Control) event.getSource();

        switch ( source.getId() ) {
            case "cbxRepository":
                break;
            case "btnConfirmar":
                close( true );
                break;
            case "btnCancelar":
                close( false );
                break;
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Stage getStage() {

        final Scene scene = rootContainer.getScene();
        return nonNull( scene ) ? (Stage) scene.getWindow() : null;
    }

    private void close( boolean confirmar ) {

        final Stage stage = getStage();
        assert stage != null;
        stage.setUserData( confirmar ? cbxRepository.isSelected() : null );
        stage.close();
    }

    public static Boolean openViewConfirmarGravacao(
        final Window windowPai,
        final Tuple2<String,String> msgConfirmacao
    ) {

        final String view = "/fxml/MapeamentoConfirmaGravacaoView.fxml";
        try {
            final URL resource = getResource( view );
            final Stage stage = new Stage();
            stage.setScene( new Scene( FXMLLoader.load(resource)) );
            stage.initOwner( windowPai );
            stage.setTitle( "Confirmar" );
            stage.initModality( WINDOW_MODAL );
            stage.setResizable( false );
            stage.setMaximized( false );
            stage.setUserData( msgConfirmacao );
            stage.showAndWait();
            return (Boolean) stage.getUserData();
        } catch ( final IOException e ) {
            e.printStackTrace();
            throw new UncheckedIOException( format( "View: %s ", view ), e );
        }
    }

}
