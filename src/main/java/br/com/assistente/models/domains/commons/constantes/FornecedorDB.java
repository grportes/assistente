package br.com.assistente.models.domains.commons.constantes;

public enum FornecedorDB {

    SYBASE( "Sybase", "/db/sybase.yml", true, false ),

    SQLITE( "Sqlite", "/db/sqlite.yml", false, true )
    ;

    private String descricao;
    private String valor;
    private boolean portaCnx;
    private boolean selecionarBase;

    FornecedorDB(
        final String descricao,
        final String valor,
        final boolean portaCnx,
        final boolean selecionarBase
    ) {

        this.descricao = descricao;
        this.valor = valor;
        this.portaCnx = portaCnx;
        this.selecionarBase = selecionarBase;
    }

    public String getDescricao() {

        return this.descricao;
    }

    public String getValor() {

        return this.valor;
    }

    public boolean isPortaCnx() {

        return portaCnx;
    }

    public boolean isSelecionarBase() {

        return selecionarBase;
    }

    @Override
    public String toString() {

        return getDescricao();
    }
}
