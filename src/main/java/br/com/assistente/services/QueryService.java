package br.com.assistente.services;

import br.com.assistente.infra.db.ConnectionFactory;
import br.com.assistente.models.DataType;
import br.com.assistente.models.ModeloCampo;
import br.com.assistente.models.ResultMapeamento;
import br.com.assistente.models.SetupUsuario;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class QueryService {

    public Set<ResultMapeamento> convTexto( final String query ) {

        final List<DataType> dataTypes = SetupUsuario.buscarDataTypes();

        final Set<ModeloCampo> campos = ConnectionFactory.execQuery( query );
        if ( isEmpty( campos ) )
            throw new IllegalArgumentException(
                "Não foi possivel ler metadados da query - Verifique se query esta retornando valores" );

        final Set<ModeloCampo> atributos = campos.stream().map( m -> {
            final DataType dataType = dataTypes
                .stream()
                .filter( type -> Objects.equals( m.getTipoDB(), type.getDbType() ) )
                .findFirst()
                .orElseThrow( () -> new RuntimeException( format(
                        "Coluna [ %s ] do tipo [ %s ] Não localizou tipo Java correspondente",
                        m.getColunaDB(), m.getTipoDB()
                ) ) );
            return new ModeloCampo.Builder( m ).comDataType( dataType ).build();
        } ).collect( toSet() );


        return Collections.emptySet();

    }

}
