package util;

import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.Mode;
import play.db.Database;
import play.db.jpa.JPAApi;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import tasks.Module;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import static infra.util.UtilCollections.isVazia;
import static infra.util.UtilDate.getAgora;
import static infra.util.UtilString.apenasNumero;
import static infra.util.UtilString.contem;
import static infra.util.UtilString.converterCamelCase;
import static infra.util.UtilString.equalsIgnoreCase;
import static infra.util.UtilString.getOrElse;
import static infra.util.UtilString.isEmptyGet;
import static infra.util.UtilString.isVazia;
import static infra.util.UtilString.removerAcentosECaracteresEspeciais;
import static infra.util.UtilString.toUpperCase;
import static java.lang.Character.isWhitespace;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showConfirmDialog;

/**
 * Classe Assistente
 *
 * <p>Autor: GPortes</p>
 *
 * <ul>
 *     <li>Gerar mapeamento</ui>
 *     <li>Gerar Dto</ui>
 *     <li>Gerar Constantes</ui>
 * </ul>
 *
 */
public class A extends WithApplication {

    private static String[] BANCOS = new String[] {
        "vendas",
        "admin",
        "dbadmin",
        "move",
        "estoque",
        "transp",
        "pbatch",
        "estati",
        "rh",
        "seguranca",
        "temporario",
        "contabil",
        "fisco",
        "fisco01",
        "fisco08",
        "fiscorobson4"
    };

    private static Map<String,String> COLUNAS_ID = new LinkedHashMap<>();

    static {
        COLUNAS_ID.put("acerto", "idAcerto");
        COLUNAS_ID.put("cliente", "idCliente");
        COLUNAS_ID.put("carga", "idCarga");
        COLUNAS_ID.put("cidade", "idCidade");
        COLUNAS_ID.put("convenio", "idConvenio");
        COLUNAS_ID.put("conta", "idConta");
        COLUNAS_ID.put("cond_venda","idCondVenda");
        COLUNAS_ID.put("debito", "idDebito");
        COLUNAS_ID.put("destinatario", "idDestinatario");
        COLUNAS_ID.put("departamento", "idDepartamento");
        COLUNAS_ID.put("descarga", "idDescarga");
        COLUNAS_ID.put("distrito", "idDistrito");
        COLUNAS_ID.put("documento", "idDocumento");
        COLUNAS_ID.put("empresa", "idEmpresa");
        COLUNAS_ID.put("empresa_motorista", "idEmpresaMotorista");
        COLUNAS_ID.put("filial", "idFilial");
        COLUNAS_ID.put("funcionario", "idFuncionario");
        COLUNAS_ID.put("fornecedor", "idFornecedor");
        COLUNAS_ID.put("folha", "idFolha");
        COLUNAS_ID.put("grupo_cidade", "idGrupoCidade");
        COLUNAS_ID.put("mercadoria", "idMercadoria");
        COLUNAS_ID.put("matricula", "idMatricula");
        COLUNAS_ID.put("motorista", "idMotorista");
        COLUNAS_ID.put("nf_saida", "idNfSaida");
        COLUNAS_ID.put("nf_fiscal", "idNfFiscal");
        COLUNAS_ID.put("nro_nota", "idNroNota");
        COLUNAS_ID.put("ocorrencia","idOcorrencia");
        COLUNAS_ID.put("ocor_cadastro","idOcorCadastro");
        COLUNAS_ID.put("pedido", "idPedido");
        COLUNAS_ID.put("promocao","idPromocao");
        COLUNAS_ID.put("ramo_atividade","idRamoAtividade");
        COLUNAS_ID.put("representante","idRepresentante");
        COLUNAS_ID.put("sessao", "idSessao");
        COLUNAS_ID.put("setor", "idSetor");
        COLUNAS_ID.put("transportadora","idTransportadora");
        COLUNAS_ID.put("usuario", "idUsuario");
        COLUNAS_ID.put("veiculo", "idVeiculo");
        COLUNAS_ID.put("viagem", "idViagem");
    }

