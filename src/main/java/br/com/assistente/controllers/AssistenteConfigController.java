package br.com.assistente.controllers;

import br.com.assistente.models.domains.admin.SetupCnxBanco;
import br.com.assistente.models.domains.admin.SetupUsuario;
import br.com.assistente.models.domains.commons.constantes.FornecedorDB;
import br.com.assistente.models.repository.admin.SetupUsuarioRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import static br.com.assistente.infra.javafx.Dialog.msgInfo;
import static br.com.assistente.infra.javafx.Dialog.selecionarPasta;
import static br.com.assistente.infra.util.UtilArquivo.getResource;
import static br.com.assistente.infra.util.UtilNumber.toInteger;
import static br.com.assistente.infra.util.UtilString.createString;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.stage.Modality.WINDOW_MODAL;

public class AssistenteConfigController {

    // Definições:
    @FXML private Pane pnContainer;
    @FXML private TextField txfAutor;
    @FXML private TextField txfLocalProjeto;
    @FXML private ComboBox cbxCnxBanco;

    // Conexao banco:
    @FXML private TextField txfCnxBancoId;
    @FXML private ComboBox<FornecedorDB> cbxCnxBancoFornecedor;
    @FXML private TextField txfCnxBancoUrl;
    @FXML private TextField txfCnxBancoPorta;
    @FXML private TextField txfCnxBancoUserName;
    @FXML private PasswordField psCnxBancoSenha;
    @FXML private Button btnCnxBancoLocalDB;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EVENTOS.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    public void initialize() {

        cbxCnxBancoFornecedor.setItems( observableArrayList( asList(FornecedorDB.values()) ) );

//        SetupUsuarioRepository.find().ifPresent( config -> {
//            txfAutor.setText( config.getAutor() );
//            txfLocalProjeto.setText( isNull(config.getLocalProjeto()) ? "" : config.getLocalProjeto() );
//            cbxCnxBancoFornecedor.setValue( config.getBanco() );
//            txfCnxBancoUrl.setText( config.getUrlConexaoBanco() );
//            txfCnxBancoPorta.setText( String.valueOf(config.getPortaConexaoBanco()) );
//            txfCnxBancoUserName.setText( config.getUserName() );
//            psCnxBancoSenha.setText( config.getPassword() );
//            ajustarAcesso();
//        });
    }

    private void ajustarAcesso() {

        txfCnxBancoPorta.setDisable( !cbxCnxBancoFornecedor.getValue().isPortaCnx() );
        btnCnxBancoLocalDB.setDisable( !cbxCnxBancoFornecedor.getValue().isSelecionarBase() );
        txfCnxBancoUserName.setDisable( !btnCnxBancoLocalDB.isDisable() );
        psCnxBancoSenha.setDisable( !btnCnxBancoLocalDB.isDisable() );
    }

    @FXML
    public void onActionDefinicoes( final ActionEvent event ) {

        if ( isNull(event) || isNull(event.getSource()) ) return;

        final Control source = (Control) event.getSource();

        final SetupUsuario setupUsuario = getSetupUsuario();

        switch ( source.getId() ) {
            case "btnConfirmar":
            case "btnLocalProjeto":
                SetupUsuarioRepository.save( setupUsuario );
                msgInfo("Configurações alteradas" );
                break;
            case "btnEditarCnxBanco":
                break;
        }

    }

    private SetupUsuario getSetupUsuario() {

        final SetupUsuario setupUsuario = new SetupUsuario();
        setupUsuario.setAutor( txfAutor.getText() );
        setupUsuario.setLocalProjeto( txfLocalProjeto.getText() );
//        setupUsuario.setIdCnxAtual( cbxCnxBanco.getValue() );
        return setupUsuario;
    }


    @FXML
    public void onActionCnxBanco( final ActionEvent event ) {

        if ( isNull(event) || isNull(event.getSource()) ) return;

        final Control source = (Control) event.getSource();

        switch ( source.getId() ) {
            case "cbxCnxBancoFornecedor":
                ajustarAcesso();
            case "btnCnxBancoNew":
                txfCnxBancoId.setText("");
                txfCnxBancoUrl.setText("");
                txfCnxBancoPorta.setText("");
                txfCnxBancoUserName.setText("");
                psCnxBancoSenha.setText("");
                break;
            case "btnCnxBancoSave":
                SetupCnxBanco cnx = getSetupCnxBanco();
                SetupUsuarioRepository.save(cnx);
                txfCnxBancoId.setText( createString(cnx.getId()) );
                break;
            case "btnCnxBancoDelete":
                System.out.println("vamos excluir ");
                break;
            case "btnCnxBancoLocalDB":
                selecionarPasta( "Selecione a base de dados SQLIte", pnContainer.getScene().getWindow() )
                    .map( File::getAbsolutePath )
                    .ifPresent( file -> txfCnxBancoUrl.setText( file ) );
                break;
        }
    }

    private SetupCnxBanco getSetupCnxBanco() {

        final SetupCnxBanco cnx = new SetupCnxBanco();
        cnx.setId( toInteger( txfCnxBancoId.getText() ) );
        cnx.setFornecedorDB( cbxCnxBancoFornecedor.getValue() );
        cnx.setUrl( txfCnxBancoUrl.getText() );
        cnx.setPorta( toInteger( txfCnxBancoPorta.getText() ) );
        cnx.setUserName( txfCnxBancoUserName.getText() );
        cnx.setPassword( psCnxBancoSenha.getText() );
        return cnx;
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
            stage.initOwner( windowPai );
            stage.setTitle( "Configurações" );
            stage.initModality( WINDOW_MODAL );
            stage.setResizable( false );
            stage.setMaximized( false );
            stage.show();
        } catch ( final IOException e ) {
            throw new UncheckedIOException(e);
        }
    }
}
