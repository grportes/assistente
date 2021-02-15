package br.com.assistente.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trim;

public class VersaoService {

    private static final Logger logger = LogManager.getLogger(VersaoService.class);

    public static final String VERSAO = "1.0";
    private static final String API = "http://localhost:8080/api/sys/assistente";

    public void checkVersao( final Consumer<String> aviso ) {
        try {
            logger.info("Executando {}", API);
            newHttpClient()
                .sendAsync( newBuilder(new URI(API)).GET().build(), ofString() )
                .whenComplete((resp,erro) -> {
                    final String msg;
                    if ( nonNull(erro) ) {
                        msg = format( "Falha ao verificar atualizações do Assistente! %n%s", API );
                    } else {
                        if ( resp.statusCode() == 200 ) {
                            if ( equalsIgnoreCase( trim( resp.body() ), VERSAO ) ) return;
                            msg = format( "Nova versão [ %s ] disponível!", resp.body() );
                        } else {
                            msg = format( "Não foi possivel verificar atualizações do Assistente! %n" +
                                    "StatusCode: %s - %s", resp.statusCode(), API
                            );
                        }
                    }
                    logger.info("{}", msg);
                    aviso.accept(msg);
                });
        } catch ( final URISyntaxException e ) {
            logger.throwing(e);
            aviso.accept( format(
                "Não foi possivel verificar atualizações do Assistente! %n" +
                "Falha ao acessar WS: [ %s ]", API
            ));
        }
    }

}
