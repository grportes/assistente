package br.com.assistente.services;

import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trim;

public class VersaoService {

    public static final String VERSAO = "1.0.1";
    private static final String API = "http://siga.arcom.com.br/api/sys/v1/assistente";

    public void check( final Consumer<String> acao ) {
        final ExecutorService executorService = newFixedThreadPool( 1 );
        try {
            final HttpTask task = new HttpTask();
            task.setOnSucceeded( ( event ) -> acao.accept( task.getValue() ) );
            task.setOnFailed( ( event ) -> acao.accept( "Check Versão: TaskError " ) );
            executorService.execute( task );
        } finally {
            executorService.shutdown();
        }
    }

    private static class HttpTask extends Task<String> {
        @Override
        protected String call() {
            try {
                final HttpResponse<String> response = newHttpClient()
                    .send( newBuilder( new URI( API ) ).GET().build(), ofString() );
                if ( response.statusCode() == 200 ) {
                    if ( equalsIgnoreCase( trim( response.body() ), VERSAO ) ) return null;
                    return format( "<< NOVA VERSÃO: %s DISPONÍVEL!! >>", response.body() );
                }
                return format( "Check Versão: %s ", response.statusCode() );
            } catch ( final URISyntaxException | InterruptedException | IOException e ) {
                return "Check Versão: Falha comunicação ";
            }
        }
    }
}
