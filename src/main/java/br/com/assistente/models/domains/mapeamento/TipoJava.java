package br.com.assistente.models.domains.mapeamento;

public enum TipoJava {

    STRING {
        @Override
        public String toString() {
            return "String";
        }
    },

    LONG {
        @Override
        public String toString() {
            return "Long";
        }
    },

    SHORT {
        @Override
        public String toString() {
            return "Short";
        }
    };


}