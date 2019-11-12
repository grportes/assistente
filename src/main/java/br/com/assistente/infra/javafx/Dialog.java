package br.com.assistente.infra.javafx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public final class Dialog {

    public static void msgAviso( final String mensagem ) {

        final Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Assistente");
        alert.setHeaderText("Atenção");
        alert.setContentText( mensagem );
        alert.showAndWait();
    }


//    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//            alert.setTitle("Confirmation Dialog");
//            alert.setHeaderText("Look, a Confirmation Dialog");
//            alert.setContentText("Are you ok with this?");
//
//    Optional<ButtonType> result = alert.showAndWait();
//            if (result.get() == ButtonType.OK){
////                Animal currentAnimal = (Animal) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
////                data.remove(currentAnimal);
//    }

}
