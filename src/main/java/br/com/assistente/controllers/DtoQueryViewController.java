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
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

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

        System.out.println("teste");
        fechar();

    }

    private void fechar() {

        final Stage stage = (Stage) rootContainer.getScene().getWindow();
        stage.getUserData();
        stage.close();
    }

    public Set<DefinicaoDto> getIdentities() {

        return Collections.singleton(
            new DefinicaoDto.Builder()
                .comTipo( DefinicaoDto.Tipo.INTEGER )
                .comNomeAtributo( "TESTE" )
                .build()
        );
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

    public static void openViewDtoIdentity(
        final Window windowPai,
        final Set<DefinicaoDto> definicaoDtos,
        final Consumer<Set<DefinicaoDto>> actionExit
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
            stage.setOnCloseRequest(ev -> {
                actionExit.accept( definicaoDtos );
            });
            stage.showAndWait();
        } catch ( final IOException e ) {
            throw new UncheckedIOException(e);
        }
    }

}
