package br.com.assistente.controllers;

import br.com.assistente.models.DefinicaoDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import static br.com.assistente.infra.util.UtilArquivo.getResource;
import static javafx.stage.Modality.WINDOW_MODAL;

public class DtoQueryViewController {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EVENTOS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void onActionDto( final ActionEvent event ) {


    }

    public Set<DefinicaoDto> getIdentities() {

        return Collections.singleton(  new DefinicaoDto.Builder().comTipo( DefinicaoDto.Tipo.INTEGER ).comNomeAtributo( "TESTE" ).build() );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static DtoQueryViewController openViewDtoIdentity( final Window windowPai ) {

        try {
            final URL resource = getResource("/fxml/DtoQueryView.fxml");

            final Stage stage = new Stage();
            stage.setScene( new Scene(FXMLLoader.load(resource)) );
            stage.initOwner( windowPai );
            stage.setTitle( "Informe ID" );
            stage.initModality( WINDOW_MODAL );
            stage.setResizable( false );
            stage.setMaximized( false );
            stage.showAndWait();




            return null;
        } catch ( final IOException e ) {
            throw new UncheckedIOException(e);
        }
    }

}
