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
import java.util.Objects;
import java.util.Optional;

import static br.com.assistente.infra.javafx.Dialog.*;
import static br.com.assistente.infra.util.UtilArquivo.getResource;
import static br.com.assistente.infra.util.UtilNumber.toInteger;
import static br.com.assistente.infra.util.UtilString.createString;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.stage.Modality.WINDOW_MODAL;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class SetupController {

    // Containers:
    @FXML private Pane pnContainer;
    @FXML private TabPane tpSetup;

    // Definições:
    @FXML private TextField txfAutor;
    @FXML private TextField txfLocalProjeto;
    @FXML private ComboBox<SetupCnxBanco> cbxCnxBanco;

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

        cbxCnxBanco.setItems( observableArrayList( emptyList() ) );
        cbxCnxBancoFornecedor.setItems( observableArrayList( asList(FornecedorDB.values()) ) );

        SetupUsuarioRepository.find().ifPresent( setupUsuario -> {
            txfAutor.setText( setupUsuario.getAutor() );
            txfLocalProjeto.setText( isNull(setupUsuario.getLocalProjeto()) ? "" : setupUsuario.getLocalProjeto() );
            cbxCnxBanco.setItems( observableArrayList( setupUsuario.getConexoesDisponiveis() ) );
            cbxCnxBanco.getItems()
                .stream()
                .filter( s -> Objects.equals(s.getId(), setupUsuario.getIdCnxAtual()) )
                .findFirst()
                .ifPresent( cbxCnxBanco::setValue );
        });
    }

    @FXML
    public void onActionDefinicoes( final ActionEvent event ) {

        if ( isNull(event) || isNull(event.getSource()) ) return;

        final Control source = (Control) event.getSource();

        switch ( source.getId() ) {
            case "btnConfirmar":
                SetupUsuarioRepository.save( getSetupUsuario() );
                msgInfo("Configurações alteradas" );
                break;
            case "btnLocalProjeto":
                selecionarPasta( "Selecione o local do projeto", pnContainer.getScene().getWindow() )
                    .map( File::getAbsolutePath )
                    .ifPresent( file -> txfLocalProjeto.setText( file ) );
                break;
            case "btnEditarCnxBanco":
                final Integer idCnxBanco = getIdCnxBancoSelecionada();
                final Optional<SetupCnxBanco> possivelCnx = SetupUsuarioRepository.findByIdCnxBanco( idCnxBanco );
                if ( possivelCnx.isPresent() ) {
                    objToView( possivelCnx.get() );
                    tpSetup.getSelectionModel().selectLast();
                } else {
                    msgErro( "Falhou edição da conexão! Não localizou dados!!" );
                }
                break;
        }

    }

    @FXML
    public void onActionCnxBanco( final ActionEvent event ) {

        if ( isNull(event) || isNull(event.getSource()) ) return;

        final Control source = (Control) event.getSource();

        switch ( source.getId() ) {
            case "cbxCnxBancoFornecedor":
                ajustarAcesso();
            case "btnCnxBancoNew":
                reset();
                break;
            case "btnCnxBancoSave":
                final SetupCnxBanco setupCnxBancoSave = getSetupCnxBanco();
                int idCnxBancoSave = SetupUsuarioRepository.save( setupCnxBancoSave );
                txfCnxBancoId.setText( createString(idCnxBancoSave) );
                int index = cbxCnxBanco.getItems().lastIndexOf( setupCnxBancoSave );
                if ( index == -1 )
                    cbxCnxBanco.getItems().add( setupCnxBancoSave );
                else
                    cbxCnxBanco.getItems().set( index, setupCnxBancoSave );
                msgInfo( "Conexão salva" );
                break;
            case "btnCnxBancoDelete":
                if ( isBlank(txfCnxBancoId.getText()) ) return;
                final int idCnxBancoDelete = parseInt( txfCnxBancoId.getText() );
                cbxCnxBanco.getItems().removeIf( s -> Objects.equals( s.getId(), idCnxBancoDelete ) );
                SetupUsuarioRepository.deleteCnxById( idCnxBancoDelete );
                reset();
                break;
            case "btnCnxBancoLocalDB":
                selecionarPasta( "Selecione a base de dados SQLIte", pnContainer.getScene().getWindow() )
                    .map( File::getAbsolutePath )
                    .ifPresent( file -> txfCnxBancoUrl.setText( file ) );
                break;
        }
    }



    private void ajustarAcesso() {

        txfCnxBancoPorta.setDisable( !cbxCnxBancoFornecedor.getValue().isPortaCnx() );
        btnCnxBancoLocalDB.setDisable( !cbxCnxBancoFornecedor.getValue().isSelecionarBase() );
        txfCnxBancoUserName.setDisable( !btnCnxBancoLocalDB.isDisable() );
        psCnxBancoSenha.setDisable( !btnCnxBancoLocalDB.isDisable() );
    }

    private SetupUsuario getSetupUsuario() {

        final SetupUsuario setupUsuario = new SetupUsuario();
        setupUsuario.setAutor( txfAutor.getText() );
        setupUsuario.setLocalProjeto( txfLocalProjeto.getText() );
        setupUsuario.setIdCnxAtual( getIdCnxBancoSelecionada() );
        return setupUsuario;
    }

    private Integer getIdCnxBancoSelecionada() {

        final SetupCnxBanco cnx = cbxCnxBanco.getValue();
        return isNull( cnx ) ? null : cnx.getId();
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

    private void objToView( final SetupCnxBanco setup ) {

        cbxCnxBancoFornecedor.getSelectionModel().select( setup.getFornecedorDB() );
        txfCnxBancoUrl.setText( setup.getUrl() );
        txfCnxBancoPorta.setText( createString( setup.getPorta() ) );
        txfCnxBancoUserName.setText( setup.getUserName() );
        psCnxBancoSenha.setText( setup.getPassword() );
        txfCnxBancoId.setText( createString( setup.getId() ) );
    }

    private void reset() {

        txfCnxBancoId.setText("");
        txfCnxBancoUrl.setText("");
        txfCnxBancoPorta.setText("");
        txfCnxBancoUserName.setText("");
        psCnxBancoSenha.setText("");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void openViewConfiguracoes( final Window windowPai ) {

        try {
            final URL resource = getResource("/fxml/SetupView.fxml");

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
