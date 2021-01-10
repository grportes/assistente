package br.com.assistente.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static br.com.assistente.infra.util.UtilYaml.load;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.startsWith;

public final class DriverCnx {

    private static final Logger logger = LogManager.getLogger(DriverCnx.class);

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

    public void setPorta( final Boolean porta ) {

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
            final URL resource = DriverCnx.class.getResource( "" );
            cache = startsWith( resource.getProtocol(), "jar" )
                ? loadCacheFromJavaUrl( resource )
                : loadCacheFromClassLoader( );
        }
    }

    /**
     * Leitura através da distribuição JavaWebStart
     *
     * @param url Url Java
     *
     * @return Lista de drivers.
     */
    private static List<DriverCnx> loadCacheFromJavaUrl( final URL url ) {

        try {
            final List<DriverCnx> buffer = new ArrayList<>(  );
            final JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            final JarFile jarFile = jarURLConnection.getJarFile();
            final Enumeration<JarEntry> entries = jarFile.entries();
            while ( entries.hasMoreElements() ) {
                final JarEntry jarEntry = entries.nextElement();
                final String nomeArquivo = jarEntry.getName();
                final boolean arquivoYml = startsWith( nomeArquivo, "drivers/" )
                    && endsWithIgnoreCase( nomeArquivo, ".yml" );
                if ( arquivoYml ) {
                    final JarEntry fileEntry = jarFile.getJarEntry( nomeArquivo );
                    try ( final BufferedInputStream bis = new BufferedInputStream( jarFile.getInputStream( fileEntry ) ) ) {
                        final Yaml yaml = new Yaml( new Constructor( DriverCnx.class ) );
                        buffer.add(  yaml.load( bis ) );
                    }
                }
            }
            return buffer;
        } catch ( final IOException e ) {
            throw new RuntimeException( e );
        }
    }

    private static List<DriverCnx> loadCacheFromClassLoader( ) {

        final ClassLoader classLoader = DriverCnx.class.getClassLoader();
        final URL drivers =  requireNonNull(
            classLoader.getResource( "drivers" ),
            "Pasta drivers não localizada!"
        );

        try {
            return Files.list( Paths.get( drivers.toURI() ) )
                    .map( path -> load( DriverCnx.class, path ) )
                    .map( Optional::get )
                    .collect( toList() );
        } catch ( IOException | URISyntaxException e ) {
            throw new RuntimeException( e );
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
