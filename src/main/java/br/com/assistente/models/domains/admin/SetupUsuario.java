package br.com.assistente.models.domains.admin;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

public class SetupUsuario {

    private String autor;
    private String localProjeto;
    private String idCnxAtual;
    private List<SetupCnxBanco> conexoesDisponiveis;

    public SetupUsuario() {
    }

    public String getAutor() {

        return autor;
    }

    public void setAutor( final String autor ) {

        this.autor = autor;
    }

    public String getLocalProjeto() {

        return localProjeto;
    }

    public void setLocalProjeto( final String localProjeto ) {

        this.localProjeto = localProjeto;
    }

    public String getIdCnxAtual() {

        return idCnxAtual;
    }

    public void setIdCnxAtual( final String idCnxAtual ) {

        this.idCnxAtual = idCnxAtual;
    }

    public List<SetupCnxBanco> getConexoesDisponiveis() {

        return conexoesDisponiveis;
    }

    public void setConexoesDisponiveis( final List<SetupCnxBanco> conexoesDisponiveis ) {

        this.conexoesDisponiveis = conexoesDisponiveis;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetupUsuario that = (SetupUsuario) o;
        return Objects.equals(autor, that.autor);
    }

    @Override
    public int hashCode() {

        return Objects.hash(autor);
    }

    public static void validarObjeto( final SetupUsuario setupUsuario ) {

        requireNonNull( setupUsuario );

        final StringJoiner fields = new StringJoiner(" ");
        if ( isBlank( setupUsuario.getAutor() ) ) fields.add( " autor ");
        if ( isBlank( setupUsuario.getIdCnxAtual() ) ) fields.add( " conexao ");
        if ( isNoneBlank(fields.toString()) )
            throw new IllegalStateException( "[SetupBanco] Campos obrigatórios: " + fields );
    }

}
