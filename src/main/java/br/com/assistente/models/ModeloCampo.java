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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static br.com.assistente.infra.util.UtilString.convCamelCase;
import static java.lang.Integer.parseInt;
import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public final class ModeloCampo {

    private final IntegerProperty posicao;
    private final BooleanProperty pk;
    private final StringProperty colunaDB;
    private final StringProperty tipoDB;
    private final BooleanProperty colNull;
    private final IntegerProperty tamanho;
    private final StringProperty colunaJava;
    private final Boolean atributoLength;
    private final StringProperty tipoJava;
    private final BooleanProperty converter;


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
        this.tipoJava = new SimpleStringProperty( builder.tipoJava );
        this.colNull = new SimpleBooleanProperty( builder.colNull );
        this.converter = new SimpleBooleanProperty( builder.converter );
        this.atributoLength = builder.atributoLength;
    }

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

    public Boolean isAtributoLength() {

        return atributoLength;
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

    public static Set<String> buscarImports( final Set<ModeloCampo> lista ) {

        return isEmpty( lista )
            ? emptySet()
            : lista.stream().map( ModeloCampo::getTipoJava ).collect(toSet());
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
        private String tipoJava;
        private boolean colNull;
        private boolean converter;
        private boolean atributoLength;
        private Integer tamanho;

        public Builder() {

            this.posicao = 0;
            this.pk = false;
            this.colunaDB = null;
            this.colunaJava = null;
            this.tipoDB = null;
            this.tipoJava = null;
            this.colNull = false;
            this.converter = false;
            this.atributoLength = false;
            this.tamanho = null;
        }

        public Builder( final ModeloCampo modeloCampo ) {

            this.posicao = modeloCampo.getPosicao();
            this.pk = modeloCampo.isPk();
            this.colunaDB = modeloCampo.getColunaDB();
            this.colunaJava = modeloCampo.getColunaJava();
            this.tipoDB = modeloCampo.getTipoDB();
            this.tipoJava = modeloCampo.getTipoJava();
            this.colNull = modeloCampo.isColNull();
            this.converter = modeloCampo.isConverter();
            this.tamanho = modeloCampo.getTamanho();
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

        public Builder comConverter( final boolean value ) {

            this.converter = value;
            return this;
        }

        public Builder comDataType( final DataType dataType ) {

            this.tipoJava = dataType.getJavaType();
            this.atributoLength = dataType.getDbLength();
            return this;
        }

        public ModeloCampo build() {

            return new ModeloCampo( this );
        }

    }

}
