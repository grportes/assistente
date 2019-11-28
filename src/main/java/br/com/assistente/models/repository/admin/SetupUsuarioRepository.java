package br.com.assistente.models.repository.admin;

import br.com.assistente.infra.exceptions.PersistenceException;
import br.com.assistente.infra.util.UtilYaml;
import br.com.assistente.models.domains.admin.SetupCnxBanco;
import br.com.assistente.models.domains.admin.SetupUsuario;

import java.nio.file.Path;
import java.util.*;

import static br.com.assistente.infra.util.UtilArquivo.buscarPastaAplicacao;
import static br.com.assistente.models.domains.admin.SetupCnxBanco.mesmosValores;
import static br.com.assistente.models.domains.admin.SetupCnxBanco.validarObjeto;
import static br.com.assistente.models.domains.admin.SetupUsuario.validarObjeto;
import static java.nio.file.Files.notExists;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class SetupUsuarioRepository {

    private static SetupUsuario setupUsuario;

    public static SetupUsuario getSetupUsuario() {

        return setupUsuario;
    }

    public static boolean isCnxDBInformada() {

        return nonNull( setupUsuario ) && nonNull( setupUsuario.getIdCnxAtual() );
    }

    public static Path getPathSetup()  {

        final Path pasta = buscarPastaAplicacao();
        if ( notExists(pasta) )
            throw new PersistenceException( "Não foi possivel localizar pasta do aplicativo" );

        return pasta;
    }

    public static Path getArquivoYaml()  {

        return getPathSetup().resolve( "assistente.yml" );
    }

    public static void load() {

        find().ifPresent(s -> setupUsuario = s);
    }

    public static Optional<SetupUsuario> find() {

        final Path arquivoYaml = getArquivoYaml();
        return UtilYaml.load( SetupUsuario.class, arquivoYaml );
    }

    public static void save( final SetupUsuario novoSetup ) {

        validarObjeto( novoSetup );

        final Path arquivoYaml = getArquivoYaml();
        final SetupUsuario setup = UtilYaml.load( SetupUsuario.class, arquivoYaml ).orElseGet( SetupUsuario::new );

        setup.setAutor( novoSetup.getAutor() );
        setup.setIdCnxAtual( novoSetup.getIdCnxAtual() );
        setup.setLocalProjeto( novoSetup.getLocalProjeto() );

        UtilYaml.dump( setup, arquivoYaml );
        setupUsuario = setup;
    }

    public static void save( final SetupCnxBanco novaCnx ) {

        validarObjeto( novaCnx );

        final Path arquivoYaml = getArquivoYaml();
        final SetupUsuario setup = UtilYaml.load( SetupUsuario.class, arquivoYaml ).orElseGet( SetupUsuario::new );

        List<SetupCnxBanco> cnxs = setup.getConexoesDisponiveis();
        if ( isNotEmpty( cnxs ) ) {
            boolean update = false;
            for ( final SetupCnxBanco cnx : cnxs ) {
                if ( Objects.equals(cnx, novaCnx) ) {
                    if ( mesmosValores(cnx, novaCnx) ) return;
                    cnx.setUserName( novaCnx.getUserName() );
                    cnx.setPassword( novaCnx.getPassword() );
                    cnx.setPorta( novaCnx.getPorta() );
                    update = true;
                }
            }
            if ( !update ) {
                novaCnx.setId( nextInt() );
                cnxs.add( novaCnx );
            }
        } else {
            cnxs = new ArrayList<>();
            novaCnx.setId( nextInt() );
            cnxs.add( novaCnx );
            setup.setConexoesDisponiveis(cnxs);
        }

        UtilYaml.dump( setup, arquivoYaml );
        setupUsuario = setup;
    }

    public static List<SetupCnxBanco> buscarCnxsDisponiveis() {

        return find().map( SetupUsuario::getConexoesDisponiveis ).orElse( emptyList() );
    }

    public static Optional<SetupCnxBanco> findByIdCnxBanco(Integer idCnxBancoSelecionada) {

        return find()
            .map( SetupUsuario::getConexoesDisponiveis )
            .orElseGet( Collections::emptyList )
            .stream()
            .filter( cnx -> Objects.equals( cnx.getId(), idCnxBancoSelecionada ) )
            .findFirst();
    }
}
