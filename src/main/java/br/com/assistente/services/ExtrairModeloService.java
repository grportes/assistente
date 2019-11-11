package br.com.assistente.services;

import br.com.assistente.models.Modelo;
import br.com.assistente.models.TipoJava;
import br.com.assistente.models.Variavel;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.of;

public class ExtrairModeloService {

    public Optional<Modelo> extrair() {

        final Set<Variavel> variaveis = new LinkedHashSet<>();
        variaveis.add( new Variavel.Builder().comTipo(TipoJava.LONG).comNome("id").build() );
        variaveis.add( new Variavel.Builder().comTipo(TipoJava.STRING).comNome("razaoSocial").build() );

        return of( new Modelo.Builder()
            .comNomeEntidade("Cliente")
            .comNomeTabela("clientes")
            .comNomeCompletoTabela("vendas.dbo.clientes")
            .comVariaveis(variaveis)
            .build()
        );

    }
}
