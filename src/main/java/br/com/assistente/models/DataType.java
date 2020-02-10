package br.com.assistente.models;

import java.util.Objects;

public class DataType {

    private String dbType;
    private Boolean dbLength;
    private String javaType;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTOR
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public DataType() {
    }

    private DataType( final Builder builder ) {

        this.dbType = builder.dbType;
        this.dbLength = builder.dbLength;
        this.javaType = builder.javaType;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GETTERS && SETTERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getDbType() {

        return dbType;
    }

    public void setDbType( final String dbType ) {

        this.dbType = dbType;
    }

    public Boolean getDbLength() {

        return dbLength;
    }

    public void setDbLength( final Boolean dbLength  ) {

        this.dbLength = dbLength;
    }

    public String getJavaType() {

        return javaType;
    }

    public void setJavaType( final String javaType ) {

        this.javaType = javaType;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EQUALS && HASHCODE
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataType dataType = (DataType) o;
        return Objects.equals(dbType, dataType.dbType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(dbType);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BUILDER
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Builder {

        private String dbType;
        private Boolean dbLength;
        private String javaType;

        public Builder( final DataType dataType ) {

            this.dbType = dataType.dbType;
            this.dbLength = dataType.dbLength;
            this.javaType = dataType.javaType;
        }

        public Builder comJavaType( final String value ) {

            this.javaType = value;
            return this;
        }

        public DataType build() {

            return new DataType( this );
        }

    }

}
