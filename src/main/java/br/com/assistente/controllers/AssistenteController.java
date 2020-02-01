package br.com.assistente.controllers;

import br.com.assistente.models.Constante;
import br.com.assistente.models.DefinicaoDto;
import br.com.assistente.models.Modelo;
import br.com.assistente.models.ModeloCampo;
import br.com.assistente.models.ResultMapeamento;
import br.com.assistente.models.SetupUsuario;
import br.com.assistente.services.ConstanteService;
import br.com.assistente.services.DefinicaoDtoService;
import br.com.assistente.services.MapeamentoService;
import br.com.assistente.services.QueryService;
import io.vavr.Tuple2;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static br.com.assistente.controllers.DtoIdentityController.openViewDtoIdentity;
import static br.com.assistente.controllers.MapeamentoConfirmaGravacaoController.openViewConfirmarGravacao;
import static br.com.assistente.controllers.SetupController.openViewConfiguracoes;
import static br.com.assistente.infra.javafx.Dialog.msgAviso;
import static br.com.assistente.infra.javafx.Dialog.msgInfo;
import static br.com.assistente.infra.javafx.Dialog.selecionarArquivo;
import static br.com.assistente.models.Constante.convPadraoNomeEnum;
import static br.com.assistente.models.DefinicaoDto.convPadraoNomeClasse;
import static br.com.assistente.models.SetupUsuario.getCatalogosCnxSelecionada;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.control.cell.CheckBoxTableCell.forTableColumn;
import static javafx.scene.input.KeyCode.DELETE;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

public class AssistenteController {

    // Container principal da Aplicação
    @FXML private VBox vboxContainer;

    // Mapeamento:
    @FXML private ComboBox<String> cbxMapeamentoBanco;
    @FXML private TextField txtMapeamentoOwner;
    @FXML private TextField txfMapeamentoNomeTabela;
    @FXML private TableView<ModeloCampo> tbvMapeamento;
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
    @FXML private ComboBox<Constante.Tipo> cbxConstanteTipos;
    @FXML private TextField txfConstanteEnum;
    @FXML private TextField txfConstanteNome;
    @FXML private TextField txfConstanteValor;
    @FXML private TextField txfConstanteDescricao;
    @FXML private TableView<Constante> tbvConstante;
    @FXML private TableColumn<Constante, String> tcConstanteNome;
    @FXML private TableColumn<Constante, String> tcConstanteValor;
    @FXML private TableColumn<Constante, String> tcConstanteDescricao;
    private ObservableList<Constante> constantes = observableArrayList();
    @FXML private CheckBox cbxConstanteConverter;

    // DTO:
    @FXML private TextField txfDtoNomeClasse;
    @FXML private TextField txfDtoNomeAtributo;
    @FXML private ComboBox<DefinicaoDto.Tipo> cbxDtoTipo;
    @FXML private CheckBox cbxAtributoId;
    @FXML private TableView<DefinicaoDto> tbvDto;
    @FXML private TableColumn<DefinicaoDto, DefinicaoDto.Tipo> tcDtoTipo;
    @FXML private TableColumn<DefinicaoDto, String> tcDtoNomeAtributo;
    @FXML private TableColumn<DefinicaoDto, Boolean> tcDtoAtributoId;
    @FXML private TableColumn<DefinicaoDto, Integer> tcDtoPosicao;
    private ObservableList<DefinicaoDto> definicaoDtos;
    @FXML CheckBox cbxDtoJsonAnnotation;
    @FXML CheckBox cbxDtoAplicarBuilder;

    // Query:
    @FXML private TextField txfQueryNomeClasse;
    @FXML private TextArea txaQuery;
    @FXML private CheckBox cbxQueryTuple;
    @FXML private CheckBox cbxQueryJsonAnnotation;
    @FXML private CheckBox cbxQueryAplicarBuilder;

    // Result:
    @FXML private Tab tabResult;
    @FXML private TextArea txaResult;
    @FXML private ComboBox<ResultMapeamento> cbxResultArquivos;

    // Services:
    private final MapeamentoService mapeamentoService = new MapeamentoService();
    private final ConstanteService constanteService = new ConstanteService();
    private final DefinicaoDtoService definicaoDtoService = new DefinicaoDtoService();
    private final QueryService queryService = new QueryService();


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EVENTOS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    public void initialize() {

        SetupUsuario.load();
        initializeMapeamento();
        initializeConstantes();
        initializeDto();
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
        tbvMapeamento.getSortOrder().add( tcMapeamentoPosicao );
        desabilitarAcoesMapeamento(true);
    }

