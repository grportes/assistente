package br.com.assistente.models;

import br.com.assistente.infra.util.UtilYaml;
import org.apache.commons.collections4.CollectionUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.assistente.infra.util.UtilYaml.getArquivoYaml;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.*;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

public class SetupCnxBanco {

    private Integer id;
    private String descricao;
    private String idDriver;
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

    public String getIdDriver() {

        return idDriver;
    }

    public void setIdDriver(final String idDriver) {

        this.idDriver = idDriver;
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

        return format( "%s - %s", getIdDriver(), getDescricao() );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EQUALS & HASCODE.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXILIARES.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static void validarObjeto( final SetupCnxBanco setupCnxBanco ) {

        requireNonNull( setupCnxBanco );

        StringJoiner fields = new StringJoiner(" ");
        if ( isEmpty( setupCnxBanco.getDescricao()) ) fields.add( "descricao" );
        if ( isNull( setupCnxBanco.getIdDriver() ) ) fields.add( "driver" );
        if ( isNull( setupCnxBanco.getEndereco() ) ) fields.add( "url" );
        if ( isNull( setupCnxBanco.getIdDriver() ) ) fields.add( "driver" );
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

    public static int save( final SetupCnxBanco cnxBanco ) {

        validarObjeto( cnxBanco );

        final AtomicInteger idCnxBanco = new AtomicInteger(0);

        final Path arquivoYaml = getArquivoYaml();

        final SetupUsuario setupUsuario = UtilYaml
            .load( SetupUsuario.class, arquivoYaml )
            .map( setupUsuarioAtual -> {

                final List<SetupCnxBanco> cnxs = setupUsuarioAtual.getConexoesDisponiveis();

                if ( CollectionUtils.isEmpty(cnxs) ) {
                    // Primeira conexão...
                    idCnxBanco.set( nextInt() );
                    cnxBanco.setId( idCnxBanco.get() );
                    setupUsuarioAtual.setConexoesDisponiveis( singletonList(cnxBanco) );
                    return setupUsuarioAtual;
                }

                // Update...
                for ( final SetupCnxBanco cnx : cnxs ) {
                    if ( !Objects.equals( cnx, cnxBanco ) ) continue;
                    idCnxBanco.set( cnx.getId() );
                    if ( !mesmosValores( cnx, cnxBanco ) ) {
                        cnx.setDescricao( cnxBanco.getDescricao() );
                        cnx.setIdDriver( cnxBanco.getIdDriver() );
                        cnx.setEndereco( cnxBanco.getEndereco() );
                        cnx.setPorta( cnxBanco.getPorta() );
                        cnx.setUserName( cnxBanco.getUserName() );
                        cnx.setPassword( cnxBanco.getPassword() );
                    }
                    return setupUsuarioAtual;
                }

                //  Nova conexão.
                idCnxBanco.set( nextInt() );
                cnxBanco.setId( idCnxBanco.get() );
                cnxs.add( cnxBanco );
                return setupUsuarioAtual;
            })
            .orElseGet(() -> {
                idCnxBanco.set( nextInt() );
                cnxBanco.setId( idCnxBanco.get() );

                final SetupUsuario setupUsuarioNew = new SetupUsuario();
                setupUsuarioNew.setIdCnxAtual( idCnxBanco.get() );
                setupUsuarioNew.setConexoesDisponiveis( singletonList(cnxBanco) );
                return setupUsuarioNew;
            });

        UtilYaml.dump( setupUsuario, arquivoYaml );
        SetupUsuario.setCache( setupUsuario );
        return idCnxBanco.get();
    }


}