package br.com.assistente.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static br.com.assistente.infra.util.UtilString.convCamelCase;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class DefinicaoDto {

    private final ObjectProperty<DefinicaoDto.Tipo> tipo;
    private final StringProperty nomeAtributo;
    private final BooleanProperty atributoId;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTOR
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public DefinicaoDto( final Builder builder ) {

        this.tipo = new SimpleObjectProperty<>( builder.tipo );
        this.nomeAtributo = new SimpleStringProperty( builder.nomeAtributo );
        this.atributoId = new SimpleBooleanProperty( builder.atributoId );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GETTERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Tipo getTipo() {

        return tipo.get();
    }

    public ObjectProperty<DefinicaoDto.Tipo> tipoProperty() {

        return tipo;
    }

    public String getNomeAtributo() {

        return nomeAtributo.get();
    }

    public StringProperty nomeAtributoProperty() {

        return nomeAtributo;
    }

    public Boolean isAtributoId() {

        return atributoId.get();
    }

    public BooleanProperty atributoIdProperty() {

        return atributoId;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EQUALS && HASHCODE
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals( Object o ) {

        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        DefinicaoDto that = (DefinicaoDto) o;
        return Objects.equals( getNomeAtributo(), that.getNomeAtributo() );
    }

    @Override
    public int hashCode() {

        return Objects.hash( getNomeAtributo() );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXLIARES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Set<String> buscarImports( final Set<DefinicaoDto> lista ) {

        return isEmpty( lista )
            ? emptySet()
            : lista.stream()
                .map( DefinicaoDto::getTipo )
                .map( Tipo::getImportNecessario )
                .filter( StringUtils::isNotBlank )
                .collect( toSet() );
    }

    public static Set<DefinicaoDto> buscarTodosAtributoId( final Set<DefinicaoDto> lista ) {

        return isEmpty( lista )
            ? emptySet()
            : lista.stream()
                .filter( DefinicaoDto::isAtributoId )
                .collect( toSet() );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTANTES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public enum Tipo {

        SHORT("Short") {
            @Override
            public String getImportNecessario() {

                return "";
            }
        },

        INTEGER("Integer") {
            @Override
            public String getImportNecessario() {

                return "";
            }
        },

        LONG("Long") {
            @Override
            public String getImportNecessario() {

                return "";
            }
        },

        BIGDECIMAL( "BigDecimal" ) {
            @Override
            public String getImportNecessario() {

                return "java.math.BigDecimal";
            }
        },

        LOCAL_DATE( "LocalDate" ) {
            @Override
            public String getImportNecessario() {

                return "java.util.LocalDate";
            }
        },

        LOCAL_DATETIME( "LocalDateTime") {
            @Override
            public String getImportNecessario() {

                return "java.util.LocalDateTime";
            }
        },

        LOCAL_TIME( "LocalTime") {
            @Override
            public String getImportNecessario() {

                return "java.util.LocalTime";
            }
        },

        CHAR( "Char" ) {
            @Override
            public String getImportNecessario() {

                return "";
            }
        },

        STRING("String") {
            @Override
            public String getImportNecessario() {

                return "";
            }
        };

        private String nome;

        Tipo( final String nome ) {

            this.nome = nome;
        }

        public String getNome() {

            return nome;
        }

        @Override
        public String toString() {

            return getNome();
        }

        public abstract String getImportNecessario();

        public static List<Tipo> buscarTipos() {

            return asList( values() );
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BUILDER
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Builder {

        private Tipo tipo;
        private String nomeAtributo;
        private Boolean atributoId;

        public Builder() {

            this.tipo = null;
            this.nomeAtributo = null;
        }

        public Builder comTipo( final Tipo value ) {

            requireNonNull( value, "Obrigatório informar o tipo do atributo" );
            this.tipo = value;
            return this;
        }

        public Builder comNomeAtributo( final String value ) {

            if ( isBlank( value ) ) throw new IllegalArgumentException( "Obrigatório informar o nome do atributo" );
            this.nomeAtributo = convCamelCase( value, false );
            return this;
        }

        public Builder comAtributoId( final Boolean value ) {

            this.atributoId = value;
            return this;
        }

        public DefinicaoDto build() {

            return new DefinicaoDto(this );
        }
    }

}
