package br.com.assistente.models.domains.admin;

import br.com.assistente.models.domains.db.DriverCnx;

import java.util.Objects;
import java.util.StringJoiner;

import static java.lang.String.format;
import static java.util.Objects.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

public class SetupCnxBanco {

    private Integer id;
    private String descricao;
    private DriverCnx driver;
    private String endereco;
    private Integer porta;
    private String userName;
    private String password;

    public SetupCnxBanco() {
    }

    public Integer getId() {

        return id;
    }

    public void setId( final Integer id ) {

        this.id = id;
    }

    public String getDescricao() {

        return descricao;
    }

    public void setDescricao( String descricao ) {

        this.descricao = descricao;
    }

    public DriverCnx getDriver() {

        return driver;
    }

    public void setDriver( final DriverCnx driver ) {

        this.driver = driver;
    }

    public String getEndereco() {

        return endereco;
    }

    public void setEndereco(final String endereco) {

        this.endereco = endereco;
    }

    public Integer getPorta() {

        return porta;
    }

    public void setPorta( final Integer porta ) {

        this.porta = porta;
    }

    public String getUserName() {

        return userName;
    }

    public void setUserName( final String userName ) {

        this.userName = userName;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword( final String password ) {

        this.password = password;
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
        SetupCnxBanco that = (SetupCnxBanco) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return hash(id);
    }

    @Override
    public String toString() {

        return format( "%s - %s", getDriver(), getDescricao() );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static void validarObjeto( final SetupCnxBanco setupCnxBanco ) {

        requireNonNull( setupCnxBanco );

        StringJoiner fields = new StringJoiner(" ");
        if ( isEmpty( setupCnxBanco.getDescricao()) ) fields.add( "descricao" );
        if ( isNull( setupCnxBanco.getDriver() ) ) fields.add( "driver" );
        if ( isNull( setupCnxBanco.getEndereco() ) ) fields.add( "url" );
        if ( isNoneBlank(fields.toString()) )
            throw new IllegalStateException( "[SetupBanco] Campos obrigatórios: " + fields );

        fields = new StringJoiner(" ");
        final DriverCnx driver = setupCnxBanco.getDriver();
        if ( driver.isExigePorta() && isNull( setupCnxBanco.getPorta() ) ) fields.add( "porta" );
        if ( driver.isExigeAutenticacao() ) {
            if ( isNull( setupCnxBanco.getUserName() ) ) fields.add( "userName" );
            if ( isNull( setupCnxBanco.getPassword() ) ) fields.add( "password" );
        }
        if ( isNoneBlank(fields.toString()) )
            throw new IllegalStateException( "[SetupBanco] Campos obrigatórios: " + fields );
    }

    public static boolean mesmosValores (
        final SetupCnxBanco setupCnxBancoA,
        final SetupCnxBanco setupCnxBancoB
    ) {

        return nonNull( setupCnxBancoA )
            && nonNull( setupCnxBancoB )
            && Objects.equals( setupCnxBancoA.getDescricao(), setupCnxBancoB.getDescricao() )
            && Objects.equals( setupCnxBancoA.getEndereco(), setupCnxBancoB.getEndereco() )
            && Objects.equals( setupCnxBancoA.getPorta(), setupCnxBancoB.getPorta() )
            && Objects.equals( setupCnxBancoA.getUserName(), setupCnxBancoB.getUserName() )
            && Objects.equals( setupCnxBancoA.getPassword(), setupCnxBancoB.getPassword() );
    }

}
