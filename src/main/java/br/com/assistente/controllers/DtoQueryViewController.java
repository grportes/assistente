package br.com.assistente.controllers;

import br.com.assistente.models.DefinicaoDto;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static br.com.assistente.infra.util.UtilArquivo.getResource;
import static java.util.Objects.nonNull;
import static javafx.application.Platform.runLater;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.control.cell.CheckBoxTableCell.forTableColumn;
import static javafx.stage.Modality.WINDOW_MODAL;

public class DtoQueryViewController  {

    @FXML private AnchorPane rootContainer;
    @FXML private TableView<DefinicaoDto> tbvDto;
    @FXML private TableColumn<DefinicaoDto, String> tcDtoNomeAtributo;
    @FXML private TableColumn<DefinicaoDto, Boolean> tcDtoAtributoId;
    private ObservableList<DefinicaoDto> definicaoDtos;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EVENTOS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    public void initialize() {

        tcDtoNomeAtributo.setCellValueFactory( c -> c.getValue().nomeAtributoProperty() );
        tcDtoAtributoId.setCellValueFactory( c -> c.getValue().atributoIdProperty() );
        tcDtoAtributoId.setCellFactory( forTableColumn(tcDtoAtributoId) );
        definicaoDtos = observableArrayList();

        runLater(() -> {

            final Stage stage = getStage();
            if ( nonNull(stage) ) {
                final Set<DefinicaoDto> dados = (Set<DefinicaoDto>) stage.getUserData();
                definicaoDtos.addAll( dados );
            }
            tbvDto.setItems( definicaoDtos );
        });
    }


    @FXML
    public void onActionBtnConfirmar( ) {

        final Stage stage = getStage();
        assert stage != null;
        stage.setUserData( definicaoDtos );
        stage.close();
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

    public static Set<DefinicaoDto> openViewDtoIdentity(
        final Window windowPai,
        final Set<DefinicaoDto> definicaoDtos
    ) {

        try {
            final URL resource = getResource("/fxml/DtoQueryView.fxml");

            final Stage stage = new Stage();
            stage.setScene( new Scene(FXMLLoader.load(resource)) );
            stage.initOwner( windowPai );
            stage.setTitle( "Informe ID" );
            stage.initModality( WINDOW_MODAL );
            stage.setResizable( false );
            stage.setMaximized( false );
            stage.setUserData( definicaoDtos );
            stage.showAndWait();
            return new HashSet<>((ObservableList<DefinicaoDto>) stage.getUserData());
        } catch ( final IOException e ) {
            throw new UncheckedIOException(e);
        }
    }

}