    private JPAApi jpaApi;
    private Database db;

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder()
                .in( Mode.TEST )
                .disable( Module.class )
                .build();
    }

    @Before
    public void before() {

        jpaApi = instanceOf( JPAApi.class );
        db = instanceOf( Database.class );
    }

    @Test
    public void testar() {

        new AssistenteUI().run();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Interface inicial.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class AssistenteUI {

        public void run() {


            JRadioButton jrbMapear = new JRadioButton("MAPEAMENTO",true);
            JRadioButton jrbGerarDto = new JRadioButton("GERAR DTO",false);
            JRadioButton jrbGerarDtoQuery = new JRadioButton("GERAR DTO / QUERY",false);
            JRadioButton jrbGerarConstante = new JRadioButton("GERAR CONSTANTE",false);

            ButtonGroup group = new ButtonGroup();
            group.add(jrbMapear);
            group.add(jrbGerarDto);
            group.add(jrbGerarDtoQuery);
            group.add(jrbGerarConstante);

            JPanel jPanel = new JPanel(new GridLayout(0,1));
            jPanel.add(jrbMapear);
            jPanel.add(jrbGerarDto);
            jPanel.add(jrbGerarDtoQuery);
            jPanel.add(jrbGerarConstante);

            if ( JOptionPane.showConfirmDialog(null, jPanel, "Assistente:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION ) {

                if ( jrbMapear.isSelected() )
                    new MapeamentoUI().run();

                if ( jrbGerarDto.isSelected() )
                    new GerarDtoUI().run();

                if ( jrbGerarDtoQuery.isSelected() )
                    new GerarDtoQueryUI().run();

                if ( jrbGerarConstante.isSelected() )
                    new GerarConstanteUI().run();
            }

        }

    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MAPEAMENTO.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class MapeamentoUI {

        public void run() {

            JComboBox cbxBancos = new JComboBox(BANCOS);

            JTextField jtfDono = new JTextField("dbo");

            JTextField jtfTabela = new JTextField("");

            JTextField jtfAutor = new JTextField( converterCamelCase(System.getProperty("user.name"), true) );

            final JTextField jtfEnumId = new JTextField("");
            jtfEnumId.setEditable(false);

            final JTextField jtfEnumDescricao = new JTextField("");
            jtfEnumDescricao.setEditable(false);

            JCheckBox jcbConstante = new JCheckBox("Gerar Enum");
            jcbConstante.addActionListener( (e) -> {
                boolean selected = ((AbstractButton) e.getSource()).getModel().isSelected();
                jtfEnumId.setEditable(selected);
                jtfEnumDescricao.setEditable(selected);
            });


            JPanel jpnPanel = new JPanel(new GridLayout(0, 1));
            jpnPanel.add(new JLabel("Autor:"));
            jpnPanel.add(jtfAutor);
            jpnPanel.add(new JLabel("Banco:"));
            jpnPanel.add(cbxBancos);
            jpnPanel.add(new JLabel("Dono:"));
            jpnPanel.add(jtfDono);
            jpnPanel.add(new JLabel("Tabela:"));
            jpnPanel.add(jtfTabela);
            jpnPanel.add(jcbConstante);
            jpnPanel.add(new JLabel("Enum Campo Id:"));
            jpnPanel.add(jtfEnumId);
            jpnPanel.add(new JLabel("Enum Campo Descrição:"));
            jpnPanel.add(jtfEnumDescricao);

            if ( showConfirmDialog(null, jpnPanel, "Mapeamento:", OK_CANCEL_OPTION, PLAIN_MESSAGE) == OK_OPTION ) {
                Info info = new Info();
                info.autor = jtfAutor.getText();
                info.banco = (String) cbxBancos.getSelectedItem();
                info.dono = jtfDono.getText();
                info.tabela = jtfTabela.getText();
                info.gerarEnum = jcbConstante.isSelected();
                info.enumId = jtfEnumId.getText();
                info.enumDescricao = jtfEnumDescricao.getText();
                mapear( info );
            }

        }

        private final class Info {

            public String autor = "";
            public String banco = "";
            public String tabela = "";
            public String dono = "";
            public boolean pkComposta = false;
            public boolean gerarEnum = false;
            public String enumId = "";
            public String enumDescricao = "";

            public String getEntidade() {

                return converterCamelCase( getSingular(tabela), true);
            }

            public String getSelectTop1() {

                if ( this.banco.contains( "fisco" ) )
                    return "select top 1 * from " + this.banco + "." + this.dono + "." + this.tabela + " at isolation read committed";

                return "select top 1 * from " + this.banco + "." + this.dono + "." + this.tabela;
            }

            public String getSelectAll() {

                return "select * from " + this.banco + "." + this.dono + "." + this.tabela;
            }

        }

        private void mapear( final Info info ) {

            jpaApi.withTransaction( () -> {

                StringBuilder mapeamento = new StringBuilder();
                StringBuilder generateGetSet = new StringBuilder();
                Connection conn = db.getConnection();
                Statement stmt = null;

                try {

                    stmt = conn.createStatement();
                    ResultSet rset = stmt.executeQuery( info.getSelectTop1() );
                    ResultSetMetaData rsmd = rset.getMetaData();
                    int numColumns = rsmd.getColumnCount();

                    List<String> pks = getPrimaryKeys( conn, info );
                    info.pkComposta = pks.size() > 1;

                    if ( info.pkComposta ) {

                        mapeamento.append(getCabecalhoId(info));

                        mapeamento.append( "\n" );
                        mapeamento.append(criarComentario("COLUNAS MAPEADAS"));
                        mapeamento.append( "\n" );

                        for ( int i = 0; i < numColumns; i++ ) {
                            if ( pks.contains(rsmd.getColumnName(i + 1)) ) {
                                final Columns coluna = getColuna(rsmd, i, TipoChave.COMPOSTA);
                                mapeamento.append( coluna.getAtributos() );
                                generateGetSet.append( coluna.getMetodos() );
                            }
                        }

                        mapeamento.append( "\n\n" );
                        mapeamento.append( criarComentario( "CONSTRUCTOR" ) );
                        mapeamento.append( "\n\n\tpublic " ).append( info.getEntidade() ).append( "Id" ).append( "() {" );
                        mapeamento.append( "\n\t\tsuper();" );
                        mapeamento.append( "\n\t}" );
                        mapeamento.append( "\n\n\tpublic " ).append( info.getEntidade() ).append( "Id" ).append( "( arg,arg,arg ) {" );
                        mapeamento.append( "\n\t\t...implementar..." );
                        mapeamento.append( "\n\t}" );

                        mapeamento.append( "\n\n\n" );
                        mapeamento.append( criarComentario( "TO_STRING" ) );
                        mapeamento.append( "\n\n\t@Override" );
                        mapeamento.append( "\n\tpublic String toString() {" );
                        mapeamento.append( "\n\t\t...implementar..." );
                        mapeamento.append( "\n\t}" );

                        mapeamento.append( "\n\n\n" );
                        mapeamento.append( criarComentario( "GETTERS / SETTERS" ) );
                        mapeamento.append( "\n" );
                        mapeamento.append( generateGetSet.toString() );

                        mapeamento.append( "\n\n\n" );
                        mapeamento.append( criarComentario( "EQUALS & HASCODE" ) );
                        mapeamento.append( gerarEqualsHashcode(info.getEntidade() + "Id") );
                        mapeamento.append( "\n\n}" );

                        mapeamento.append( getCabecalho( info ) );

                        // Id.
                        generateGetSet.delete( 0, generateGetSet.length() );
                        generateGetSet.append( format( "\n\tpublic %s getId() {", info.getEntidade() ) );
                        generateGetSet.append( "\n\n\t\treturn this.id;");
                        generateGetSet.append( "\n\t}");
                        generateGetSet.append( format( "\n\n\tpublic setId( final %s id ) {", info.getEntidade() ) );
                        generateGetSet.append( "\n\n\t\tthis.id = id;");
                        generateGetSet.append( "\n\t}\n");

                        // Demais colunas da tabela.
                        for ( int i = 0; i < numColumns; i++ ) {
                            if ( pks.contains( rsmd.getColumnName( i + 1 ) ) )
                                continue;
                            Columns coluna = getColuna(rsmd, i);
                            mapeamento.append( coluna.getAtributos() );
                            generateGetSet.append( coluna.getMetodos() );
                        }

                    } else {

                        mapeamento.append( getCabecalho( info ) );

                        for ( int i = 0; i < numColumns; i++ ) {
                            Columns coluna = getColuna(rsmd, i, pks.contains(rsmd.getColumnName(i + 1)) ? TipoChave.SIMPLES : TipoChave.SEM_CHAVE);
                            mapeamento.append( coluna.getAtributos() );
                            generateGetSet.append( coluna.getMetodos() );
                        }
                    }

                    if ( info.gerarEnum )
                        mapeamento.append( getConstante( stmt, info ) );

                    mapeamento.append( "\n\n" );
                    mapeamento.append( criarComentario( "RELACIONAMENTO ENTRE ENTIDADES" ) );
                    mapeamento.append( "\n\n\n\n" );

                    mapeamento.append( criarComentario( "GETTERS / SETTERS" ) );
                    mapeamento.append( "\n" );
                    mapeamento.append( generateGetSet.toString() );

                    mapeamento.append( "\n\n" );
                    mapeamento.append( criarComentario( "METODOS TRANSIENTES" ) );
                    mapeamento.append( "\n\n\n\n" );

                    mapeamento.append( criarComentario( "CONSTANTES" ) );
                    mapeamento.append( "\n\n\n\n" );

                    mapeamento.append( criarComentario( "EQUALS & HASCODE" ) );
                    mapeamento.append( gerarEqualsHashcode(info.getEntidade()) );

                    mapeamento.append( "\n\n\n\n" );
                    mapeamento.append( criarComentario( "METODOS AUXILIARES" ) );
                    mapeamento.append( "\n\n}\n\n" );

                    exibir( mapeamento.toString() );

                } catch ( SQLException e ) {

                    System.out.println( "=======================================================" );
                    System.out.println( "======================= FALHA =========================" );
                    System.out.println( "=======================================================" );
                    System.out.println( info.getSelectTop1() );
                    System.out.println( e.getMessage() );
                    System.out.println( "=======================================================" );
                    System.out.println( "=======================================================" );

                } finally {

                    try {
                        if ( stmt != null )
                            stmt.close();
                    } catch ( SQLException e ) {
                        e.printStackTrace();
                    }

                }
            });
        }

        private String gerarEqualsHashcode( final String entidade ) {

            StringBuilder mapeamento = new StringBuilder();

            mapeamento.append( "\n\n\t@Override");
            mapeamento.append( "\n\tpublic boolean equals( Object o ) {" );
            mapeamento.append( "\n\n\t\tif ( this == o ) return true;" );
            mapeamento.append( format( "\n\t\tif ( !( o instanceof %s ) ) return false;", entidade ) );
            mapeamento.append( format( "\n\t\t%s that = (%s) o;", entidade, entidade ) );
            mapeamento.append( "\n\t\treturn Objects.equals( getId(), that.getId() );" );
            mapeamento.append( "\n\t}" );
            mapeamento.append( "\n\n\t@Override");
            mapeamento.append( "\n\tpublic int hashCode() {");
            mapeamento.append( "\n\n\t\treturn Objects.hash( getId() );");
            mapeamento.append( "\n\t}" );

            return mapeamento.toString();
        }

        private List<String> getPrimaryKeys(Connection conn, Info info) throws SQLException {

            List<String> pks = new ArrayList<>();
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getPrimaryKeys(info.banco,info.dono,info.tabela);
            while( rs.next() ) pks.add( rs.getString(4) );
            return pks;
        }

        private String getCabecalhoId(Info info) {

            StringBuilder builder = new StringBuilder();
            builder.append("\n\n\n");
            builder.append("\nimport javax.persistence.Embeddable;");
            builder.append("\nimport javax.persistence.Column;");
            builder.append("\nimport java.io.Serializable;");
            builder.append("\nimport java.util.Objects;");
            builder.append("\n\n/**");
            builder.append("\n * Classe que representa o Id da entidade: ").append(info.getEntidade());
            builder.append("\n *");
            builder.append("\n * <p>Autor: ").append(info.autor).append("</p>");
            builder.append("\n *");
            builder.append("\n * @since ").append(new SimpleDateFormat("dd/MM/yyyy").format(getAgora()));
            builder.append("\n *");
            builder.append("\n * @see models.domains.").append(info.banco).append(".").append(info.getEntidade());
            builder.append("\n */");
            builder.append("\n@Embeddable");
            builder.append("\npublic class ").append(info.getEntidade()).append("Id implements Serializable {");
            builder.append("\n");
            return builder.toString();

        }

        private String getCabecalho(Info info) {

            StringBuilder builder = new StringBuilder();

            builder.append("\n\n\n\n");
            builder.append( info.pkComposta ? "import javax.persistence.EmbeddedId;" :  "import javax.persistence.Id;" );
            builder.append("\nimport javax.persistence.Entity;");
            builder.append("\nimport javax.persistence.Table;");
            builder.append("\nimport javax.persistence.Column;");
            builder.append("\nimport javax.persistence.GeneratedValue;");
            builder.append("\nimport javax.persistence.GenerationType;");
            builder.append("\nimport java.util.Objects;");
            builder.append("\nimport infra.model.Model;");
            builder.append("\n\n/**");
            builder.append("\n");
            builder.append(" * Classe ref. ao mapeamento da tabela [ ").append(info.banco).append(".").append(info.dono).append(".").append(info.tabela).append(" ]");
            builder.append("\n");
            builder.append(" *");
            builder.append("\n * <p>Autor: ").append(info.autor).append("</p>");
            builder.append("\n *");
            builder.append("\n * @since ").append(new SimpleDateFormat("dd/MM/yyyy").format( getAgora()));
            builder.append("\n */");
            builder.append("\n");
            builder.append("@Entity");
            builder.append("\n");
            builder.append("@Table( name = \"").append(info.tabela).append("\", schema = \"").append(info.dono).append("\", catalog = \"").append(info.banco).append("\" )");
            builder.append("\n");
            builder.append("public class ").append(info.getEntidade()).append(" extends Model {");
            builder.append("\n\n");
            builder.append(criarComentario("COLUNAS MAPEADAS"));
            builder.append("\n\n");

            if ( info.pkComposta ) {
                builder.append("\t@EmbeddedId");
                builder.append("\n\tprivate ").append(info.getEntidade()).append("Id id;");
                builder.append("\n");
            }

            return builder.toString();

        }

        private Columns getColuna(ResultSetMetaData rsmd, int i) throws SQLException {

            return getColuna(rsmd, i, TipoChave.SEM_CHAVE);
        }

        private Columns getColuna(
            final ResultSetMetaData rsmd,
            final int i,
            final TipoChave tipoChave
        ) throws SQLException {

            int col = i + 1;
            String nomeColunaDb = rsmd.getColumnName(col);
            String tipoColunaDb = rsmd.getColumnTypeName(col);
            StringBuilder atributo = new StringBuilder();
            StringBuilder metodoGet = new StringBuilder();
            StringBuilder metodoSet = new StringBuilder();

            if ( "version".equals(nomeColunaDb) )
                atributo.append("\n\t@Version");

            if ( Objects.equals( TipoChave.SIMPLES, tipoChave )  ) {
                atributo.append("\t@Id");
                if ( tipoColunaDb.contains("identity") ) {
                    atributo.append("\n\t@GeneratedValue( strategy = GenerationType.AUTO )");
                }
            }

            atributo.append("\n\t@Column( name = \"").append(nomeColunaDb).append("\", ");

            if ( isNumeric(tipoColunaDb) ) {
                atributo.append("columnDefinition = \"");
                atributo.append(tipoColunaDb).append("(").append(rsmd.getPrecision(col));
                atributo.append(",").append(rsmd.getScale(col)).append(")\"");
                atributo.append(", precision = ").append(rsmd.getPrecision(col));
                atributo.append(", scale = ").append(rsmd.getScale(col));
            } else if ( isTexto(tipoColunaDb) ) {
                atributo.append("columnDefinition = \"");
                atributo.append(tipoColunaDb).append("(").append(rsmd.getPrecision(col));
                atributo.append(")\", length = ").append(rsmd.getPrecision(col));
            } else {
                if ("int identity".equals(tipoColunaDb))
                    atributo.append("columnDefinition = \"int\"");
                else
                    atributo.append("columnDefinition = \"").append(tipoColunaDb).append("\"");
            }

            switch ( rsmd.isNullable(i + 1) ) {
                case ResultSetMetaData.columnNullable:
                    atributo.append(", ").append("nullable = true");
                    break;
                case ResultSetMetaData.columnNoNulls:
                    atributo.append(", ").append("nullable = false");
                    break;
            }

            atributo.append(" )");

            final String tipoDeDados = contem( nomeColunaDb,"cpf", "cnpj", "cgc", "cnpj_local", "conselho_regional_inscricao", "pis_pasep", "habilitacao" )
                ? "Long"
                : getTipo( tipoColunaDb,true );

            atributo.append("\n\tprivate ");
            atributo.append( tipoDeDados );

            boolean isPk = false;

            nomeColunaDb = traduzNomeColuna(nomeColunaDb);

            switch (tipoChave) {
                case COMPOSTA:
                    isPk = false;
                    atributo
                    .append(" ")
                    .append( COLUNAS_ID.getOrDefault( nomeColunaDb,converterCamelCase( nomeColunaDb ) ) )
                    .append(";");
                    break;
                case SIMPLES:
                    isPk = true;
                    atributo.append(" ").append("id;");
                    break;
                default:
                    atributo.append(" ").append( converterCamelCase(nomeColunaDb) ).append(";");
            }

            atributo.append("\n");

            final String colunaJava = isPk ? "id" : converterCamelCase(nomeColunaDb);
            final String colunaJava2 = isPk ? "Id" : converterCamelCase(nomeColunaDb,true);

            metodoGet.append( format( "\n\tpublic %s get%s() {", tipoDeDados, colunaJava2 ) );
            metodoGet.append( format( "\n\n\t\treturn this.%s;", colunaJava ) );
            metodoGet.append( "\n\t}\n");

            metodoSet.append( format( "\n\tpublic void set%s( final %s %s ) {", colunaJava2, tipoDeDados, colunaJava ) );
            metodoSet.append( format( "\n\n\t\tthis.%s = %s;", colunaJava, colunaJava ) );
            metodoSet.append( "\n\t}\n");

            return Columns.newInstance( atributo.toString(), metodoGet.append(metodoSet.toString()).toString() );
        }

        private String getConstante(Statement stmt, Info info) throws SQLException {

            StringBuilder builder = new StringBuilder();

            builder.append("\n\n");
            builder.append( criarComentario("CONSTANTES") );
            builder.append("\n\n");

            builder.append("\tpublic enum Id").append(info.getEntidade());
            builder.append(" implements Constante<?> {");
            builder.append("\n\n");

            ResultSet rs = stmt.executeQuery(info.getSelectAll());

            while ( rs.next() ) {

                String descricao = removerAcentosECaracteresEspeciais(rs.getObject(info.enumDescricao).toString().trim());

                builder.append("\t\t/**");
                builder.append("\n\t\t * ").append(rs.getObject(info.enumId));
                builder.append("\n\t\t */");
                builder.append("\n\t\t").append( descricao.replaceAll("[-/.()]","_").replaceAll(" ","_") ).append("(\"");
                builder.append(descricao).append("\", ( ? ) ");
                builder.append(rs.getObject(info.enumId)).append(" )");

                if ( rs.isLast() ) {
                    builder.append("\n\n\t\t;");
                    builder.append("\n\n\t\tprivate ? valor;");
                    builder.append("\n\t\tprivate String descricao;");
                    builder.append("\n\n\t\t").append("Id").append(info.getEntidade()).append("( final String descricao,");
                    builder.append("\n\t\t\t\t\t").append("final ? valor ) {");
                    builder.append("\n\t\t\t").append("this.descricao = descricao;");
                    builder.append("\n\t\t\t").append("this.valor = valor;");
                    builder.append("\n\t\t}");
                    builder.append("\n\n\t\t@Override");
                    builder.append("\n\t\tpublic String getDescricao() {");
                    builder.append("\n\t\t\treturn this.descricao;");
                    builder.append("\n\t\t}");
                    builder.append("\n\n\t\t@Override");
                    builder.append("\n\t\tpublic ? getValor() {");
                    builder.append("\n\t\t\treturn this.valor;");
                    builder.append("\n\t\t}");
                } else {
                    builder.append(",");
                }

                builder.append("\n\n");
            }

            builder.append("\n\t}");

            return builder.toString();

        }

    }





    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GERAR DTO.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class GerarDtoUI {

        public void run() {

            JTextField jtfAutor = new JTextField( converterCamelCase(System.getProperty("user.name"), true) );
            JTextField jtfNomeClasse = new JTextField("");
            JTextField jtfMotivo = new JTextField("Classe que representa informações...");

            JPanel jpnPanel1 = new JPanel(new GridLayout(0, 1));
            jpnPanel1.add(new JLabel("Autor:"));
            jpnPanel1.add(jtfAutor);
            jpnPanel1.add(new JLabel("Nome da Classe:"));
            jpnPanel1.add(jtfNomeClasse);
            jpnPanel1.add(new JLabel("Motivo:"));
            jpnPanel1.add(jtfMotivo);
            jpnPanel1.add(new JLabel("Definição:"));

            JTextArea jTextArea = new JTextArea(10,50);
            JScrollPane jScrollPane = new JScrollPane( jTextArea );

            JPanel jpnHelp = new JPanel(new GridLayout(0, 1));
            jpnHelp.add(new JLabel("Exemplo:         short id_empresa*,long id_cliente*,string razao_social"));
            jpnHelp.add(new JLabel("                                  * => Atributo identificador o objeto."));

            JPanel jpnPanel0 = new JPanel(new BorderLayout());
            jpnPanel0.add(BorderLayout.NORTH,jpnPanel1);
            jpnPanel0.add(BorderLayout.CENTER,jScrollPane);
            jpnPanel0.add(BorderLayout.SOUTH,jpnHelp);

            if ( JOptionPane.showConfirmDialog(null, jpnPanel0, "GERAR DTO:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION ) {
                gerar(
                    converterCamelCase( getSingular(jtfNomeClasse.getText()) , true),
                    jtfMotivo.getText(),
                    jtfAutor.getText(),
                    jTextArea.getText()
                );
            }

        }

        private void gerar(
            String nomeClasse,
            String motivo,
            String autor,
            String atributos
        ) {

            nomeClasse = getOrElse( nomeClasse, "None" );
            motivo = getOrElse( motivo, "..." );
            autor = getOrElse( autor, "None" );

            if ( isVazia( atributos ) ) {
                JOptionPane.showMessageDialog( null, "DEFINICAO ERRADA !!" );
                return;
            }

            StringBuilder codigo = new StringBuilder(  );
            codigo.append( "\n\n");
            codigo.append( "\nimport java.io.Serializable;" );
            codigo.append( "\nimport java.util.Objects;" );
            codigo.append( "\n\n");
            codigo.append( gerarJavaDoc( motivo, autor ) );
            codigo.append( "\npublic class ").append( nomeClasse ).append( " implements Serializable {\n" );

            StringBuilder scriptDeclaracaoVariaveis = new StringBuilder(  );
            StringBuilder scriptDeclaracaoArgumento = new StringBuilder(  );
            StringBuilder scriptCorpoConstrutor = new StringBuilder(  );
            StringBuilder scriptDeclaracaoGets = new StringBuilder(  );
            List<String> camposID = new ArrayList<>(  );
            StringBuilder scriptToString = new StringBuilder(  );

            scriptToString.append( "\n\n\t\treturn \"" ).append( nomeClasse ).append( " { " );
            boolean concatena = false;
            boolean informouID = atributos.indexOf( "*" ) > 0;

            for ( String declaracao : atributos.split(",") ) {

                List<Campo> campos = extrairCampo( declaracao );

                for ( Campo campo : campos ) {

                    scriptDeclaracaoVariaveis.append( "\n\t" ).append( campo.getDeclaracaoVariavelInstancia() );
                    scriptDeclaracaoArgumento.append( campo.getDeclaracaoArgumento() ).append( ",\n\t\t\t" );
                    scriptCorpoConstrutor.append( "\n\t\tthis." ).append( campo.getNomeSimples() ).append( " = " ).append( campo.getNomeSimples() ).append( ";" );
                    scriptDeclaracaoGets.append( "\n\t" ).append( campo.getDeclaracaoMetodoGet() ).append( "\n\n\t\treturn " ).append( campo.getNomeSimples() ).append( ";\n\t}\n" );

                    if ( !informouID ) {
                        campo.setId( true );
                        informouID = true;
                    }

                    if ( campo.getId() ) {

                        camposID.add( campo.getNomeGet() );

                        if ( !concatena ) {
                            scriptToString.append( campo.getDeclaracaoToString());
                            concatena = true;
                        } else {
                            scriptToString.append( " + \", " ).append( campo.getDeclaracaoToString() );
                        }
                    }
                }
            }

            if ( isVazia( scriptDeclaracaoVariaveis ) ) {
                JOptionPane.showMessageDialog( null, "DEFINICAO ERRADA !!" );
                return;
            }

            scriptDeclaracaoArgumento = retiraUltimosCaracteres( scriptDeclaracaoArgumento, 5 );

            codigo.append( scriptDeclaracaoVariaveis );
            codigo.append( "\n\n\tpublic " ).append( nomeClasse ).append( "( " ).append( scriptDeclaracaoArgumento ).append( " ) {\n" );
            codigo.append( scriptCorpoConstrutor ).append( "\n\t}\n" );
            codigo.append( scriptDeclaracaoGets );
            codigo.append( "\n\n" );

            codigo.append( criarComentario("EQUALS & HASHCODE.") );
            codigo.append( "\n\n\t@Override" );
            codigo.append( "\n\tpublic boolean equals( Object obj ) {" );
            codigo.append( "\n\n\t\tif ( this == obj ) return true;" );
            codigo.append( format( "\n\t\tif ( !( obj instanceof %s ) ) return false;", nomeClasse ) );
            codigo.append( format( "\n\t\t%s that = (%s) obj;", nomeClasse, nomeClasse ) );
            codigo.append( camposID
                            .stream()
                            .map( s -> format("Objects.equals( %s, that.%s )", s, s) )
                            .collect( joining(" &&\n\t\t\t", "\n\t\treturn ", ";" ) )
            );
            codigo.append( "\n\t}" );
            codigo.append( "\n\n\t@Override" );
            codigo.append( "\n\tpublic int hashCode() {" );
            codigo.append( camposID
                            .stream()
                            .collect( joining(", ", "\n\n\t\treturn Objects.hash( ", " );") )
            );
            codigo.append( "\n\t}" );
            codigo.append( "\n\n\t@Override" );
            codigo.append( "\n\tpublic String toString() {" );
            codigo.append( scriptToString ).append( " + \" }\";" );
            codigo.append( "\n\t}" );
            codigo.append( "\n\n\n" );

            codigo.append( criarComentario( "METODOS AUXILIARES" ) );
            codigo.append( "\n}" );
            codigo.append( "\n\n\n" );

            exibir( codigo.toString() );
        }
    }





    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GERAR DTO A PARTIR DE UMA QUERY.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class GerarDtoQueryUI {

        public void run() {

            JTextField jtfAutor = new JTextField( converterCamelCase(System.getProperty("user.name"), true) );
            JTextField jtfNomeClasse = new JTextField("");
            JTextField jtfMotivo = new JTextField("Classe que representa informações...");

            JPanel jpnPanel1 = new JPanel(new GridLayout(0, 1));
            jpnPanel1.add(new JLabel("Autor:"));
            jpnPanel1.add(jtfAutor);
            jpnPanel1.add(new JLabel("Nome da Classe:"));
            jpnPanel1.add(jtfNomeClasse);
            jpnPanel1.add(new JLabel("Motivo:"));
            jpnPanel1.add(jtfMotivo);
            JCheckBox jbcAnotacoes = new JCheckBox("Gerar anotações [ JSON ]");
            jbcAnotacoes.setSelected( false );
            jpnPanel1.add( jbcAnotacoes );
            jpnPanel1.add(new JLabel("Query:"));
            JTextArea jTextArea = new JTextArea(10,50);
            JScrollPane jScrollPane = new JScrollPane( jTextArea );

            JPanel jpnHelp = new JPanel(new GridLayout(0, 1));
            jpnHelp.add(new JLabel("Exemplo(s):    [select]       select * from admin.dbo.empresas where empresa = 1"));
            jpnHelp.add(new JLabel("                           [procedure]    fisco.dbo.up_esocial_funcionarios( 1, 2, '1900-01-01' ) "));

            JPanel jpnPanel0 = new JPanel(new BorderLayout());
            jpnPanel0.add(BorderLayout.NORTH,jpnPanel1);
            jpnPanel0.add(BorderLayout.CENTER,jScrollPane);
            jpnPanel0.add(BorderLayout.SOUTH,jpnHelp);

            if ( JOptionPane.showConfirmDialog(null, jpnPanel0, "GERAR DTO:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION )
                gerar(
                    converterCamelCase( getSingular(jtfNomeClasse.getText()) , true),
                    jtfMotivo.getText(),
                    jtfAutor.getText(),
                    jTextArea.getText(),
                    jbcAnotacoes.isSelected()
                );
        }

        private void gerar(
            String nomeClasse,
            String motivo,
            String autor,
            String query,
            boolean gerarAnotacoesJson
        ) {

            nomeClasse = isEmptyGet( nomeClasse, "None" );
            motivo = isEmptyGet( motivo, "..." );
            autor = isEmptyGet( autor, "None" );

            final List<Atributo> atributos = executarQuery(query);
            if ( isVazia(atributos)) return;

            final StringBuilder codigo = new StringBuilder();
            codigo.append( "\n" );
            codigo.append( "import java.io.Serializable;\n" );
            getImports(atributos).map(imp -> imp.concat("\n")).ifPresent(codigo::append);
            if ( gerarAnotacoesJson ) {
                codigo.append( "import com.fasterxml.jackson.annotation.JsonProperty;\n" );
                getImportsJson(atributos).map(imp -> imp.concat("\n")).ifPresent(codigo::append);
            }
            getStaticImports(atributos).map(imp -> "\n".concat(imp).concat("\n")).ifPresent(codigo::append);

            codigo.append( "\n\n" );
            codigo.append( gerarJavaDoc( motivo, autor ) );
            codigo.append( "\npublic class " ).append( nomeClasse ).append( " implements Serializable {\n" );
            codigo.append(
                atributos
                    .stream()
                    .map( atributo -> format("\n\tprivate final %s %s;", atributo.tipoDado, atributo.nomeAtributo ) )
                    .collect( joining() )
            );

            codigo.append( "\n\n\n" );
            codigo.append( criarComentario( "CONSTRUCTOR" ) );
            codigo.append( "\n\n\t@Deprecated" );
            codigo.append( format( "\n\tpublic %s(", nomeClasse ) );
            codigo.append(
                atributos
                .stream()
                .map( atributo -> format("\n\t\tfinal %s %s", atributo.getTipoDado(), atributo.nomeAtributo ) )
                .collect( joining(",") )
            );
            codigo.append( "\n\t) {\n\n" );
            codigo.append(
                atributos
                .stream()
                .map( atributo -> format( "\t\t%s;", atributo.getDefinicaoConstructor() ) )
                .collect( joining( "\n") )
            );
            codigo.append( "\n\t}\n\n\n" );

            codigo.append( criarComentario( "GETTERS" ).concat( "\n\n" ) );
            codigo.append(
                atributos
                .stream()
                .map( atributo -> {
                    final StringJoiner definicaoMetodo = new StringJoiner("\n");
                    if ( gerarAnotacoesJson ) {
                        definicaoMetodo.add( format( "\t@JsonProperty( \"%s\" )", atributo.nomeAtributo ) );
                        atributo
                            .getJsonSerializer()
                            .map( "\t"::concat )
                            .ifPresent(definicaoMetodo::add );
                    }
                    definicaoMetodo.add( format( "\tpublic %s %s {\n", atributo.tipoDado, atributo.getNomeMetodo() ) );
                    definicaoMetodo.add( format( "\t\treturn this.%s;", atributo.nomeAtributo ) );
                    definicaoMetodo.add( "\t}\n\n" );
                    return definicaoMetodo.toString();
                })
                .collect( joining() )
            );

            codigo.append( "\n" );
            codigo.append( criarComentario( "EQUALS & HASHCODE" ) );
            codigo.append( "\n\n\t\t!!!!!!   FAVOR GERAR EQUALS E HASCODE  !!!!!!!!\n\n" );
            codigo.append( "\n}\n\n\n" );

            codigo.append( "<named-native-query" );
            codigo.append( "\n\tname=\"Classe.nomeDoMetodo\"" );
            codigo.append( "\n\tresult-set-mapping=\"Classe.nomeDoMetodo\"" );
            codigo.append( "\n>" );
            codigo.append( "\n\t<query><![CDATA[\n\n" );
            codigo.append( query );
            codigo.append( "\n\n\t]]></query>\n</named-native-query>\n\n" );

            codigo.append( "<sql-result-set-mapping name=\"Classe.nomeDoMetodo\">" );
            codigo.append( format( "\n\t<constructor-result target-class=\"models.commons.dtos.%s\">\n", nomeClasse ) );
            codigo.append(
                atributos
                .stream()
                .map( Atributo::getDefinicaoXML )
                .map( xml -> format( "\t\t%s", xml) )
                .collect( joining( "\n") )
            );
            codigo.append( "\n\t</constructor-result>" );
            codigo.append( "\n</sql-result-set-mapping>\n\n\n" );

            exibir( codigo.toString() );
        }

        private Optional<String> getImports( final List<Atributo> atributos ) {
            final StringJoiner imports = new StringJoiner("\n");
            if ( atributos.stream().anyMatch(atributo -> equalsIgnoreCase(atributo.tipoDado,"BigDecimal") ) )
                imports.add( "import java.math.BigDecimal;" );
            if ( atributos.stream().anyMatch(atributo -> atributo.aplicarCastLocalDate) )
                imports.add( "import java.time.LocalDate;" );
            if ( atributos.stream().anyMatch(atributo -> atributo.aplicarCastLocalDateTime) )
                imports.add( "import java.time.LocalDateTime;" );
            if ( atributos.stream().anyMatch(atributo -> atributo.aplicarCastLocalTime) )
                imports.add( "import java.time.LocalTime;" );
            return isVazia(imports) ? empty() : of(imports.toString());
        }

        private Optional<String> getStaticImports( final List<Atributo> atributos ) {
            final StringJoiner imports = new StringJoiner("\n");
            if ( atributos.stream().anyMatch(atributo -> atributo.aplicarCastLocalDate) )
                imports.add( "import static infra.util.UtilDate.toLocalDate;" );
            if ( atributos.stream().anyMatch(atributo -> atributo.aplicarCastLocalDateTime) )
                imports.add( "import static infra.util.UtilDate.toLocalDateTime;" );
            if ( atributos.stream().anyMatch(atributo -> atributo.aplicarCastLocalTime) )
                imports.add( "import static infra.util.UtilDate.toLocalTime;" );
            return isVazia(imports) ? empty() : of(imports.toString());
        }

        private Optional<String> getImportsJson( final List<Atributo> atributos ) {
            final StringJoiner imports = new StringJoiner("\n");
            if ( atributos.stream().anyMatch(atributo -> atributo.aplicarCastLocalDate) )
                imports.add( "import infra.jsonDeserializer.LocalDateSerializer;" );
            if ( atributos.stream().anyMatch(atributo -> atributo.aplicarCastLocalDateTime) )
                imports.add( "import infra.jsonDeserializer.LocalDateTimeSerializer;" );
            if ( atributos.stream().anyMatch(atributo -> atributo.aplicarCastLocalTime) )
                imports.add( "import infra.jsonDeserializer.LocalTimeSerializer;" );
            if ( isVazia(imports) ) return empty();
            imports.add( "import com.fasterxml.jackson.databind.annotation.JsonSerialize;" );
            return of(imports.toString());
        }

        private List<Atributo> executarQuery( String query ) {

            if ( isVazia( query ) ) {
                JOptionPane.showMessageDialog( null, "INFORMAR QUERY !!" );
                return emptyList();
            }

            try (
                final Connection conn = db.getConnection();
                final Statement stmt = conn.createStatement()
            ) {

                final List<Atributo> atributos = new ArrayList<>();

                query = query.trim();
                if ( !query.toLowerCase().startsWith( "select" ) )
                    query = format( "{ call %s }", query );

                ResultSet rset = stmt.executeQuery( query );
                ResultSetMetaData rsmd = rset.getMetaData();
                int numColumns = rsmd.getColumnCount();
                for ( int i = 0; i < numColumns; i++ ) {
                    final int col = i + 1;
                    atributos.add(
                        new Atributo( rsmd.getColumnName(col), rsmd.getColumnTypeName(col) )
                    );
                }

                return atributos;

            } catch ( SQLException e ) {

                System.out.println( "\n\n=======================================================" );
                System.out.println( "======================= FALHA =========================" );
                System.out.println( "=======================================================" );
                System.out.println( query );
                System.out.println( e.getMessage() );
                System.out.println( "=======================================================" );
                System.out.println( "=======================================================\n\n" );
                return emptyList();
            }
        }

        private final class Atributo {

            String nomeColunaDb;
            String nomeAtributo;
            String tipoDado;
            String tipoDadoComplex;
            String tipoConstructor;
            boolean aplicarCastLocalDate;
            boolean aplicarCastLocalDateTime;
            boolean aplicarCastLocalTime;

            public Atributo(
                final String columnName,
                final String columnTypeName
            ) {

                this.nomeColunaDb = columnName;
                this.nomeAtributo = COLUNAS_ID.getOrDefault( this.nomeColunaDb, converterCamelCase( this.nomeColunaDb ) );
                this.tipoDado = getTipo( columnTypeName, true );
                this.tipoDadoComplex = getTipo( columnTypeName,false );

                switch ( tipoDado ) {
                    case "LocalTime":
                        this.tipoConstructor = "java.util.Date";
                        this.aplicarCastLocalTime = true;
                        break;
                    case "LocalDate":
                        this.tipoConstructor = "java.util.Date";
                        this.aplicarCastLocalDate = true;
                        break;
                    case "LocalDateTime":
                        this.tipoConstructor = "java.util.Date";
                        this.aplicarCastLocalDateTime = true;
                        break;
                    default:
                        this.tipoConstructor = tipoDado;
                }

                if ( contem( columnName,"cpf", "cnpj", "cgc", "cnpj_local", "conselho_regional_inscricao", "pis_pasep", "habilitacao" ) ) {
                    tipoDado = "Long";
                    tipoConstructor = "Long";
                }
            }

            String getTipoDado() {
                return isUtilDate() ? "java.util.Date" : tipoDado;
            }

            String getNomeMetodo() {
                return format( "get%s()", converterCamelCase(nomeAtributo, true));
            }

            boolean isUtilDate() {
                return aplicarCastLocalDate || aplicarCastLocalDateTime || aplicarCastLocalTime;
            }

            String getDefinicaoConstructor() {
                if ( aplicarCastLocalDate )
                    return format( "this.%s = toLocalDate( %s )", nomeAtributo, nomeAtributo );
                if ( aplicarCastLocalDateTime )
                    return format( "this.%s = toLocalDateTime( %s )", nomeAtributo, nomeAtributo );
                if ( aplicarCastLocalTime )
                    return format( "this.%s = toLocalTime( %s )", nomeAtributo, nomeAtributo );
                return format( "this.%s = %s", nomeAtributo, nomeAtributo );
            }

            String getDefinicaoXML() {
                return isUtilDate()
                    ? format( "<column name=\"%s\" class=\"java.util.Date\"/>", nomeColunaDb )
                    : format( "<column name=\"%s\" class=\"%s\"/>", nomeColunaDb, tipoDadoComplex );
            }

            Optional<String> getJsonSerializer() {
                if ( aplicarCastLocalDate ) return of( "@JsonSerialize( using = LocalDateSerializer.class )" );
                if ( aplicarCastLocalDateTime ) return of( "@JsonSerialize( using = LocalDateTimeSerializer.class )" );
                if ( aplicarCastLocalTime ) return of( "@JsonSerialize( using = LocalTimeSerializer.class )" );
                return empty();
            }
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GERAR CONSTANTE.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class GerarConstanteUI {

        public void run() {

            JTextField jtfAutor = new JTextField( converterCamelCase(System.getProperty("user.name"), true) );
            JTextField jtfNomeEnumerado = new JTextField("");
            JCheckBox jcbConverter = new JCheckBox("Gerar Converter");

            JPanel jpnPanel1 = new JPanel(new GridLayout(0, 1));
            jpnPanel1.add(new JLabel("Autor:"));
            jpnPanel1.add(jtfAutor);
            jpnPanel1.add(new JLabel("Nome do Enumerado:"));
            jpnPanel1.add(jtfNomeEnumerado);
            jpnPanel1.add(jcbConverter);
            jpnPanel1.add(new JLabel("Definição:"));

            JTextArea jTextArea = new JTextArea(10,50);
            JScrollPane jScrollPane = new JScrollPane(jTextArea);

            JPanel jpnHelp = new JPanel(new GridLayout(0, 1));
            jpnHelp.add(new JLabel("Exemplo:   AV.      Avenida"));
            jpnHelp.add(new JLabel("                    RUA     Rua"));
            jpnHelp.add(new JLabel("                    TVA     Travessa"));

            JPanel jpnPanel0 = new JPanel(new BorderLayout());
            jpnPanel0.add(BorderLayout.NORTH,jpnPanel1);
            jpnPanel0.add(BorderLayout.CENTER,jScrollPane);
            jpnPanel0.add(BorderLayout.SOUTH,jpnHelp);

            if ( JOptionPane.showConfirmDialog(null, jpnPanel0, "GERAR CONSTANTE:", OK_CANCEL_OPTION, PLAIN_MESSAGE) == OK_OPTION )
                gerar( jtfAutor.getText(),
                        converterCamelCase( getSingular(jtfNomeEnumerado.getText()) , true),
                        jTextArea.getText(),
                        jcbConverter.isSelected() );

        }

        private void gerar(
            final String autor,
            final String nomeEnumerado,
            final String definicao,
            final boolean gerarConverter
        ) {

            Map<String,String> map = new LinkedHashMap<>( );
            for ( String linha : definicao.split("\n") ) {

                StringBuilder key = new StringBuilder();
                StringBuilder descricao = new StringBuilder();
                boolean ehDescricao = false;

                for ( int i = 0; i < linha.length(); i++ ) {

                    char c = linha.charAt( i );

                    if ( i == 0 ) {
                        key.append( c );
                        continue;
                    }

                    if ( isWhitespace( c ) && !ehDescricao ) {
                        ehDescricao = true;
                        continue;
                    }

                    if ( ehDescricao ) {
                        descricao.append( c );
                        continue;
                    }

                    key.append( c );

                }

                map.put( key.toString().trim().toUpperCase(), descricao.toString().trim().toUpperCase() );

            }


            final boolean ehNumero = apenasNumero( map.keySet().iterator().next() );
            final String definicaoTipo = (ehNumero ? "Short" : "String");

            StringBuilder codigo = new StringBuilder();

            codigo.append( "\n\n\nimport infra.model.Constante;" );
            codigo.append( "\n\n/**");
            codigo.append( "\n");
            codigo.append( " * Constante ref. ao conjunto de valores de ...");
            codigo.append( "\n");
            codigo.append( " *");
            codigo.append( "\n");
            codigo.append( " * <p>Autor: ").append(autor).append("</p>");
            codigo.append( "\n");
            codigo.append( " *");
            codigo.append( "\n");
            codigo.append( " * @since ").append(getHoraLocal());
            codigo.append( "\n");
            codigo.append( " */");
            codigo.append( "\n");
            codigo.append( "public enum ").append(nomeEnumerado).append(" implements Constante<").append(definicaoTipo).append("> {");
            codigo.append( "\n\n");

            Set<String> dados = map.keySet();
            for ( Iterator<String> it = dados.iterator() ; it.hasNext() ;  ) {

                String valor = it.next();
                String descricao = map.get( valor );

                codigo.append( "\t/**" );
                codigo.append( "\n\t * " );
                codigo.append( ehNumero ? valor : "\"" + valor + "\""  );
                codigo.append( "\n\t */" );

                final String declaracao = format("%s( \"%s\", %s )",
                    removerAcentosECaracteresEspeciais( descricao )
                    .replaceAll("\"", "_")
                    .replaceAll("/", "_")
                    .replaceAll(" ", "_")
                    .replaceAll( "-", "_" ),
                    descricao,
                    ehNumero ? "(short) " + removerAcentosECaracteresEspeciais(valor) : "\"" + removerAcentosECaracteresEspeciais(valor) + "\""
                ) + ( it.hasNext() ? "," : "\n\t;" );

                codigo.append( "\n\t" );
                codigo.append( declaracao );
                codigo.append( "\n\n" );
            }

            codigo.append( "\t" ).append( "private final String descricao;");
            codigo.append( "\n\t" ).append( "private final " ).append(definicaoTipo).append( " valor;" );

            codigo.append( "\n\n\t").append( nomeEnumerado ).append( "(");
            codigo.append( "\n\t\tfinal String descricao," );
            codigo.append( "\n\t\t" ).append( "final " ).append( definicaoTipo ).append( " valor");
            codigo.append( "\n\t) {");
            codigo.append( "\n\n\t\tthis.descricao = descricao;" );
            codigo.append( "\n\t\tthis.valor = valor;" );
            codigo.append( "\n\t}" );
            codigo.append( "\n\n\t@Override" );
            codigo.append( "\n\tpublic String getDescricao() {" );
            codigo.append( "\n\n\t\treturn this.descricao;" );
            codigo.append( "\n\t}" );
            codigo.append( "\n\n\t@Override" );
            codigo.append( "\n\tpublic ").append(definicaoTipo).append( " getValor() {" );
            codigo.append( "\n\n\t\treturn this.valor;" );
            codigo.append( "\n\t}" );
            codigo.append( "\n\n}" );

            if ( gerarConverter ) {

                codigo.append( "\n\n\nimport models.commons.constantes." ).append( nomeEnumerado ).append( ";" );
                codigo.append( "\nimport javax.persistence.AttributeConverter;" );
                codigo.append( "\nimport javax.persistence.Converter;" );
                codigo.append( ehNumero ?  "\nimport static infra.util.UtilConstante.getValorInteger;" : "\nimport static infra.util.UtilConstante.getValor;" );
                codigo.append( "\nimport static infra.util.UtilEnum.getEnum;" );
                if ( ehNumero ) codigo.append( "\nimport static infra.util.UtilNumero.toShort;" );
                codigo.append( "\n\n/**");
                codigo.append( "\n * Classe converter para constante ").append( nomeEnumerado );
                codigo.append( "\n *");
                codigo.append( "\n * <p>Autor: ").append(autor).append("</p>");
                codigo.append( "\n *");
                codigo.append( "\n * @since ").append(getHoraLocal());
                codigo.append( "\n *");
                codigo.append( "\n * @see models.commons.constantes.").append(nomeEnumerado);
                codigo.append( "\n */");
                codigo.append( "\n@Converter");
                codigo.append( "\npublic class ").append(nomeEnumerado).append("Converter implements AttributeConverter<").append(nomeEnumerado).append(",").append( ehNumero ? "Integer" : definicaoTipo).append("> {");
                codigo.append( "\n\n");
                codigo.append( "\t@Override");
                final String descArgumento = nomeEnumerado.substring( 0, 1 ).toLowerCase() + nomeEnumerado.substring( 1 );
                codigo.append( format( "\n\tpublic %s convertToDatabaseColumn( final %s %s ) {\n", ehNumero ? "Integer" : definicaoTipo, nomeEnumerado, descArgumento  ) );
                if ( ehNumero )
                    codigo.append( format( "\n\t\treturn getValorInteger( %s );", descArgumento ) );
                else
                    codigo.append( format( "\n\t\treturn getValor( %s );", descArgumento ) );
                codigo.append( "\n\t}\n");
                codigo.append( "\n\t@Override");
                codigo.append( "\n\tpublic ").append(nomeEnumerado).append(" convertToEntityAttribute( final ").append( ehNumero ? "Integer" : definicaoTipo ).append( " valor ) {" );
                if ( ehNumero )
                    codigo.append( "\n\n\t\treturn getEnum( ").append(nomeEnumerado).append( ".class, toShort(valor) );" );
                else
                    codigo.append( "\n\n\t\treturn getEnum( ").append(nomeEnumerado).append( ".class, valor );" );
                codigo.append( "\n\t}");
                codigo.append( "\n}");
                codigo.append( "\n\n\n" );
                codigo.append( format( "//  @Convert( converter = %sConverter.class )", nomeEnumerado ) );
            }

            codigo.append( "\n\n" );

            exibir( codigo.toString() );
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private StringBuilder retiraUltimosCaracteres(
        final StringBuilder builder,
        final int qtde
    ) {

        int tam = builder.length();
        return builder.delete( (tam - qtde), tam  );
    }

    private enum TipoChave {

        SIMPLES, COMPOSTA, SEM_CHAVE;
    }

    private boolean isNumeric( String tipo ) {

        return "numeric".contains(tipo) || "decimal".contains(tipo) || "money".contains(tipo);
    }

    private boolean isTexto( String tipo ) {

        return "varchar".contains(tipo) || "char".contains(tipo) || "text".contains(tipo);
    }

    private boolean isDate( String tipo ) {

        return "date".contains(tipo);
    }

    private boolean isDateTime( String tipo ) {

        return "smalldatetime".contains(tipo) || "datetime".contains(tipo);
    }

    private boolean isTime( String tipo ) {

        return "time".equals(tipo);
    }

    private void exibir( String script ) {

        System.out.println(script);

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection str = new StringSelection( script );
        clipboard.setContents( str, null );
    }

    private String getHoraLocal() {

        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    private String getSingular( String texto ) {

        if ( isVazia(texto) )
            return texto;

        if ( texto.endsWith("oes") ) {
            texto = texto.substring(0,texto.length() - 3);
            return texto + "ao";
        }

        if ( texto.endsWith("s") )
            return texto.substring(0,texto.length() - 1);

        return texto;

    }

    private String criarComentario(String comentario) {

        StringBuilder builder = new StringBuilder();
        builder.append("\t///////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        builder.append("\n\t//");
        builder.append("\n\t// ").append(comentario);
        builder.append("\n\t//");
        builder.append("\n\t///////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        return builder.toString();
    }

    public class Campo {

        private final String tipo;
        private final String nome;
        private final String nomeSimples;
        private final String iptName;
        private final String iptType;
        private final String nomeGet;
        private Boolean id;
        private final String declaracaoArgumento;
        private final String declaracaoVariavelInstancia;
        private final String declaracaoMetodoGet;
        private final String declaracaoEquals;
        private final String declaracaoHashCode;
        private final String declaracaoToString;

        private Campo(
            final String dataType,
            final String nomeDoAtributo
        ) {


            this.nome = nomeDoAtributo.trim();

            // HTML...
            this.iptName = "ipt" + converterCamelCase( nomeDoAtributo, true );

            // ID.
            this.id = nomeDoAtributo.lastIndexOf( "*" ) > 0;

            // Nome simples.
            final String atributo = id ? nomeDoAtributo.substring( 0, nomeDoAtributo.length() -1 ) : nomeDoAtributo;
            this.nomeSimples = converterCamelCase( atributo );

            final String dataTypeTmp = dataType.trim().toLowerCase();

            if ( dataTypeTmp.contains( "string" ) || dataTypeTmp.contains( "text" ) || dataTypeTmp.contains( "char" ) ) {

                this.iptType = "text";
                this.tipo = "String";

                this.declaracaoArgumento = "final String " + converterCamelCase( atributo );
                this.declaracaoVariavelInstancia = "private " + this.declaracaoArgumento + ";";

                this.nomeGet = "get" + converterCamelCase( atributo, true ) + "()";
                this.declaracaoMetodoGet = "public String " + nomeGet  + " {";
                this.declaracaoEquals = "if ( " + nomeGet + " != null ? !" + nomeGet + ".equals( that." + nomeGet + " ) : that." + nomeGet + " != null ) return false;";
                this.declaracaoHashCode = "( " + nomeGet + " != null ? " + nomeGet + ".hashCode() : 0 )";
                this.declaracaoToString = nomeSimples + " = '\" + " + nomeSimples + " + \"'\"";

            } else if ( dataTypeTmp.contains( "int" )
                    || dataTypeTmp.contains( "long" )
                    || dataTypeTmp.contains( "short" )
                    || dataTypeTmp.contains( "number" )
                    || dataTypeTmp.contains( "bigdecimal" ) )  {

                this.iptType = "number";
                this.tipo =  dataTypeTmp.equals( "bigdecimal" ) ? "BigDecimal" : toUpperCase( dataTypeTmp, 0 );

                this.declaracaoArgumento = "final " + this.tipo + " " + converterCamelCase( atributo );
                this.declaracaoVariavelInstancia = "private " + this.declaracaoArgumento + ";";

                this.nomeGet = "get" + converterCamelCase( atributo, true ) + "()";
                this.declaracaoMetodoGet = "public " + this.tipo + " " + nomeGet + " {";
                this.declaracaoEquals = "if ( " + nomeGet + " != null ? !" + nomeGet + ".equals( that." + nomeGet + " ) : that." + nomeGet + " != null ) return false;";
                this.declaracaoHashCode = "( " + nomeGet + " != null ? " + nomeGet + ".hashCode() : 0 )";
                this.declaracaoToString =  nomeSimples + " = \" + " + nomeSimples;

            } else if ( dataTypeTmp.equals( "localdate" ) ) {

                this.iptType =  "localdate";
                this.tipo = "LocalDate";

                this.declaracaoArgumento = "final LocalDate " + converterCamelCase( atributo );
                this.declaracaoVariavelInstancia = "private " + this.declaracaoArgumento + ";";

                this.nomeGet = "get" + converterCamelCase( atributo, true ) + "()";
                this.declaracaoMetodoGet = "public LocalDate " + nomeGet  + " {";
                this.declaracaoEquals = "if ( " + nomeGet + " != null ? !" + nomeGet + ".equals( that." + nomeGet + " ) : that." + nomeGet + " != null ) return false;";
                this.declaracaoHashCode = "( " + nomeGet + " != null ? " + nomeGet + ".hashCode() : 0 )";
                this.declaracaoToString =  nomeSimples + " = \" + " + nomeSimples;

            } else if ( dataTypeTmp.equals( "localdatetime" ) ) {

                this.iptType =  "localdatetime";
                this.tipo = "LocalDateTime";

                this.declaracaoArgumento = "final LocalDateTime " + converterCamelCase( atributo );
                this.declaracaoVariavelInstancia = "private " + this.declaracaoArgumento + ";";

                this.nomeGet = "get" + converterCamelCase( atributo, true ) + "()";
                this.declaracaoMetodoGet = "public LocalDateTime " + nomeGet  + " {";
                this.declaracaoEquals = "if ( " + nomeGet + " != null ? !" + nomeGet + ".equals( that." + nomeGet + " ) : that." + nomeGet + " != null ) return false;";
                this.declaracaoHashCode = "( " + nomeGet + " != null ? " + nomeGet + ".hashCode() : 0 )";
                this.declaracaoToString =  nomeSimples + " = \" + " + nomeSimples;

            } else if ( dataTypeTmp.contains( "boolean" ) ) {

                this.iptType =  "boolean";
                this.tipo = "Boolean";

                this.declaracaoArgumento = "final Boolean " + converterCamelCase( atributo );
                this.declaracaoVariavelInstancia = "private " + this.declaracaoArgumento + ";";

                this.nomeGet = "is" + converterCamelCase( atributo, true ) + "()";
                this.declaracaoMetodoGet = "public Boolean " + nomeGet  + " {";
                this.declaracaoEquals = "if ( " + nomeGet + " != null ? !" + nomeGet + ".equals( that." + nomeGet + " ) : that." + nomeGet + " != null ) return false;";
                this.declaracaoHashCode = "( " + nomeGet + " != null ? " + nomeGet + ".hashCode() : 0 )";
                this.declaracaoToString =  nomeSimples + " = \" + " + nomeSimples;

            } else {

                this.iptType = "text";
                this.tipo = "?";

                this.declaracaoArgumento = "final ? " + converterCamelCase( atributo );
                this.declaracaoVariavelInstancia = "private " + this.declaracaoArgumento + ";";

                this.nomeGet = "get" + converterCamelCase( atributo, true ) + "()";
                this.declaracaoMetodoGet = "public Boolean " + nomeGet  + " {";
                this.declaracaoEquals = "if ( " + nomeGet + " != null ? !" + nomeGet + ".equals( that." + nomeGet + " ) : that." + nomeGet + " != null ) return false;";
                this.declaracaoHashCode = "( " + nomeGet + " != null ? " + nomeGet + ".hashCode() : 0 )";
                this.declaracaoToString = nomeSimples + " = \" + " + nomeSimples;

            }

        }

        public void setId( Boolean id ) {
            this.id = id;
        }

        public String getTipo() {
            return tipo;
        }

        public String getNome() {
            return nome;
        }

        public String getNomeSimples() {
            return nomeSimples;
        }

        public String getIptName() {
            return iptName;
        }

        public String getIptType() {
            return iptType;
        }

        public String getNomeGet() {
            return nomeGet;
        }

        public String getDeclaracaoMetodoGet() {
            return declaracaoMetodoGet;
        }

        public String getDeclaracaoVariavelInstancia() {
            return declaracaoVariavelInstancia;
        }

        public String getDeclaracaoEquals() {
            return declaracaoEquals;
        }

        public Boolean getId() {
            return id;
        }

        public String getDeclaracaoArgumento() {
            return declaracaoArgumento;
        }

        public String getDeclaracaoHashCode() {
            return declaracaoHashCode;
        }

        public String getDeclaracaoToString() {
            return declaracaoToString;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            Campo campo = ( Campo ) o;
            if ( getTipo() != null ? !getTipo().equals( campo.getTipo() ) : campo.getTipo() != null ) return false;
            return !( nome != null ? !nome.equals( campo.nome ) : campo.nome != null );
        }

        @Override
        public int hashCode() {
            int result = getTipo() != null ? getTipo().hashCode() : 0;
            result = 31 * result + ( nome != null ? nome.hashCode() : 0 );
            return result;
        }
    }

    private List<Campo> extrairCampo( String value ) {

        int pos = 0;
        java.util.List<Campo> dados = new ArrayList<>();

        for ( String input : value.split( "," ) ) {
            input = input.trim();
            pos = input.lastIndexOf( ' ' );
            if ( pos > 0 )
                dados.add( new Campo( input.substring( 0, pos ).trim(), input.substring( pos ).trim()  ) );
        }

        return dados;
    }

    private String getTipo(
        final String tipo,
        boolean nomeSimples
    ) {

        if ( isTexto(tipo) )
            return nomeSimples ? "String" : "java.lang.String";

        if ( "numeric(".contains(tipo) )
            return nomeSimples ? "BigDecimal" : "java.math.BigDecimal";

        if ( "int".contains(tipo) || "int identity".contains(tipo) || "numeric identity".contains(tipo) )
            return nomeSimples ? "Long" : "java.lang.Long";

        if ( "tinyint".contains(tipo) || "smallint".contains(tipo))
            return nomeSimples ? "Short" : "java.lang.Short";

        if ( isTime(tipo) )
            return nomeSimples ? "LocalTime" : "java.time.LocalTime";

        if ( isDate(tipo) )
            return nomeSimples ? "LocalDate" : "java.time.LocalDate";

        if ( isDateTime(tipo) )
            return nomeSimples ? "LocalDateTime" : "java.time.LocalDateTime";

        if ( isNumeric(tipo) )
            return nomeSimples ? "BigDecimal" : "java.math.BigDecimal";

        if ( "image".contains(tipo) )
            return nomeSimples ? "Blob" : "java.lang.Blob";

        if  ( "varbinary".contains(tipo) )
            return nomeSimples ? "Blob" : "java.sql.Blob";

        System.out.printf("\n>>>>>>>>>>>>>>>>> TIPO [%s] NAO ESTA SENDO TRATADO !! <<<<<<<<<<<<<<<<<<<<<\n", tipo);

        return "?";
    }

    private String gerarJavaDoc(
        final String motivo,
        final String autor
    ) {

        final StringBuilder builder = new StringBuilder();
        builder.append("/**");
        builder.append("\n");
        builder.append(" * ").append(motivo);
        builder.append("\n");
        builder.append(" *");
        builder.append("\n * <p>Autor: ").append(autor).append("</p>");
        builder.append("\n *");
        builder.append("\n * @since ").append(getHoraLocal());
        builder.append("\n */");
        return builder.toString();
    }

    private String traduzNomeColuna( final String coluna ) {

        return COLUNAS_ID
        .entrySet()
        .stream()
        .filter( value -> coluna.startsWith(value.getKey()))
        .findFirst()
        .map( m -> "id_".concat(coluna) )
        .orElse( coluna );
    }

    private interface Columns {

        String getAtributos();
        String getMetodos();

        static Columns newInstance( final String atributos,
                                    final String metodos ) {

            return new Columns() {
                @Override
                public String getAtributos() {
                    return atributos;
                }

                @Override
                public String getMetodos() {
                    return metodos;
                }
            };
        }
    }


}
