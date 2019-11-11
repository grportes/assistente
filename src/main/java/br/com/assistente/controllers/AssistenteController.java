package br.com.assistente.controllers;

import br.com.assistente.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AssistenteController {

    // UI - Controles
    @FXML private Pane pnRoot;
    @FXML private MenuItem mnMapeamento;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EVENTOS.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void onActionMnMapeamento( final ActionEvent event ) {

        try {
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(AssistenteController.class.getResource("/fxml/MapeamentoView.fxml"))));
            stage.setTitle("Mapeamento");
            stage.initModality(Modality.NONE);
            stage.initOwner( Main.getPrimaryStage().getScene().getWindow() );
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
