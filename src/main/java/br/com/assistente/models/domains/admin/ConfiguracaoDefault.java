package br.com.assistente.models.domains.admin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class ConfiguracaoDefault {

    private String autor;
    private String banco;
    private Path localProjeto;

    public ConfiguracaoDefault( final Builder builder ) {

        this.autor = builder.autor;
        this.banco = builder.banco;
        this.localProjeto = builder.localProjeto;
    }

    public ConfiguracaoDefault( final String value ) {

    }

    public String getAutor() {

        return autor;
    }

    public String getBanco() {

        return banco;
    }

    public Path getLocalProjeto() {

        return localProjeto;
    }


    public static class Builder {

        private String autor;
        private String banco;
        private Path localProjeto;

        public Builder comAutor( final String value ) {

            this.autor = value;
            return this;
        }

        public Builder comBanco( final String value ) {

            this.banco = value;
            return this;
        }

        public Builder comLocalProjeto( final Path value ) {

            this.localProjeto = value;
            return this;
        }

        public ConfiguracaoDefault build() {

            return new ConfiguracaoDefault( this );
        }

    }

    public static class Parse {

        public static String convString( final ConfiguracaoDefault config ) {

            if ( isNull(config) ) return "null|null|null";

            return format( "%s|%s|%s",
                config.getAutor(),
                config.getBanco(),
                isNull(config.getLocalProjeto()) ? null : config.getLocalProjeto().toString()
            );
        }

        public static Optional<ConfiguracaoDefault> convObj( final String value ) {

            if ( isBlank(value) ) return empty();

            final Matcher matcher = Pattern.compile("(.*)\\|(.*)\\|(.*)").matcher(value);
            return  matcher.find()
                ? of( new ConfiguracaoDefault
                        .Builder()
                        .comAutor( matcher.group(1) )
                        .comBanco( matcher.group(2) )
                        .comLocalProjeto( Paths.get( matcher.group(3) ) )
                        .build()
                    )
                : empty();
        }
    }

}
