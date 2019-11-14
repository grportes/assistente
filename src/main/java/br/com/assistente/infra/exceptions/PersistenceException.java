package br.com.assistente.infra.exceptions;

public class PersistenceException extends RuntimeException {

    public PersistenceException( final String message ) {
        super(message);
    }
}
