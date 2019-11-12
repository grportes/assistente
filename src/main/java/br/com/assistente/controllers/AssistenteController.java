package br.com.assistente.controllers;

import br.com.assistente.models.mapeamento.Modelo;
import br.com.assistente.models.mapeamento.ModeloCampo;
import br.com.assistente.services.MapeamentoService;
import io.vavr.control.Try;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.HashSet;

import static br.com.assistente.infra.javafx.Dialog.msgAviso;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableList;

public class AssistenteController {

    // Services:
    private final MapeamentoService mapeamentoService = new MapeamentoService();

    // Mapeamento:
    @FXML private ComboBox<String> cbxMapeamentoBanco;
    @FXML private TextField txtMapeamentoOwner;
    @FXML private TextField txfMapeamentoNomeTabela;
    @FXML private TableView<ModeloCampo> tblMapeamento;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoDB;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoJava;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoTipoDB;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoTipoJava;
    @FXML private TableColumn<ModeloCampo, Boolean> tcMapeamentoColNull;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoConverter;
    private ObservableList<ModeloCampo> observableModelo = observableArrayList();


    @FXML private Button btnMapeamento;



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EVENTOS.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    public void initialize() {

        initializeMapeamento();
    }

    private void initializeMapeamento() {

        cbxMapeamentoBanco.setItems( observableList(mapeamentoService.buscarBancos()) );

        tcMapeamentoDB.setCellValueFactory(c -> c.getValue().colunaDBProperty());
        tcMapeamentoJava.setCellValueFactory(c -> c.getValue().colunaJavaProperty());
        tcMapeamentoTipoDB.setCellValueFactory(c -> c.getValue().tipoDBProperty());
        tcMapeamentoTipoJava.setCellValueFactory(c -> c.getValue().tipoJavaProperty());
        tcMapeamentoColNull.setCellValueFactory(c -> c.getValue().colNullProperty());
        tcMapeamentoConverter.setCellValueFactory(c -> c.getValue().converterProperty());
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


        var possivelModelo = Try.of(() -> mapeamentoService.extrair(
            cbxMapeamentoBanco.getValue(),
            txtMapeamentoOwner.getText(),
            txfMapeamentoNomeTabela.getText()
        ));

        if ( possivelModelo.isSuccess() ) {
            observableModelo.clear();
            observableModelo.addAll(possivelModelo.get());
            desabilitarAcoesMapeamento(true);

        } else {
            msgAviso( possivelModelo.getCause().getMessage() );
        }

    }

    public void onActionMapeamentoBtnLimpar() {

        desabilitarAcoesMapeamento(false);
        observableModelo.clear();
    }

    public void onActionBtnMapeamento() {

        var modelo = new Modelo.Builder()
            .comBanco(cbxMapeamentoBanco.getValue())
            .comOwner(txtMapeamentoOwner.getText())
            .comTabela(txfMapeamentoNomeTabela.getText())
            .comCampos(new HashSet<>(observableModelo))
            .build();

        mapeamentoService.executar( modelo );
    }

}
