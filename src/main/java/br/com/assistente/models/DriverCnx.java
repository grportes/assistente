package br.com.assistente.models;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.assistente.infra.util.UtilArquivo.getResourceFolder;
import static br.com.assistente.infra.util.UtilYaml.load;
import static java.lang.String.format;
import static java.nio.file.Files.notExists;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public final class DriverCnx {

    private String id;
    private String driver;
    private String protocolo;
    private Boolean autenticar;
    private Boolean porta;
    private Boolean selecionarBaseDados;
    private String selectDate;
    private String selectTop;
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

    public String getSelectTop() {

        return selectTop;
    }

    public void setSelectTop( final String selectTop ) {

        this.selectTop = selectTop;
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
            final Path pathDrivers = Paths.get( "E:\\assistente\\assistente-1.0.1.jar\\drivers");
            try {
                cache = Files.list(pathDrivers)
                    .map( path -> load(DriverCnx.class, path) )
                    .map( Optional::get )
                    .collect( toList() );
            } catch ( final IOException e ) {
                throw new UncheckedIOException( format( "Falhou leitura em: %s", pathDrivers.toString() ), e );
            }
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
