package br.com.assistente.models;

import javax.jnlp.BasicService;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

public class SetupUsuarioNew {

    private static PersistenceService ps;
    private static BasicService bs;

    static {
        try {
            ps = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService");
            bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
            final URL codeBase = bs.getCodeBase();
            ps.create(codeBase, 16 * 1024);
            ps.setTag(codeBase, PersistenceService.CACHED);
        } catch ( UnavailableServiceException e) {
            throw new RuntimeException( e );
        } catch ( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }


    public static void gravar() {

        // http://www.javased.com/index.php?api=javax.jnlp.BasicService
        // http://useof.org/java-open-source/javax.jnlp.PersistenceService
        // https://docs.oracle.com/javase/8/docs/jre/api/javaws/jnlp/index.html

    }


}
