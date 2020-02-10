package br.com.assistente.models;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static br.com.assistente.infra.util.UtilArquivo.buscarNomeArquivoAplicacaoJar;
import static br.com.assistente.infra.util.UtilYaml.load;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.lastIndexOfIgnoreCase;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

public final class DriverCnx {

    private String id;
    private String driver;
    private String protocolo;
    private Boolean autenticar;
    private Boolean porta;
    private Boolean selecionarBaseDados;
    private String selectDate;
    private List<DataType> dataTypes;

    public String getId() {

        return id;
    }

    public void setId( final String id ) {

        this.id = id;
    }

    public String getDriver() {

        return driver;
    }

    public void setDriver( final String driver ) {

        this.driver = driver;
    }

    public String getProtocolo() {

        return protocolo;
    }

    public void setProtocolo(final String protocolo) {

        this.protocolo = protocolo;
    }

    public Boolean isExigeAutenticacao() {

        return autenticar;
    }

    public void setAutenticar( final Boolean autenticar ) {

        this.autenticar = autenticar;
    }

    public Boolean isExigePorta() {

        return porta;
    }

    public void setPorta(final Boolean porta) {

        this.porta = porta;
    }

    public Boolean getSelecionarBaseDados() {

        return selecionarBaseDados;
    }

    public void setSelecionarBaseDados( final Boolean selecionarBaseDados ) {

        this.selecionarBaseDados = selecionarBaseDados;
    }

    public String getSelectDate() {

        return selectDate;
    }

    public void setSelectDate( final String selectDate ) {

        this.selectDate = selectDate;
    }

    public List<DataType> getDataTypes() {

        return dataTypes;
    }

    public void setDataTypes( final List<DataType> dataTypes ) {

        this.dataTypes = dataTypes;
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
        DriverCnx driverCnx = (DriverCnx) o;
        return Objects.equals(id, driverCnx.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {

        return getId();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTANTES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static List<DriverCnx> cache;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void loadCache() {

        if ( isEmpty( cache ) ) {
            final URL resource = DriverCnx.class.getResource( "/drivers" );
            cache = equalsIgnoreCase( resource.getProtocol(), "jar" )
                ? loadCacheFromJar( )
                : loadCacheFromFile( resource );
        }
    }

    private static List<DriverCnx> loadCacheFromJar( ) {

        final String jarFile = buscarNomeArquivoAplicacaoJar();

        try ( final ZipFile zipFile = new ZipFile( jarFile ) ) {
            final Enumeration<? extends ZipEntry> e = zipFile.entries();
            final List<DriverCnx> buffer = new ArrayList<>(  );
            while (e.hasMoreElements()) {
                final ZipEntry entry = e.nextElement();
                if ( startsWithIgnoreCase( entry.getName(), "drivers/" ) ) {
                    if ( lastIndexOfIgnoreCase( entry.getName(), ".yml" ) > 0 ) {
                        try ( final BufferedInputStream bis = new BufferedInputStream( zipFile.getInputStream(entry) ) ) {
                            final Yaml yaml = new Yaml( new Constructor( DriverCnx.class ) );
                            buffer.add(  yaml.load( bis ) );
                        }
                    }
                }
            }
            return buffer;
        } catch ( IOException e) {
            throw new UncheckedIOException( format( "Falha ao ler arquivo %s", jarFile ), e );
        }
    }

    private static List<DriverCnx> loadCacheFromFile( final URL resource ) {

        // Tratamento para paths do Windows:
        final Pattern regex = compile( "(/\\w:)(.*)$", CASE_INSENSITIVE );
        final Matcher matcher = regex.matcher( resource.getFile() );
        final String nomeArquivo =  ( matcher.find() && matcher.groupCount() > 1 )
            ? resource.getFile().substring( 1 )
            : resource.getFile();

        try {
            return Files.list( Paths.get( nomeArquivo ) )
                .map( path -> load( DriverCnx.class, path ) )
                .map( Optional::get )
                .collect( toList() );
        } catch ( final IOException e ) {
            throw new UncheckedIOException( format( "Falhou leitura em: %s", nomeArquivo ), e );
        }
    }

    public static List<String> buscarIds() {

        loadCache();
        return cache.stream().map( DriverCnx::getId ).collect( toList() );
    }

    public static Optional<DriverCnx> findById( final String id ) {

        loadCache();
        return isNull( cache )
            ? empty()
            : cache.stream().filter( driver -> Objects.equals( driver.getId(), id ) ).findFirst();
    }
}
