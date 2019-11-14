package br.com.assistente.infra.javafx;

import javafx.scene.control.Alert;

public final class Dialog {

    private static void msg(
        final String mensagem,
        final Alert.AlertType alertType
    ) {

        final Alert alert = new Alert(alertType);
        alert.setTitle("Assistente");
        alert.setHeaderText("Atenção");
        alert.setContentText( mensagem );
        alert.showAndWait();
    }

    public static void msgInfo( final String mensagem ) {

        msg( mensagem, Alert.AlertType.INFORMATION );
    }

    public static void msgAviso( final String mensagem ) {

        msg( mensagem, Alert.AlertType.WARNING );
    }

    public static void msgErro( final String mensagem ) {

        msg( mensagem, Alert.AlertType.ERROR );
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
