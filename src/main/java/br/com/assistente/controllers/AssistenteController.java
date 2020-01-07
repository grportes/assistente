package br.com.assistente.controllers;

import br.com.assistente.models.Modelo;
import br.com.assistente.models.ModeloCampo;
import br.com.assistente.models.ResultMapeamento;
import br.com.assistente.models.SetupUsuario;
import br.com.assistente.services.MapeamentoService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static br.com.assistente.controllers.SetupController.openViewConfiguracoes;
import static br.com.assistente.infra.javafx.Dialog.msgAviso;
import static br.com.assistente.models.SetupUsuario.getCatalogosCnxSelecionada;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.control.cell.CheckBoxTableCell.forTableColumn;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

public class AssistenteController {

    // Container principal da Aplicação
    @FXML private VBox vboxContainer;

    // Mapeamento:
    @FXML private ComboBox<String> cbxMapeamentoBanco;
    @FXML private TextField txtMapeamentoOwner;
    @FXML private TextField txfMapeamentoNomeTabela;
    @FXML private TableView<ModeloCampo> tblMapeamento;
    @FXML private TableColumn<ModeloCampo, Integer> tcMapeamentoPosicao;
    @FXML private TableColumn<ModeloCampo, Boolean> tcMapeamentoColNull;
    @FXML private TableColumn<ModeloCampo, Boolean> tcMapeamentoID;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoDB;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoJava;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoTipoDB;
    @FXML private TableColumn<ModeloCampo, Integer> tcMapeamentoTamanho;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoTipoJava;
    @FXML private TableColumn<ModeloCampo, Boolean> tcMapeamentoConverter;
    @FXML private TableColumn<ModeloCampo, String> tcMapeamentoNomeEnum;
    @FXML private Button btnMapeamento;
    private ObservableList<ModeloCampo> observableModelo = observableArrayList();

    // Constante:

    // Result:
    @FXML private Tab tabResult;
    @FXML private TextArea txtResult;
    @FXML private ComboBox<ResultMapeamento> cbxResultArquivos;

    // Services:
    private final MapeamentoService mapeamentoService = new MapeamentoService();


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EVENTOS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    public void initialize() {

        SetupUsuario.load();
        initializeMapeamento();
    }

    public void onActionConfiguracoes() {

        openViewConfiguracoes(
            vboxContainer.getScene().getWindow(),
            reload -> {
                if ( reload ) {
                    cbxMapeamentoBanco.setItems( observableArrayList( getCatalogosCnxSelecionada() ) );
                }
            });
    }

    public void onActionMapeamentoBtnLerTabela() {

        txtMapeamentoOwner.setText( lowerCase( trim( txtMapeamentoOwner.getText() ) ) );
        txfMapeamentoNomeTabela.setText( lowerCase( trim( txfMapeamentoNomeTabela.getText() ) ) );

        final Set<ModeloCampo> campos = mapeamentoService.extrair(
            new Modelo.Builder()
                .comBanco( cbxMapeamentoBanco.getValue() )
                .comOwner( txtMapeamentoOwner.getText() )
                .comTabela( txfMapeamentoNomeTabela.getText() )
                .build()
        );

        if ( isEmpty( campos ) ) {
            msgAviso( "Não foi possivel ler dados" );
            return;
        }

        observableModelo.clear();
        observableModelo.addAll( campos );
        tblMapeamento.getSortOrder().add( tcMapeamentoPosicao );
        desabilitarAcoesMapeamento(true);
    }

    public void onActionMapeamentoBtnLimpar() {

        desabilitarAcoesMapeamento(false);
        observableModelo.clear();
    }

    public void onActionBtnMapeamento() {

        final Modelo modelo = new Modelo.Builder()
            .comBanco( cbxMapeamentoBanco.getValue() )
            .comOwner( txtMapeamentoOwner.getText() )
            .comTabela( txfMapeamentoNomeTabela.getText() )
            .comCampos( new HashSet<>(observableModelo) )
            .build();

        final Set<ResultMapeamento> results = mapeamentoService.executar( modelo );
        if ( isEmpty( results ) ) return;

        cbxResultArquivos.setItems( observableArrayList( results ) );
        setarTab( tabResult );

        results.stream().findFirst().ifPresent( rm -> {
            cbxResultArquivos.setValue( rm );
            txtResult.setText( rm.getConteudoEntidade() );
        });
    }

