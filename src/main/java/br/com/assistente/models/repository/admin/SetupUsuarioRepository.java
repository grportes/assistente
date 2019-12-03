package br.com.assistente.models.repository.admin;

import br.com.assistente.infra.exceptions.PersistenceException;
import br.com.assistente.infra.util.UtilYaml;
import br.com.assistente.models.domains.admin.SetupCnxBanco;
import br.com.assistente.models.domains.admin.SetupUsuario;
import org.apache.commons.collections4.CollectionUtils;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.assistente.infra.util.UtilArquivo.buscarPastaAplicacao;
import static br.com.assistente.models.domains.admin.SetupCnxBanco.mesmosValores;
import static br.com.assistente.models.domains.admin.SetupCnxBanco.validarObjeto;
import static br.com.assistente.models.domains.admin.SetupUsuario.validarObjeto;
import static java.nio.file.Files.notExists;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class SetupUsuarioRepository {

    private static SetupUsuario cache;

    public static SetupUsuario getSetupUsuario() {

        return cache;
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

        find().ifPresent(s -> cache = s);
    }

    public static Optional<SetupUsuario> find() {

        final Path arquivoYaml = getArquivoYaml();
        return UtilYaml.load( SetupUsuario.class, arquivoYaml );
    }

    public static void save( final SetupUsuario novoSetup ) {

        validarObjeto( novoSetup );

        final Path arquivoYaml = getArquivoYaml();

        final SetupUsuario setupUsuario = UtilYaml.load( SetupUsuario.class, arquivoYaml ).orElseGet( SetupUsuario::new );
        setupUsuario.setAutor( novoSetup.getAutor() );
        setupUsuario.setIdCnxAtual( novoSetup.getIdCnxAtual() );
        setupUsuario.setLocalProjeto( novoSetup.getLocalProjeto() );

        UtilYaml.dump( setupUsuario, arquivoYaml );
        cache = setupUsuario;
    }

    public static void deleteCnxById( final Integer id ) {

        final Path arquivoYaml = getArquivoYaml();

        UtilYaml.load( SetupUsuario.class, arquivoYaml ).ifPresent( setupUsuario -> {
            final List<SetupCnxBanco> cnxs = setupUsuario.getConexoesDisponiveis();
            if ( isNotEmpty(cnxs) ) {
                cnxs.removeIf( cnx -> Objects.equals( cnx.getId(), id ) );
                UtilYaml.dump( setupUsuario, arquivoYaml );
                cache = setupUsuario;
            }

        });
    }

    public static int save( final SetupCnxBanco novaCnx ) {

        validarObjeto( novaCnx );

        final AtomicInteger idSetupCnx = new AtomicInteger(0);

        final Path arquivoYaml = getArquivoYaml();

        final SetupUsuario setup = UtilYaml
            .load( SetupUsuario.class, arquivoYaml )
            .map( setupUsuarioAtual -> {

                final List<SetupCnxBanco> cnxs = setupUsuarioAtual.getConexoesDisponiveis();

                if ( isEmpty(cnxs) ) {
                    // Primeira conexão...
                    idSetupCnx.set( nextInt() );
                    novaCnx.setId( idSetupCnx.get() );
                    cache.setConexoesDisponiveis( singletonList(novaCnx) );
                    return setupUsuarioAtual;
                }

                // Update...
                for ( final SetupCnxBanco cnx : cnxs ) {
                    if ( !Objects.equals( cnx, novaCnx ) ) continue;
                    idSetupCnx.set( cnx.getId() );
                    if ( !mesmosValores( cnx, novaCnx ) ) {
                        cnx.setFornecedorDB( novaCnx.getFornecedorDB() );
                        cnx.setUrl( novaCnx.getUrl() );
                        cnx.setPorta( novaCnx.getPorta() );
                        cnx.setUserName( novaCnx.getUserName() );
                        cnx.setPassword( novaCnx.getPassword() );
                    }
                    return setupUsuarioAtual;
                }

                //  Nova conexão.
                idSetupCnx.set( nextInt() );
                novaCnx.setId( idSetupCnx.get() );
                cnxs.add( novaCnx );
                return setupUsuarioAtual;
            })
            .orElseGet(() -> {
                idSetupCnx.set( nextInt() );
                novaCnx.setId( idSetupCnx.get() );

                final SetupUsuario setupUsuario = new SetupUsuario();
                setupUsuario.setIdCnxAtual( idSetupCnx.get() );
                setupUsuario.setConexoesDisponiveis( singletonList(novaCnx) );
                return setupUsuario;
            });

        UtilYaml.dump( setup, arquivoYaml );
        cache = setup;
        return idSetupCnx.get();
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
