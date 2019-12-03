package br.com.assistente.models.domains.admin;

import br.com.assistente.models.domains.commons.constantes.FornecedorDB;

import java.util.Objects;
import java.util.StringJoiner;

import static java.lang.String.format;
import static java.util.Objects.*;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

public class SetupCnxBanco {

    private Integer id;
    private FornecedorDB fornecedorDB;
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

    public FornecedorDB getFornecedorDB() {

        return fornecedorDB;
    }

    public void setFornecedorDB( final FornecedorDB fornecedorDB ) {

        this.fornecedorDB = fornecedorDB;
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

        return nonNull( getPorta() )
            ? format( "%s - %s:%s", getFornecedorDB(), getUrl(), getPorta() )
            : format( "%s - %s", getFornecedorDB(), getUrl() );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static void validarObjeto(final SetupCnxBanco setupCnxBanco ) {

        requireNonNull( setupCnxBanco );

        final StringJoiner fields = new StringJoiner(" ");
        if ( isNull( setupCnxBanco.getFornecedorDB() ) ) fields.add( "fornecedorDB" );
        if ( isNull( setupCnxBanco.getUrl() ) ) fields.add( "url" );

        if ( nonNull( setupCnxBanco.getFornecedorDB() )
            && setupCnxBanco.getFornecedorDB().isSelecionarBase() )
            if ( isNoneBlank(fields.toString()) )
                throw new IllegalStateException( "[SetupBanco] Campos obrigatórios: " + fields );
            else
                return;

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
            && Objects.equals( setupCnxBancoA.getUrl(), setupCnxBancoB.getUrl() )
            && Objects.equals( setupCnxBancoA.getPorta(), setupCnxBancoB.getPorta() )
            && Objects.equals( setupCnxBancoA.getUserName(), setupCnxBancoB.getUserName() )
            && Objects.equals( setupCnxBancoA.getPassword(), setupCnxBancoB.getPassword() );
    }

}
