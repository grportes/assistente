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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static br.com.assistente.infra.util.UtilString.normalizeJava;
import static br.com.assistente.infra.util.UtilString.requireNotBlank;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.trim;

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
                .map( Tipo::getPackage )
                .filter( nomePackage -> !startsWithIgnoreCase( nomePackage, "java.lang." ) )
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

    public static Set<String> buscarImportsTupleConverter( final Set<DefinicaoDto> lista ) {

        return isEmpty( lista )
            ? emptySet()
            : lista.stream()
                .map( DefinicaoDto::getTipo )
                .map( Tipo::getTupleConverter )
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

    private static Pattern REGEX = Pattern.compile( "(.*)(dto)$", CASE_INSENSITIVE );

    public static String convPadraoNomeClasse( final String nomeClasse ) {

        requireNotBlank( nomeClasse, "Favor informar o nome da classe" );

        final String tmp = normalizeJava( nomeClasse, true );

        final Matcher matcher = REGEX.matcher( tmp );
        if ( matcher.find() && matcher.groupCount() > 1 )
            return trim( matcher.group( 1 ) ) + "Dto";

        return trim(tmp) + "Dto";
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTANTES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public enum Tipo {

        SHORT("Short") {
            @Override
            public String getPackage() {
                return "java.lang.Short";
            }

            @Override
            public String getJsonSerialize() {
                return "";
            }

            @Override
            public String getTupleConverter() {
                return "infra.util.UtilNumber.toShort";
            }
        },

        INTEGER("Integer") {
            @Override
            public String getPackage() {
                return "java.lang.Integer";
            }

            @Override
            public String getJsonSerialize() {
                return "";
            }

            @Override
            public String getTupleConverter() {
                return "infra.util.UtilNumber.toInteger";
            }
        },

        LONG("Long") {
            @Override
            public String getPackage() {
                return "java.lang.Long";
            }

            @Override
            public String getJsonSerialize() {
                return "";
            }

            @Override
            public String getTupleConverter() {
                return "infra.util.UtilNumber.toLong";
            }
        },

        BIGDECIMAL( "BigDecimal" ) {
            @Override
            public String getPackage() {
                return "java.math.BigDecimal";
            }

            @Override
            public String getJsonSerialize() {
                return "";
            }

            @Override
            public String getTupleConverter() {
                return "infra.util.UtilNumber.toBigDecimal";
            }
        },

        LOCAL_DATE( "LocalDate" ) {
            @Override
            public String getPackage() {
                return "java.time.LocalDate";
            }

            @Override
            public String getJsonSerialize() {
                return "infra.json.Serializer.SerializerLocalDateSerializer";
            }

            @Override
            public String getTupleConverter() {
                return "infra.util.UtilDate.toLocalDate";
            }
        },

        LOCAL_DATETIME( "LocalDateTime") {
            @Override
            public String getPackage() {
                return "java.time.LocalDateTime";
            }

            @Override
            public String getJsonSerialize() {
                return "infra.json.Serializer.SerializerLocalDateTimeSerializer";
            }

            @Override
            public String getTupleConverter() {
                return "infra.util.UtilDate.toLocalDateTime";
            }
        },

        LOCAL_TIME( "LocalTime") {
            @Override
            public String getPackage() {
                return "java.time.LocalTime";
            }

            @Override
            public String getJsonSerialize() {
                return "infra.json.Serializer.SerializerLocalTime";
            }

            @Override
            public String getTupleConverter() {
                return "infra.util.UtilDate.toLocalTime";
            }
        },

        CHAR( "Char" ) {
            @Override
            public String getPackage() {
                return "java.lang.Character";
            }

            @Override
            public String getJsonSerialize() {
                return "";
            }

            @Override
            public String getTupleConverter() {
                return "";
            }
        },

        STRING("String") {
            @Override
            public String getPackage() {
                return "java.lang.String";
            }

            @Override
            public String getJsonSerialize() {
                return "";
            }

            @Override
            public String getTupleConverter() {
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

        public abstract String getPackage();
        public abstract String getJsonSerialize();
        public abstract String getTupleConverter();

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
            this.atributoId = false;
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

        public Builder comTipo( final DataType dataType ) {

            requireNonNull( dataType, "Obrigatório informar o tipo do atributo" );
            final String javaType = substringAfterLast( dataType.getJavaType(), "." );
            this.tipo = Tipo.buscarTipos()
                .stream()
                .filter( t -> equalsIgnoreCase( t.getNome(), javaType ) )
                .findFirst()
                .orElseThrow( () -> new IllegalArgumentException( "Nao localizou tipo de dados" ) );
            return this;
        }

        public Builder comNomeAtributo( final String value ) {

            if ( isBlank( value ) ) throw new IllegalArgumentException( "Obrigatório informar o nome do atributo" );
            this.nomeAtributo = normalizeJava( value, false );
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
