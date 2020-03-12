package br.com.assistente;

import org.junit.Test;

import java.util.Optional;

public class MainTest {

    @Test
    public void ex01() {

        System.out.println("teste");



    }

    private Optional<String> ex011() {

        return Optional.of("ex011");
    }

    private Optional<String> ex012() {

        return Optional.of("ex012");
    }
}
