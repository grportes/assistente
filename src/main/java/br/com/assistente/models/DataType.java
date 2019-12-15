package br.com.assistente.models;

import java.util.Objects;

public class DataType {

    private String dbType;
    private String javaType;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

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
}
