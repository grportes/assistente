package br.com.assistente.services;

import br.com.assistente.models.DefinicaoDto;
import br.com.assistente.models.ResultMapeamento;
import br.com.assistente.models.SetupUsuario;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static br.com.assistente.infra.util.UtilArquivo.createDirectoryIfNotExists;
import static br.com.assistente.infra.util.UtilCollections.requireNotEmpty;
import static br.com.assistente.infra.util.UtilString.requireNotBlank;
import static br.com.assistente.infra.util.UtilVelocity.exec;
import static br.com.assistente.models.DefinicaoDto.buscarImports;
import static br.com.assistente.models.DefinicaoDto.buscarImportsSerializer;
import static br.com.assistente.models.DefinicaoDto.buscarTodosAtributoId;
import static br.com.assistente.models.DefinicaoDto.orderByPosicao;
import static br.com.assistente.models.TipoResult.DTO;
import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.singleton;
import static org.apache.commons.lang3.StringUtils.lastIndexOfIgnoreCase;

public class DefinicaoDtoService {

    public Set<ResultMapeamento> convTexto(
        final String nomeClasse,
        final Set<DefinicaoDto> definicoes,
        boolean gerarJsonAnnotations,
        boolean gerarClasseBuilder
    ) {
        requireNotBlank( nomeClasse, "É necessário informar o nome do DTO!");
        requireNotEmpty( definicoes, "É necessário informar os atributos do DTO" );

        final String nomeAutor = SetupUsuario.find().map(SetupUsuario::getAutor).orElse("????");

        final VelocityContext context = new VelocityContext();
        context.put( "nomeAutor", nomeAutor );
        context.put( "nomeClasse", nomeClasse );
        context.put( "definicoes", orderByPosicao( definicoes ) );
        context.put( "gerarJsonAnnotations", gerarJsonAnnotations );
        context.put( "gerarClasseBuilder", gerarClasseBuilder );
        context.put( "importsNecessarios", buscarImports( definicoes ) );
        context.put( "importsNecessariosSerializer", buscarImportsSerializer( definicoes ) );
        context.put( "identificadores", buscarTodosAtributoId( definicoes ) );
        context.put( "StringUtils", StringUtils.class );
        context.put( "dataHora", now().format( ofPattern( "dd/MM/yyyy" ) ) );

        return singleton( new ResultMapeamento.Builder()
            .comNomeEntidade( nomeClasse )
            .comConteudoEntidade( exec( context, "definicao_dto.vm" ) )
            .comTipoResult( DTO )
            .build()
        );
    }

    public void gravarArquivos( final Set<ResultMapeamento> rsMapeamentos ) {
        final Path rootPath = SetupUsuario.buscarLocalProjeto().resolve( "app" ).resolve( "models" );
        if ( !exists( rootPath ) )
            throw new IllegalArgumentException( format( "Não foi possível localizar caminho: %s", rootPath ) );

        final Path commonsPath = rootPath.resolve( "commons" );
        createDirectoryIfNotExists( commonsPath );

        final Path dtoPath = commonsPath.resolve( "dtos" );
        createDirectoryIfNotExists( dtoPath );

        final ResultMapeamento mapeamento = rsMapeamentos
            .stream()
            .filter( m -> lastIndexOfIgnoreCase( m.getNomeEntidade(), "Dto" ) != -1 )
            .findFirst()
            .orElseThrow( () -> new RuntimeException( "Falha ao localizar classe Dto!" ) );

        final Path pathArquivo = dtoPath.resolve( mapeamento.getNomeEntidade().concat( ".java" ) );
        if ( exists( pathArquivo ) )
            throw new RuntimeException( "Não foi possível criar classe pois já existe!" );

        try {
            Files.write( pathArquivo, mapeamento.getConteudoEntidade().getBytes() );
        } catch ( final IOException e ) {
            throw new UncheckedIOException( format( "Falhou gravação de %s", pathArquivo.toString()), e );
        }
    }
}
