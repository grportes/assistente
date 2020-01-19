package br.com.assistente.services;

import br.com.assistente.infra.db.ConnectionFactory;
import br.com.assistente.models.ModeloCampo;
import br.com.assistente.models.ResultMapeamento;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.Set;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class QueryService {

    public Set<ResultMapeamento> convTexto( final String query ) {

        Set<ModeloCampo> campos = ConnectionFactory.execQuery( query );

        if ( isEmpty( campos ) )
            throw new IllegalArgumentException(
                "Não foi possivel ler metadados da query - Verifique se query esta retornando valores" );

        for ( ModeloCampo campo : campos ) {
            System.out.println( campo.getColunaDB() + " - " + campo.getTipoJava() );
        }

        return Collections.emptySet();

    }

}
