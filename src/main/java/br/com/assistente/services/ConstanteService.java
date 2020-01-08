package br.com.assistente.services;

import br.com.assistente.models.Constante;
import br.com.assistente.models.ResultMapeamento;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static br.com.assistente.infra.util.UtilCollections.createSet;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.upperCase;

public class ConstanteService {

    private static final Set<String> tipos = createSet(
    "Short;Integer;Long;String", ';'
    );

    public Set<String> buscarTipos() {

        return tipos;
    }

    public Constante check(
        final String tipo,
        final Constante constante
    ) {

        if ( isEmpty( tipo ) ) throw new IllegalArgumentException( "Obrigatório informar o tipo de dados da constante!" );
        requireNonNull( constante, "Obrigatório informar dados" );

        if ( isEmpty( constante.getNome() ) ) throw new IllegalArgumentException( "Obrigatório informar nome da constante!" );
        if ( isEmpty( constante.getValor() ) ) throw new IllegalArgumentException( "Obrigatório informar o valor da constante!" );
        if ( isEmpty( constante.getDescricao() ) ) throw new IllegalArgumentException( "Obrigatório informar descrição da constante!" );

        if ( tipos.stream().noneMatch( t -> Objects.equals(t, tipo) ) )
            throw new IllegalArgumentException( "Não localizou tipo de dados" );

        try {

            final String nome = replace( upperCase( trim( constante.getNome() ) ), " ", "_" );

            final String valor = trim( constante.getValor() );
            switch ( tipo ) {
                case "Short":
                    Short.parseShort( valor );
                    break;
                case "Integer":
                    Integer.parseInt( valor );
                    break;
                case "Long":
                    Long.parseLong( valor );
                    break;
                case "String":
                    break;
                default:
                    throw new RuntimeException( "Tipo de dados não aceito" );
            }

            return new Constante.Builder()
                .comNome( nome )
                .comValor( valor )
                .comDescricao( constante.getDescricao().trim().toUpperCase() )
                .build();

        } catch ( NumberFormatException e ) {
            throw new IllegalArgumentException( constante.getValor() + " inválido!" );
        }
    }

    public Set<ResultMapeamento> convTexto(
        final String nomeEnum,
        final String tipo,
        final Set<Constante> constantes
    ) {

        return Collections.emptySet();
    }
}
