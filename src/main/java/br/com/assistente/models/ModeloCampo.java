package br.com.assistente.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static br.com.assistente.infra.util.UtilString.convCamelCase;
import static java.lang.Integer.parseInt;
import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public final class ModeloCampo {

    private final IntegerProperty posicao;
    private final BooleanProperty pk;
    private final StringProperty colunaDB;
    private final StringProperty tipoDB;
    private final BooleanProperty colNull;
    private final IntegerProperty tamanho;
    private final IntegerProperty digitoDecimal;
    private final StringProperty colunaJava;
    private final StringProperty tipoJava;
    private final BooleanProperty converter;
    private final StringProperty nomeEnum;
    private final Boolean atributoLength;
    private final Boolean autoIncremento;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTOR
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ModeloCampo( final Builder builder ) {

        this.posicao = new SimpleIntegerProperty( builder.posicao );
        this.pk = new SimpleBooleanProperty( builder.pk );
        this.colunaDB = new SimpleStringProperty( builder.colunaDB );
        this.tamanho = new SimpleIntegerProperty( builder.tamanho );
        this.colunaJava = new SimpleStringProperty( builder.colunaJava );
        this.tipoDB = new SimpleStringProperty( builder.tipoDB );
        this.digitoDecimal = new SimpleIntegerProperty( builder.digitoDecimal );
        this.tipoJava = new SimpleStringProperty( builder.tipoJava );
        this.colNull = new SimpleBooleanProperty( builder.colNull );
        this.converter = new SimpleBooleanProperty( builder.converter );
        this.nomeEnum = new SimpleStringProperty( "" );
        this.atributoLength = builder.atributoLength;
        this.autoIncremento = builder.autoIncremento;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GETTERS && SETTERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public IntegerProperty posicaoProperty() {

        return posicao;
    }

    public int getPosicao() {

        return posicao.get();
    }

    public boolean isPk() {

        return pk.get();
    }

    public BooleanProperty pkProperty() {

        return pk;
    }

    public String getColunaDB() {

        return colunaDB.get();
    }

    public StringProperty colunaDBProperty() {

        return colunaDB;
    }

    public int getTamanho() {

        return tamanho.get();
    }

    public IntegerProperty tamanhoProperty() {

        return tamanho;
    }

    public String getColunaJava() {

        return colunaJava.get();
    }

    public StringProperty colunaJavaProperty() {

        return colunaJava;
    }

    public String getTipoDB() {

        return tipoDB.get();
    }

    public StringProperty tipoDBProperty() {

        return tipoDB;
    }

    public IntegerProperty digitoDecimalProperty() {

        return digitoDecimal;
    }

    public Integer getDigitoDecimal() {

        return digitoDecimal.get();
    }

    public String getTipoJava() {

        return tipoJava.get();
    }

    public StringProperty tipoJavaProperty() {

        return tipoJava;
    }

    public boolean isColNull() {

        return colNull.get();
    }

    public BooleanProperty colNullProperty() {

        return colNull;
    }

    public boolean isConverter() {

        return converter.get();
    }

    public BooleanProperty converterProperty() {

        return converter;
    }

    public StringProperty nomeEnumProperty() {

        return nomeEnum;
    }

    public String getNomeEnum() {

        return nomeEnum.get();
    }

    public Boolean isAtributoLength() {

        return atributoLength;
    }

    public Boolean isAutoIncremento() {

        return autoIncremento;
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
        ModeloCampo that = (ModeloCampo) o;
        return Objects.equals( posicao, that.posicao );
    }

    @Override
    public int hashCode() {

        return Objects.hash( posicao );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTANTES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Extrair numero de p.ex: varchar(100)
    private static final Pattern REGEX = Pattern.compile( "(.*)\\((\\d+)\\)" );


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METODOS AUXLIARES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Set<ModeloCampo> orderByPosicao( final Set<ModeloCampo> lista ) {

        return isEmpty( lista )
            ? emptySet()
            : lista.stream()
                .sorted( comparing( ModeloCampo::getPosicao ) )
                .collect( toCollection( LinkedHashSet::new ) );
    }

    public static Set<ModeloCampo> buscarPks( final Set<ModeloCampo> lista ) {

        return isEmpty( lista ) ? emptySet() : lista.stream().filter( ModeloCampo::isPk ).collect( toSet() );
    }

    public static boolean chaveComposta( final Set<ModeloCampo> lista ) {

        return isNotEmpty( lista ) && lista.stream().filter( ModeloCampo::isPk ).count() > 1;
    }

    public static boolean atributoVersion( final Set<ModeloCampo> lista ) {

        return isNotEmpty( lista )
                && lista.stream().anyMatch( m -> equalsIgnoreCase( m.getColunaDB(), "version" ) );
    }

    public static Set<String> buscarImports( final Set<ModeloCampo> lista ) {

        final Supplier<Stream<String>> tiposJavaObrigatorios = () -> Stream.of(
            "java.math.BigDecimal",
            "java.time.LocalDate",
            "java.time.LocalTime",
            "java.time.LocalDateTime"
        );

        return isEmpty( lista )
            ? emptySet()
            : lista.stream()
                .map( ModeloCampo::getTipoJava )
                .filter( tipoJava ->  tiposJavaObrigatorios.get().anyMatch( t -> t.equalsIgnoreCase( tipoJava ) )  )
                .collect( toSet() );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BUILDER
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Builder {

        private int posicao;
        private boolean pk;
        private String colunaDB;
        private String colunaJava;
        private String tipoDB;
        private int digitoDecimal;
        private String tipoJava;
        private boolean colNull;
        private boolean converter;
        private boolean atributoLength;
        private int tamanho;
        private boolean autoIncremento;

        public Builder() {

            this.posicao = 0;
            this.pk = false;
            this.colunaDB = null;
            this.colunaJava = null;
            this.tipoDB = null;
            this.digitoDecimal = 0;
            this.tipoJava = null;
            this.colNull = false;
            this.converter = false;
            this.atributoLength = false;
            this.tamanho = 0;
            this.autoIncremento = false;
        }

        public Builder( final ModeloCampo modeloCampo ) {

            this.posicao = modeloCampo.getPosicao();
            this.pk = modeloCampo.isPk();
            this.colunaDB = modeloCampo.getColunaDB();
            this.colunaJava = modeloCampo.getColunaJava();
            this.tipoDB = modeloCampo.getTipoDB();
            this.tipoJava = modeloCampo.getTipoJava();
            this.digitoDecimal = modeloCampo.getDigitoDecimal();
            this.colNull = modeloCampo.isColNull();
            this.converter = modeloCampo.isConverter();
            this.tamanho = modeloCampo.getTamanho();
            this.autoIncremento = modeloCampo.isAutoIncremento();
        }

        public Builder comPosicao( final int value ) {

            this.posicao = value;
            return this;
        }

        public Builder comPK( final boolean value ) {

            this.pk = value;
            return this;
        }

        public Builder comColunaDB( final String value ) {

            this.colunaDB = lowerCase( value );
            this.colunaJava = ColunaId.getNomeAtributo( value ).orElse( convCamelCase( value, false ) );
            return this;
        }

        public Builder comDigitoDecimal( final Integer value ) {

            this.digitoDecimal = isNull( value ) ? 0 : value;
            return this;
        }

        public Builder comTamanho( final Integer value ) {

            if ( isNull( this.tamanho ) ) this.tamanho = value;
            return this;
        }

        public Builder comTipoDB( final String value ) {

            final Matcher matcher = REGEX.matcher( value );
            if ( matcher.find() ) {
                this.tipoDB = matcher.group( 1 );
                this.tamanho = parseInt( matcher.group( 2 ) );
            } else {
                this.tipoDB = value;
            }

            return this;
        }

        public Builder comColNull( final boolean value ) {

            this.colNull = value;
            return this;
        }

        public Builder comDataType( final DataType value ) {

            this.tipoJava = value.getJavaType();
            this.atributoLength = value.getDbLength();
            return this;
        }

        public Builder comAutoIncremento( final boolean value ) {

            this.autoIncremento = value;
            return this;
        }

        public ModeloCampo build() {

            return new ModeloCampo( this );
        }

    }

}
