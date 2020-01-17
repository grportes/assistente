package br.com.assistente.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static br.com.assistente.infra.util.UtilString.convCamelCase;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.lastIndexOfIgnoreCase;

public class DefinicaoDto {

    private final IntegerProperty posicao;
    private final ObjectProperty<DefinicaoDto.Tipo> tipo;
    private final StringProperty nomeAtributo;
    private final BooleanProperty atributoId;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTOR
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public DefinicaoDto( final Builder builder ) {

        this.posicao = new SimpleIntegerProperty( builder.posicao );
        this.tipo = new SimpleObjectProperty<>( builder.tipo );
        this.nomeAtributo = new SimpleStringProperty( builder.nomeAtributo );
        this.atributoId = new SimpleBooleanProperty( builder.atributoId );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GETTERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public int getPosicao() {

        return posicao.get();
    }

    public IntegerProperty posicaoProperty() {

        return posicao;
    }

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

    public static Set<String> buscarImportsSerializer( final Set<DefinicaoDto> lista ) {

        return isEmpty( lista )
            ? emptySet()
            : lista.stream()
                .map( DefinicaoDto::getTipo )
                .map( Tipo::getJsonSerialize )
                .filter( StringUtils::isNotBlank )
                .collect( toSet() );
    }

    public static Set<DefinicaoDto> orderByPosicao( final Set<DefinicaoDto> lista ) {

        return isEmpty( lista )
                ? emptySet()
                : lista.stream()
                .sorted( comparing( DefinicaoDto::getPosicao ) )
                .collect( toCollection( LinkedHashSet::new ) );
    }

    public static Set<DefinicaoDto> buscarTodosAtributoId( final Set<DefinicaoDto> lista ) {

        return isEmpty( lista )
            ? emptySet()
            : lista.stream()
                .filter( DefinicaoDto::isAtributoId )
                .collect( toSet() );
    }

    public static String convPadraoNomeClasse( final String nomeClasse ) {

        if ( isBlank( nomeClasse ) ) throw new IllegalArgumentException( "Favor informar o nome da classe" );
        String tmp = convCamelCase( nomeClasse, true );
        return ( lastIndexOfIgnoreCase( tmp, "dto" ) == -1 ) ? tmp.concat( "Dto" ) : tmp;

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

            @Override
            public String getJsonSerialize() {
                return "";
            }
        },

        INTEGER("Integer") {
            @Override
            public String getImportNecessario() {

                return "";
            }

            @Override
            public String getJsonSerialize() {
                return "";
            }
        },

        LONG("Long") {
            @Override
            public String getImportNecessario() {

                return "";
            }

            @Override
            public String getJsonSerialize() {

                return "";
            }
        },

        BIGDECIMAL( "BigDecimal" ) {
            @Override
            public String getImportNecessario() {

                return "java.math.BigDecimal";
            }

            @Override
            public String getJsonSerialize() {

                return "";
            }
        },

        LOCAL_DATE( "LocalDate" ) {
            @Override
            public String getImportNecessario() {

                return "java.util.LocalDate";
            }

            @Override
            public String getJsonSerialize() {

                return "infra.json.Serializer.SerializerLocalDateSerializer";
            }
        },

        LOCAL_DATETIME( "LocalDateTime") {
            @Override
            public String getImportNecessario() {

                return "java.util.LocalDateTime";
            }

            @Override
            public String getJsonSerialize() {

                return "infra.json.Serializer.SerializerLocalDateTimeSerializer";
            }
        },

        LOCAL_TIME( "LocalTime") {
            @Override
            public String getImportNecessario() {

                return "java.util.LocalTime";
            }

            @Override
            public String getJsonSerialize() {

                return "infra.json.Serializer.SerializerLocalTime";
            }
        },

        CHAR( "Char" ) {
            @Override
            public String getImportNecessario() {

                return "";
            }

            @Override
            public String getJsonSerialize() {

                return "";
            }
        },

        STRING("String") {
            @Override
            public String getImportNecessario() {

                return "";
            }

            @Override
            public String getJsonSerialize() {

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
        public abstract String getJsonSerialize();

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

        public Integer posicao;
        private Tipo tipo;
        private String nomeAtributo;
        private Boolean atributoId;

        public Builder() {

            this.posicao = null;
            this.tipo = null;
            this.nomeAtributo = null;
            this.atributoId = null;
        }

        public Builder comPosicao( final Integer value ) {

            requireNonNull( value, "Obrigatório informar posição do atributo" );
            this.posicao = value;
            return this;
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
