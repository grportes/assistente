package br.com.assistente.models.repository.admin;

import br.com.assistente.infra.exceptions.BusinessException;
import br.com.assistente.infra.exceptions.PersistenceException;
import br.com.assistente.models.domains.admin.ConfiguracaoDefault;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static br.com.assistente.infra.util.UtilArquivo.buscarLocalApp;
import static br.com.assistente.infra.util.UtilArquivo.excluir;
import static br.com.assistente.infra.util.UtilArquivo.gravarBinario;
import static br.com.assistente.infra.util.UtilArquivo.lerArquivoBinario;
import static java.lang.String.format;
import static java.nio.file.Files.notExists;
import static java.util.Optional.ofNullable;

public class ConfiguracaoDefaultRepository {

    private static ConfiguracaoDefault configuracaoDefault;


    public static Optional<ConfiguracaoDefault> find() {

        final Path arquivoDat = getLocalArquivoDat();

        configuracaoDefault = null;

        lerArquivoBinario( arquivoDat )
            .flatMap( ConfiguracaoDefault.Parse::convObj )
            .ifPresent( conf -> configuracaoDefault = conf );

        return ofNullable( configuracaoDefault );
    }

    public static void save( final ConfiguracaoDefault novaConfiguracao ) {

        final Path arquivoDat = getLocalArquivoDat();

        if ( ! excluir( arquivoDat ) )
            throw new PersistenceException( format( "Falhou exclusão do arquivo %s", arquivoDat ) );

        if ( ! gravarBinario( arquivoDat , ConfiguracaoDefault.Parse.convString(novaConfiguracao) ) )
            throw new PersistenceException( format( "Falhou escrita do arquivo %s", arquivoDat ) );

        configuracaoDefault = novaConfiguracao;
    }

    private static Path getLocalArquivoDat()  {

        final Path local = buscarLocalApp();
        if ( notExists( local ) )
            throw new PersistenceException( "Não foi possivel localizar pasta do aplicativo" );

        return local.resolve("assistente.dat");
    }



}
