package br.com.assistente.infra.javafx;

import io.vavr.Tuple2;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public final class Dialog {

    private static void msg(
        final String mensagem,
        final Alert.AlertType alertType
    ) {

        final Alert alert = new Alert(alertType);
        alert.setTitle( "Assistente" );
        alert.setHeaderText( null );
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

    public static Optional<File> selecionarPasta(
        final String titulo,
        final Window windowPai
    ) {

        final DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle( titulo );
        return ofNullable( dirChooser.showDialog(windowPai) );
    }

    public static Optional<File> selecionarArquivo(
        final String titulo,
        final Window windowPai
    ) {

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( titulo );
        return ofNullable( fileChooser.showOpenDialog(windowPai) );
    }

    public static Optional<File> selecionarArquivo(
        final String titulo,
        final Window windowPai,
        final Tuple2<String, String>... extensoes
    ) {

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( titulo );

        final List<FileChooser.ExtensionFilter> filters = Arrays
                .stream( extensoes )
                .map( t -> new FileChooser.ExtensionFilter( t._1(), t._2() ) )
                .collect( toList() );

        fileChooser.getExtensionFilters().addAll( filters );
        return ofNullable( fileChooser.showOpenDialog(windowPai) );
    }

}
