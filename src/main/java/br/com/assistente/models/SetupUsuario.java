package br.com.assistente.models;

import br.com.assistente.infra.util.UtilYaml;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import static br.com.assistente.infra.util.UtilYaml.getArquivoYaml;
import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.of;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

public class SetupUsuario {

    private String autor;
    private String localProjeto;
    private Integer idCnxAtual;
    private List<SetupCnxBanco> conexoesDisponiveis;

    public SetupUsuario() {
    }

    public String getAutor() {

        return autor;
    }

    public void setAutor( final String autor ) {

        this.autor = autor;
    }

    public String getLocalProjeto() {

        return localProjeto;
    }

    public void setLocalProjeto( final String localProjeto ) {

        this.localProjeto = localProjeto;
    }

    public Integer getIdCnxAtual() {

        return idCnxAtual;
    }

    public void setIdCnxAtual( final Integer idCnxAtual ) {

        this.idCnxAtual = idCnxAtual;
    }

    public List<SetupCnxBanco> getConexoesDisponiveis() {

        return conexoesDisponiveis;
    }

    public void setConexoesDisponiveis( final List<SetupCnxBanco> conexoesDisponiveis ) {

        this.conexoesDisponiveis = conexoesDisponiveis;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EQUALS & HASCODE.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetupUsuario that = (SetupUsuario) o;
        return Objects.equals(autor, that.autor);
    }

    @Override
    public int hashCode() {

        return Objects.hash(autor);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTANTES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static SetupUsuario cache;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void load() {

        find().ifPresent( SetupUsuario::setCache );
    }

    public static void setCache( final SetupUsuario value ) {

        cache = value;
    }

    public static Optional<SetupUsuario> find() {

        if ( nonNull( cache ) ) return of( cache );

        final Path arquivoYaml = getArquivoYaml();
        return UtilYaml.load( SetupUsuario.class, arquivoYaml );
    }


    public static void validarObjeto( final SetupUsuario setupUsuario ) {

        requireNonNull( setupUsuario );

        final StringJoiner fields = new StringJoiner(" ");
        if ( isBlank( setupUsuario.getAutor() ) ) fields.add( " autor ");
        if ( isNull( setupUsuario.getIdCnxAtual() ) ) fields.add( " conexao ");
        if ( isNoneBlank(fields.toString()) )
            throw new IllegalStateException( "[SetupBanco] Campos obrigatórios: " + fields );
    }

    public static Optional<SetupCnxBanco> findByIdCnxBanco( Integer idCnxBancoSelecionada ) {

        return find()
            .map( SetupUsuario::getConexoesDisponiveis )
            .orElseGet( Collections::emptyList )
            .stream()
            .filter( cnx -> Objects.equals( cnx.getId(), idCnxBancoSelecionada ) )
            .findFirst();
    }

    public static Optional<SetupCnxBanco> buscarCnxAtivaDoUsuario() {

        return SetupUsuario.find().flatMap( su -> SetupCnxBanco.findById(su.getIdCnxAtual()) );
    }

    public static Path buscarLocalProjeto() {

        final Path localProjeto = find()
            .map( SetupUsuario::getLocalProjeto )
            .filter( StringUtils::isNotBlank )
            .map( Paths::get )
            .orElseThrow( () -> new RuntimeException("Favor informar o local do projeto no menu configurações!") );

        if ( !exists(localProjeto) )
            throw new IllegalArgumentException( format( "Não foi possível localizar caminho: %s", localProjeto ) );

        return localProjeto;
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

    public static Set<String> getCatalogosCnxSelecionada() {

        if ( isNull( cache ) ) return emptySet();

        final Integer idCnxAtual = cache.getIdCnxAtual();
        if ( isNull( idCnxAtual ) ) return emptySet();

        return cache.getConexoesDisponiveis()
            .stream()
            .filter( cnx -> Objects.equals(cnx.getId(), idCnxAtual) )
            .map( SetupCnxBanco::getCatalogos )
            .collect( LinkedHashSet::new, Set::addAll, Set::addAll );
    }

    public static List<DataType> buscarDataTypes() {

        return buscarCnxAtivaDoUsuario()
                .map( SetupCnxBanco::getIdDriver )
                .flatMap( DriverCnx::findById )
                .map( DriverCnx::getDataTypes )
                .orElseThrow( () -> new RuntimeException( "Não foi possivel localizar driver de conexão!!" ) );
    }

}
