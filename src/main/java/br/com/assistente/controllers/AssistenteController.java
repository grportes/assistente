package br.com.assistente.controllers;

import br.com.assistente.models.Modelo;
import br.com.assistente.models.ModeloCampo;
import br.com.assistente.models.SetupUsuario;
import br.com.assistente.services.MapeamentoService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.HashSet;

import static br.com.assistente.controllers.SetupController.openViewConfiguracoes;
import static br.com.assistente.models.SetupUsuario.getCatalogosCnxSelecionada;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.control.cell.CheckBoxTableCell.forTableColumn;

public class AssistenteController {

    // Services:
    private final MapeamentoService mapeamentoService = new MapeamentoService();

    @FXML private VBox vboxContainer;

    // Mapeamento:
    @FXML private ComboBox<String> cbxMapeamentoBanco;
    @FXML private TextField txtMapeamentoOwner;
    @FXML private TextField txfMapeamentoNomeTabela;
    @FXML private TableView<ModeloCampo> tblMapeamento;
    @FXML private TableColumn<ModeloCampo, Boolean> tcMapeamentoColNull;
    @FXML private TableColumn<ModeloCampo, Boolean> tcMapeamentoID;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoDB;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoJava;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoTipoDB;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoTipoJava;
    @FXML private TableColumn<ModeloCampo, Boolean> tcMapeamentoConverter;
    @FXML private Button btnMapeamento;
    private ObservableList<ModeloCampo> observableModelo = observableArrayList();



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EVENTOS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void onActionConfiguracoes() {

        openViewConfiguracoes(
            vboxContainer.getScene().getWindow(),
            reload -> {
                if ( reload ) {
                    cbxMapeamentoBanco.setItems( observableArrayList( getCatalogosCnxSelecionada() ) );
                }
            });
    }

    @FXML
    public void initialize() {

        initializeMapeamento();
        SetupUsuario.load();
        cbxMapeamentoBanco.setItems( observableArrayList( getCatalogosCnxSelecionada() ) );
//        if ( !SetupUsuarioRepository.isCnxDBInformada() )
//            openViewConfiguracoes( vboxContainer.getScene().getWindow() );

    }

    private void initializeMapeamento() {

        tcMapeamentoColNull.setCellValueFactory( c -> c.getValue().colNullProperty() );
        tcMapeamentoColNull.setCellFactory( forTableColumn(tcMapeamentoColNull) );
        tcMapeamentoID.setCellValueFactory( c -> c.getValue().pkProperty() );
        tcMapeamentoID.setCellFactory( forTableColumn(tcMapeamentoID) );
        tcMapeamentoDB.setCellValueFactory( c -> c.getValue().colunaDBProperty() );
        tcMapeamentoJava.setCellValueFactory( c -> c.getValue().colunaJavaProperty() );
        tcMapeamentoTipoDB.setCellValueFactory( c -> c.getValue().tipoDBProperty() );
        tcMapeamentoTipoJava.setCellValueFactory( c -> c.getValue().tipoJavaProperty() );
        tcMapeamentoConverter.setCellValueFactory( c -> c.getValue().converterProperty() );
        tcMapeamentoConverter.setCellFactory( forTableColumn(tcMapeamentoConverter) );

        tblMapeamento.setItems( observableModelo );
    }

    private void desabilitarAcoesMapeamento( final boolean disable ) {

        cbxMapeamentoBanco.setDisable(disable);
        txtMapeamentoOwner.setDisable(disable);
        txfMapeamentoNomeTabela.setDisable(disable);
        if ( disable ) {
            btnMapeamento.setDisable(false);
        } else {
            cbxMapeamentoBanco.setValue("");
            txtMapeamentoOwner.setText("");
            txfMapeamentoNomeTabela.setText("");
            btnMapeamento.setDisable(true);
        }
    }

    public void onActionMapeamentoBtnLerTabela() {

        mapeamentoService.extrair( new Modelo
            .Builder()
            .comBanco( cbxMapeamentoBanco.getValue() )
            .comOwner( txtMapeamentoOwner.getText() )
            .comTabela( txfMapeamentoNomeTabela.getText() )
            .build()
        );


//        if ( possivelModelo.isSuccess() ) {
//            observableModelo.clear();
//            observableModelo.addAll(possivelModelo.get());
//            desabilitarAcoesMapeamento(true);
//        } else {
//            msgAviso( possivelModelo.getCause().getMessage() );
//        }

    }

    public void onActionMapeamentoBtnLimpar() {

        desabilitarAcoesMapeamento(false);
        observableModelo.clear();
    }

    public void onActionBtnMapeamento() {

        Modelo modelo = new Modelo.Builder()
            .comBanco(cbxMapeamentoBanco.getValue())
            .comOwner(txtMapeamentoOwner.getText())
            .comTabela(txfMapeamentoNomeTabela.getText())
            .comCampos(new HashSet<>(observableModelo))
            .build();

        mapeamentoService.executar( modelo );
    }

}
