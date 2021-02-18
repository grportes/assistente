package br.com.assistente.infra.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public final class UtilCrypto {

    private static final SecretKey SECRET_KEY;

    static {
        SECRET_KEY = new SecretKeySpec( "l4;WWHw:.}byWlJo<4Ph|1tgs;I2TXe6".getBytes(), "AES" );
    }

    public static String encriptar( final String mensagem ) {

        try {
            final var cipher = Cipher.getInstance("AES");
            cipher.init( ENCRYPT_MODE, SECRET_KEY );
            final var bytes = cipher.doFinal( mensagem.getBytes() );
            return Base64.getEncoder().encodeToString( bytes );
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException( "Não foi possível localizar o algorítmo de criptografia!" );
        } catch (InvalidKeyException e) {
            throw new RuntimeException( "Chave inválida!" );
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException( "Tamanho do bloco da mensagem inválido!" );
        } catch (BadPaddingException e) {
            throw new RuntimeException( "Preenchimento incorreto de dados!" );
        }
    }

    public static String descriptografar( final String mensagem ) {

        try {
            final var bytes = Base64.getDecoder().decode( mensagem );
            final var cipher = Cipher.getInstance("AES");
            cipher.init( DECRYPT_MODE, SECRET_KEY );
            return new String( cipher.doFinal( bytes ) );
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException( "Não foi possível localizar o algorítmo de criptografia!" );
        } catch (InvalidKeyException e) {
            throw new RuntimeException( "Chave inválida!" );
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException( "Tamanho do bloco da mensagem inválido!" );
        } catch (BadPaddingException e) {
            throw new RuntimeException( "Preenchimento incorreto de dados!" );
        }
    }
}