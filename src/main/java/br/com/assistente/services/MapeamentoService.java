package br.com.assistente.services;

import br.com.assistente.infra.exceptions.BusinessException;
import br.com.assistente.models.Modelo;
import br.com.assistente.models.ModeloCampo;
import br.com.assistente.models.SetupCnxBanco;
import br.com.assistente.models.SetupUsuario;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADERS;

public class MapeamentoService {

    public List<String> buscarBancos() {

        List<String> bancos = new ArrayList<>();
        bancos.add("vendas");
        bancos.add("move");
        bancos.add("estoque");
        bancos.add("admin");
        bancos.add("seguranca");
        return bancos;
    }

    public List<ModeloCampo> extrair(
        final String banco,
        final String owner,
        final String tabela
    ) throws BusinessException {

        if ( isBlank(banco) || isBlank(owner) || isBlank(tabela) )
            throw new BusinessException( "É necessário informar banco / owner / tabela" );

        final SetupCnxBanco setupCnxBanco = SetupUsuario.find()
            .flatMap( su -> SetupCnxBanco.findById(su.getIdCnxAtual()) )
            .orElseThrow( () -> new BusinessException("Não localizou conexão ativa") );





//        ConexaoDB.execQuery( "SELECT * FROM perfis", dados -> {
//            for (Object dado : dados) {
//                System.out.println(dado);
//            }
//        });


//        try {
//            ConexaoDB.conectar();
//            MapListHandler beanListHandler = new MapListHandler();
//            QueryRunner runner = new QueryRunner();
//
//            ResultSetHandler<Object[]> handler = new ResultSetHandler<Object[]>() {
//                public Object[] handle(ResultSet rs) throws SQLException {
//                    if (!rs.next()) {
//                        return null;
//                    }
//                    ResultSetMetaData meta = rs.getMetaData();
//                    int cols = meta.getColumnCount();
//                    Object[] result = new Object[cols];
//
//                    for (int i = 0; i < cols; i++) {
//                        result[i] = rs.getObject(i + 1);
//                    }
//                    return result;
//                }
//            };
//
//            Object[] result  = runner.query(ConexaoDB.getConnection(), "SELECT * FROM perfis", handler );
//            System.out.print("Result: " + Arrays.toString(result));
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            ConexaoDB.desconnectar();
//        }

        final String nomeCompletoTabela = format( "%s.%s.%s", banco, owner, tabela );

        List<ModeloCampo> dados = new ArrayList<>();

        dados.add( new ModeloCampo.Builder().comPK(true).comColunaDB("id").comTipoDB("integer").comTipoJava("Long").build() );
        dados.add( new ModeloCampo.Builder().comPK(false).comColunaDB("razao_social").comTipoDB("integer").comTipoJava("Long").comConverter(true).build() );


        return dados;
    }

    public void executar( final Modelo modelo ) {

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty( RESOURCE_LOADERS, "classpath" );
        engine.setProperty( "resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        engine.init();

        final Template template = engine.getTemplate("/mapeamento/template.vm");

//        VelocityContext context = new VelocityContext();
//        context.put("nomeAutor", SetupUsuarioRepository.find().map(SetupUsuario::getAutor).orElse("?") );
//        context.put("modelo", modelo);
//        context.put("StringUtils", StringUtils.class);
//
//        try {
//            final StringWriter writer = new StringWriter();
//            template.merge( context, writer );
//            System.out.println(writer.toString());
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
