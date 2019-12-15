package br.com.assistente.models;

import br.com.assistente.config.ConexaoDB;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.assistente.infra.util.UtilYaml.load;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

public class DriverCnx {

    private String id;
    private String driver;
    private String protocolo;
    private Boolean autenticar;
    private Boolean porta;
    private Boolean selecionarBaseDados;
    private String selectDate;
    private String selectTop;
    private List<DataType> dataTypes;
    private List<String> bancos;

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

    public List<String> getBancos() {

        return bancos;
    }

    public void setBancos( final List<String> bancos ) {

        this.bancos = bancos;
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

    public static List<String> buscarIds() {

        if ( isNull(cache) ) {

            final URL resource = ConexaoDB.class.getResource("/db");
            final Path arquivos = Paths.get( resource.getPath() );

            try {
                cache = Files.list( arquivos )
                    .map( path -> load(DriverCnx.class, path) )
                    .map( Optional::get )
                    .collect( toList() );
            } catch (IOException e) {
                cache = emptyList();
            }
        }

        return cache.stream().map( DriverCnx::getId ).collect( toList() );
    }


    public static Optional<DriverCnx> findById( final String id ) {

        return isNull(cache)
            ? empty()
            : cache.stream().filter(driver -> Objects.equals( driver.getId(), id ) ).findFirst();
    }

}
