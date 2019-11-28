package br.com.assistente.models.domains.db;

import java.util.List;

public class DefinicaoDB {

    private String driver;
    private String selectTop;
    private List<DataType> dataTypes;
    private List<String> bancos;

    public String getDriver() {

        return driver;
    }

    public void setDriver( final String driver ) {

        this.driver = driver;
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
}
