package br.com.assistente.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import static java.lang.String.format;
import static java.net.http.HttpRequest.newBuilder;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trim;

public class VersaoService {

    public static final String VERSAO = "1.0";
    private static final String API = "http://localhost:8080/api/sys/assistente";

    public Optional<String> checkVersao() {
        final HttpClient httpClient = HttpClient.newHttpClient();
        try {
            final HttpRequest httpRequest = newBuilder(new URI(API)).GET().build();
            final HttpResponse<String> resp = httpClient.send(httpRequest, BodyHandlers.ofString());

            if ( resp.statusCode() == 200 ) {
                if ( equalsIgnoreCase( trim( resp.body() ), VERSAO ) ) return Optional.empty();
                return of( format( "Nova versão [ %s ] disponível!", resp.body() ) );
            }
            return of( format(
                "Não foi possivel verificar atualizações do Assistente! %n" +
                "StatusCode: %s - %s", resp.statusCode(), API
            ));
        } catch (IOException | InterruptedException | URISyntaxException e) {
            return of( format(
                "Não foi possivel verificar atualizações do Assistente! %n" +
                "Falha ao acessar WS: [ %s ]", API
            ));
        }
    }

}
