package br.com.assistente.models.domains.admin;

import java.util.Objects;
import java.util.StringJoiner;

import static java.lang.String.format;
import static java.util.Objects.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

public class SetupCnxBanco {

    private Integer id;
    private String descricao;
    private String driverCnx;
    private String url;
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

    public String getDriverCnx() {

        return driverCnx;
    }

    public void setDriverCnx( final String driverCnx ) {

        this.driverCnx = driverCnx;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl( final String url ) {

        this.url = url;
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

        return format( "%s - %s", getDriverCnx(), getDescricao() );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static void validarObjeto( final SetupCnxBanco setupCnxBanco ) {

        requireNonNull( setupCnxBanco );

        final StringJoiner fields = new StringJoiner(" ");
        if ( isEmpty( setupCnxBanco.getDescricao()) ) fields.add( "descricao" );
        if ( isNull( setupCnxBanco.getDriverCnx() ) ) fields.add( "fornecedorDB" );
        if ( isNull( setupCnxBanco.getUrl() ) ) fields.add( "url" );
        if ( isNull( setupCnxBanco.getPorta() ) ) fields.add( "porta" );
        if ( isNull( setupCnxBanco.getUserName() ) ) fields.add( "userName" );
        if ( isNull( setupCnxBanco.getPassword() ) ) fields.add( "password" );
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
            && Objects.equals( setupCnxBancoA.getUrl(), setupCnxBancoB.getUrl() )
            && Objects.equals( setupCnxBancoA.getPorta(), setupCnxBancoB.getPorta() )
            && Objects.equals( setupCnxBancoA.getUserName(), setupCnxBancoB.getUserName() )
            && Objects.equals( setupCnxBancoA.getPassword(), setupCnxBancoB.getPassword() );
    }

}