    public void onActionMapeamentoBtnLimpar() {

        desabilitarAcoesMapeamento(false);
        observableModelo.clear();
        cbxMapeamentoBanco.requestFocus();
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
            txaResult.setText( rm.getConteudoEntidade() );
        });
    }


    public void onActionMapeamento( final ActionEvent event ) {

        if ( isNull(event) || isNull(event.getSource()) ) return;
        final Control source = (Control) event.getSource();
    }



    public void onActionConstante( final ActionEvent event ) {

        if ( isNull(event) || isNull(event.getSource()) ) return;
        final Control source = (Control) event.getSource();

        switch ( source.getId() ) {
            case "btnConstanteLimpar":
                resetConstante();
                break;
            case "btnConstanteAdd":
                adicionarConstante();
                break;
            case "btnConstanteImportar":
                importarConstante();
                break;
            case "btnConstanteGerar":
                gerarResultConstante();
                break;
        }

    }

    public void onActionDto( final ActionEvent event ) {

        if ( isNull(event) || isNull(event.getSource()) ) return;
        final Control source = (Control) event.getSource();

        switch ( source.getId() ) {
            case "btnDtoLimpar":
                resetDto();
                break;
            case "btnDtoAdd":
                adicionarItemDto();
                break;
            case "btnDtoGerar":
                gerarResultDto();
                break;
        }
    }

    public void onActionQuery( final ActionEvent event ) {

        if ( isNull(event) || isNull(event.getSource()) ) return;

        final Control source = (Control) event.getSource();

        switch ( source.getId() ) {
            case "btnQueryLimpar":
                resetQuery();
                break;
            case "cbxQueryTuple":
                desabilitarAcoesQuery( cbxQueryTuple.isSelected() );
                break;
            case "btnQueryGerar":
                gerarResultQuery();
                break;
        }
    }

    public void onActionResult( final ActionEvent event ) {

        if ( isNull(event) || isNull(event.getSource()) ) return;

        final Control source = (Control) event.getSource();

        switch ( source.getId() ) {
            case "btnResultCopiar":
                copiarResultadoParaAreaTransferencia();
                break;
            case "btnResultGravar":
                gravarResultadoNaPastaDoProjeto();
                break;
            case "cbxResultArquivos":
                selecionarClasse();
                break;
            case "btnResultAtualizar":
                atualizarResult();
                break;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MAPEAMENTO
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Window getParent() {

        return vboxContainer.getScene().getWindow();
    }

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

        tbvMapeamento.setItems( observableModelo );
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTANTES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initializeConstantes() {

        cbxConstanteTipos.setItems( observableArrayList( Constante.Tipo.buscarTipos() ) );
        txfConstanteNome.focusedProperty().addListener( (ov, oldV, newV) -> {
            if ( !newV && isBlank( txfConstanteDescricao.getText() )) {
                txfConstanteDescricao.setText( txfConstanteNome.getText() );
            }
        });

        // Definição da TableView
        tcConstanteNome.setCellValueFactory( c -> c.getValue().nomeProperty() );
        tcConstanteValor.setCellValueFactory( c -> c.getValue().valorProperty() );
        tcConstanteDescricao.setCellValueFactory( c -> c.getValue().descricaoProperty() );
        constantes = observableArrayList();
        tbvConstante.setRowFactory( tv -> {
            // Duplo Clique!!
            final TableRow<Constante> row = new TableRow<>();
            row.setOnMouseClicked( event -> {
                if ( event.getClickCount() == 2 && !row.isEmpty() ) {
                    final Constante item = row.getItem();
                    txfConstanteNome.setText( item.getNome() );
                    txfConstanteValor.setText( item.getValor() );
                    txfConstanteDescricao.setText( item.getDescricao() );
                    txfConstanteNome.requestFocus();
                }
            });
            return row;
        });
        tbvConstante.setOnKeyPressed( event -> {
            if ( Objects.equals( event.getCode(), DELETE ) ) {
                final Constante constante = tbvConstante.getSelectionModel().getSelectedItem();
                if ( nonNull( constante ) ) {
                    constantes.removeIf( c -> Objects.equals( c.getValor(), constante.getValor() ) );
                    txfConstanteNome.requestFocus();
                }
            }
        });
        tbvConstante.setItems( constantes );
    }

    private void importarConstante() {

        msgInfo( "Arquivo csv deve estar no formato:\n\n \"DESCRICAO<TAB>VALOR\"");

        selecionarArquivo(
            "Selecione o arquivo",
            vboxContainer.getScene().getWindow(),
            new Tuple2<>( "Texto (CSV - tabulação)", "*.csv" )
        )
        .map( File::getAbsolutePath )
        .ifPresent( arquivo ->{
            final Set<Constante> constantesArquivo = constanteService.lerArquivoCSV( arquivo );
            if ( isNotEmpty( constantesArquivo ) ) {
                constantes.removeAll( constantesArquivo );
                constantes.addAll( constantesArquivo );
            }
        });

    }

    private void resetConstante() {

        txfConstanteEnum.setText( " " );
        txfConstanteNome.setText( "" );
        txfConstanteValor.setText( "" );
        txfConstanteDescricao.setText( "" );
        cbxConstanteConverter.setSelected( false );
        constantes.clear();
        txfConstanteEnum.requestFocus();
    }

    private void adicionarConstante() {

        final String valor = trim( txfConstanteValor.getText() );
        requireNonNull( cbxConstanteTipos.getValue(), "Favor informar o tipo!" ).checkTipo( valor );

        final Constante constante = new Constante.Builder()
            .comNome( txfConstanteNome.getText() )
            .comValor( txfConstanteValor.getText() )
            .comDescricao( txfConstanteDescricao.getText() )
            .build();

        constantes.removeIf( c ->
            Objects.equals( c.getNome(), constante.getNome() ) || Objects.equals( c.getValor(), constante.getValor() )
        );
        constantes.add( constante );

        txfConstanteNome.setText( "" );
        txfConstanteValor.setText( "" );
        txfConstanteDescricao.setText( "" );
        txfConstanteNome.requestFocus();
    }

    private void gerarResultConstante() {

        txfConstanteEnum.setText( convPadraoNomeEnum( txfConstanteEnum.getText() ) );

        setarResultado( constanteService.convTexto(
            txfConstanteEnum.getText(),
            cbxConstanteTipos.getValue(),
            new HashSet<>( constantes ),
            cbxConstanteConverter.isSelected()
        ));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DTO
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initializeDto() {

        cbxDtoTipo.setItems( observableArrayList( DefinicaoDto.Tipo.buscarTipos() ) );
        tcDtoTipo.setCellValueFactory( c -> c.getValue().tipoProperty() );
        tcDtoNomeAtributo.setCellValueFactory( c -> c.getValue().nomeAtributoProperty() );
        tcDtoAtributoId.setCellValueFactory( c -> c.getValue().atributoIdProperty() );
        tcDtoAtributoId.setCellFactory( forTableColumn(tcDtoAtributoId) );
        tcDtoPosicao.setCellValueFactory( c -> c.getValue().posicaoProperty().asObject() );
        definicaoDtos = observableArrayList();
        tbvDto.setRowFactory( tv -> {
            // Duplo Clique!!
            final TableRow<DefinicaoDto> row = new TableRow<>();
            row.setOnMouseClicked( event -> {
                if ( event.getClickCount() == 2 && !row.isEmpty() ) {
                    final DefinicaoDto item = row.getItem();
                    cbxDtoTipo.setValue( item.getTipo() );
                    txfDtoNomeAtributo.setText( item.getNomeAtributo() );
                    txfDtoNomeAtributo.requestFocus();
                }
            });
            return row;
        });
        tbvDto.setOnKeyPressed( event -> {
            if ( Objects.equals( event.getCode(), DELETE ) ) {
                final DefinicaoDto definicaoDto = tbvDto.getSelectionModel().getSelectedItem();
                if ( nonNull( definicaoDto ) ) {
                    definicaoDtos.removeIf( d -> Objects.equals( d, definicaoDto ) );
                    txfDtoNomeAtributo.requestFocus();
                }
            }
        });

        tbvDto.setItems( definicaoDtos );
    }

    private void resetDto() {

        txfDtoNomeClasse.setText( "" );
        cbxDtoTipo.setValue( null );
        txfDtoNomeAtributo.setText( "" );
        cbxDtoAplicarBuilder.setSelected( false );
        cbxDtoJsonAnnotation.setSelected( false );
        definicaoDtos.clear();
        txfDtoNomeClasse.requestFocus();
    }

    private void adicionarItemDto() {

        final DefinicaoDto definicaoDto = new DefinicaoDto.Builder()
            .comPosicao( size( definicaoDtos ) + 1 )
            .comNomeAtributo( txfDtoNomeAtributo.getText() )
            .comTipo( cbxDtoTipo.getValue() )
            .comAtributoId( cbxAtributoId.isSelected() )
            .build();

        definicaoDtos.removeIf( d -> Objects.equals( d, definicaoDto ) );
        definicaoDtos.add( definicaoDto );

        txfDtoNomeAtributo.setText( "" );
        cbxDtoTipo.setValue( null );
        cbxAtributoId.setSelected( false );
        cbxDtoTipo.requestFocus();
    }

    private void gerarResultDto() {

        txfDtoNomeClasse.setText( convPadraoNomeClasse( txfDtoNomeClasse.getText() ) );
        setarResultado( definicaoDtoService.convTexto(
            txfDtoNomeClasse.getText(),
            new HashSet<>( definicaoDtos ),
            cbxDtoJsonAnnotation.isSelected(),
            cbxDtoAplicarBuilder.isSelected()
        ));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // QUERY
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void resetQuery() {

        txfQueryNomeClasse.setText( "" );
        txaQuery.setText( "" );
        cbxQueryTuple.setSelected( false );
        cbxQueryJsonAnnotation.setSelected( false );
        cbxQueryAplicarBuilder.setSelected( false );
    }

    private void desabilitarAcoesQuery( final boolean desabilitar ) {

        if ( desabilitar ) txfQueryNomeClasse.setText( "" );
        txfQueryNomeClasse.setDisable( desabilitar );
        cbxQueryJsonAnnotation.setDisable( desabilitar );
        cbxQueryAplicarBuilder.setDisable( desabilitar );
    }

    public void gerarResultQuery() {

        if ( cbxQueryTuple.isSelected() ) {
            setarResultado( queryService.convTexto( txaQuery.getText() ) );
        } else {
            txfQueryNomeClasse.setText( convPadraoNomeClasse( txfQueryNomeClasse.getText() ) );
            setarResultado(
                queryService.convTexto(
                    txfQueryNomeClasse.getText(),
                    txaQuery.getText(),
                    cbxQueryJsonAnnotation.isSelected(),
                    cbxQueryAplicarBuilder.isSelected(),
                    queryDto -> openViewDtoIdentity( getParent(), queryDto )
                )
            );
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // RESULTADO.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setarResultado( final Set<ResultMapeamento> results ) {

        if ( isEmpty( results ) ) return;

        cbxResultArquivos.setItems( observableArrayList( results ) );
        setarTab( tabResult );

        results.stream().findFirst().ifPresent( rm -> {
            cbxResultArquivos.setValue( rm );
            txaResult.setText( rm.getConteudoEntidade() );
        });
    }

    private boolean gerouResult() {

        return nonNull(cbxResultArquivos) && nonNull( cbxResultArquivos.getValue() );
    }

    private void selecionarClasse() {

        if ( !gerouResult() ) return;
        txaResult.setText( cbxResultArquivos.getValue().getConteudoEntidade() );
    }

    private void copiarResultadoParaAreaTransferencia() {

        final ClipboardContent content = new ClipboardContent();
        content.putString( txaResult.getText() );
        Clipboard.getSystemClipboard().setContent( content );
    }

    private void atualizarResult() {

        if ( !gerouResult() ) return;
        final String nomeEntidade = cbxResultArquivos.getValue().getNomeEntidade();
        final List<ResultMapeamento> novaLista = cbxResultArquivos
            .getItems()
            .stream()
            .map( rs -> Objects.equals( rs.getNomeEntidade(), nomeEntidade )
                ? new ResultMapeamento.Builder().comNomeEntidade( nomeEntidade ).comConteudoEntidade( txaResult.getText() ).build()
                : rs
            ).collect( toList() );
        cbxResultArquivos.setItems( observableArrayList( novaLista ) );
    }

    private void gravarResultadoNaPastaDoProjeto() {

        if ( !gerouResult() ) return;

        final ResultMapeamento value = cbxResultArquivos.getValue();
        switch ( requireNonNull( value.getTipoResult(), "Tipo de Result não localizado!" ) ) {
            case MAPEAMENTO:
                mapeamentoService.gravarArquivos(
                    new HashSet<>(cbxResultArquivos.getItems()),
                    msg -> openViewConfirmarGravacao( getParent(), msg ) );
                msgInfo( "Operação concluída" );
                break;
            case CONSTANTE:
                constanteService.gravarArquivos( new HashSet<>(cbxResultArquivos.getItems()) );
                msgInfo( "Operação concluída" );
                break;
            case DTO:
                break;
        }
    }

}