    public void onActionResult( final ActionEvent event ) {

        if ( isNull(event) || isNull(event.getSource()) ) return;

        final Control source = (Control) event.getSource();

        switch ( source.getId() ) {
            case "btnResultCopiar":
                break;
            case "btnResultGravar":
                break;
            case "cbxResultArquivos":
                if ( nonNull(cbxResultArquivos) && nonNull( cbxResultArquivos.getValue() ) )
                    txtResult.setText( cbxResultArquivos.getValue().getConteudoEntidade() );
                break;
            case "btnResultAtualizar":
                if ( nonNull(cbxResultArquivos) && nonNull( cbxResultArquivos.getValue() ) ) {
                    final String nomeEntidade = cbxResultArquivos.getValue().getNomeEntidade();
                    final List<ResultMapeamento> novaLista = cbxResultArquivos
                        .getItems()
                        .stream()
                        .map( rs -> Objects.equals( rs.getNomeEntidade(), nomeEntidade )
                            ? new ResultMapeamento.Builder().comNomeEntidade( nomeEntidade ).comConteudoEntidade( txtResult.getText() ).build()
                            : rs
                        ).collect( toList() );
                    cbxResultArquivos.setItems( observableArrayList( novaLista ) );
                }
                break;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initializeMapeamento() {

        tcMapeamentoPosicao.setCellValueFactory( c -> c.getValue().posicaoProperty().asObject() );
        tcMapeamentoColNull.setCellValueFactory( c -> c.getValue().colNullProperty() );
        tcMapeamentoColNull.setCellFactory( forTableColumn(tcMapeamentoColNull) );
        tcMapeamentoID.setCellValueFactory( c -> c.getValue().pkProperty() );
        tcMapeamentoID.setCellFactory( forTableColumn(tcMapeamentoID) );
        tcMapeamentoDB.setCellValueFactory( c -> c.getValue().colunaDBProperty() );
        tcMapeamentoJava.setCellValueFactory( c -> c.getValue().colunaJavaProperty() );
        tcMapeamentoTipoDB.setCellValueFactory( c -> c.getValue().tipoDBProperty() );
        tcMapeamentoTipoJava.setCellValueFactory( c -> c.getValue().tipoJavaProperty() );
        tcMapeamentoTamanho.setCellValueFactory( c -> c.getValue().tamanhoProperty().asObject() );
        tcMapeamentoConverter.setCellValueFactory( c -> c.getValue().converterProperty() );
        tcMapeamentoConverter.setCellFactory( forTableColumn( index -> {
            final ModeloCampo modeloCampo = observableModelo.get( index );
            if ( modeloCampo.isConverter() ) {
                Platform.runLater( () -> {
                    final TextInputDialog dialog = new TextInputDialog( modeloCampo.getNomeEnum() );
                    dialog.setTitle( "Atenção" );
                    dialog.setContentText( "Informe o nome do Enum:" );
                    Optional<String> possivelTexto = dialog.showAndWait();
                    if ( possivelTexto.isPresent() ) {
                        modeloCampo.nomeEnumProperty().setValue( possivelTexto.get() );
                    } else {
                        modeloCampo.nomeEnumProperty().setValue( "" );
                        modeloCampo.converterProperty().setValue( false );
                    }
                });
            } else {
                modeloCampo.nomeEnumProperty().setValue( "" );
            }
            return modeloCampo.converterProperty();
        }));
        tcMapeamentoNomeEnum.setCellValueFactory( c -> c.getValue().nomeEnumProperty() );

        tblMapeamento.setItems( observableModelo );
        cbxMapeamentoBanco.setItems( observableArrayList( getCatalogosCnxSelecionada() ) );
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

    private void setarTab( final Tab tab ) {

        tab.getTabPane().getSelectionModel().select( tab );
    }
}
