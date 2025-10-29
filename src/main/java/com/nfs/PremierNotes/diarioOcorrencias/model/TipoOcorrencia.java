package com.nfs.PremierNotes.diarioOcorrencias.model;

    public enum TipoOcorrencia {
        AFASTAMENTO("Afastamento"),
        ATESTADO("Atestado"),
        ERRO_APONTAMENTO("Erro de apontamento"),
        ESQUECIMENTO("Esquecimento"),
        FERIAS("Férias"),
        FOLGA("Folga"),
        NAO_ATUOU("Não atuou"),
        ONBOARDING("Onboarding"),
        OUTRO ("Outro");

        private final String descricao;

        TipoOcorrencia(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
