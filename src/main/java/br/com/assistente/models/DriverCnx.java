package br.com.assistente.models;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.assistente.infra.util.UtilArquivo.getResourceFolder;
import static br.com.assistente.infra.util.UtilYaml.load;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

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

    private static void loadCache() {

        if ( isNull(cache) )
            try {
                cache = Files.list( getResourceFolder( "drivers" ) )
                    .map( path -> load(DriverCnx.class, path) )
                    .map( Optional::get )
                    .collect( toList() );
            } catch ( final IOException e ) {
                cache = emptyList();
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

    public static String getQuerySelectTop1(
        final DriverCnx driverCnx,
        final Modelo modelo
    ) {

        requireNonNull( driverCnx, "DriveCnx - Argumento obritatório: driverCnx" );
        requireNonNull( modelo, "DriveCnx - Argumento obritatório: modelo" );

        return driverCnx.getSelectTop().replaceAll( "%TABLE%", modelo.getNomeCompletoTabela() );
    }

}
