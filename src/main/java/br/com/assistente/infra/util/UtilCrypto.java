package br.com.assistente.infra.util;

import java.util.StringJoiner;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

public final class UtilCrypto {

    private static final int SECRET_KEY = 8;

    public static String encriptar( final String value ) {
        return crypto(value, '+');
    }

    public static String descriptografar( final String value ) {
        return crypto(value, '-');
    }

    private static String crypto(
        final String value,
        final Character operacao
    ) {
        if (isBlank(value)) return "null";
        char[] chars = value.toCharArray();
        final StringJoiner newMsg = new StringJoiner("");
        for (char c : chars) {
            switch (operacao) {
                case '+':
                    c += SECRET_KEY;
                    break;
                case '-':
                    c -= SECRET_KEY;
                    break;
            }
            newMsg.add( format("%s", c) );
        }
        return newMsg.toString();
    }
}