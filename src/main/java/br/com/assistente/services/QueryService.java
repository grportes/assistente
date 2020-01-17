package br.com.assistente.services;

import br.com.assistente.infra.db.ConnectionFactory;
import br.com.assistente.models.ResultMapeamento;

import java.util.Collections;
import java.util.Set;

public class QueryService {

    public Set<ResultMapeamento> convTexto( final String query ) {

        ConnectionFactory.execQuery( query );

        return Collections.emptySet();

    }

}
