package com.nfs.PremierNotes.diarioOcorrencias.model;

    public enum TipoOcorrencia {
        AFASTAMENTO("Afastamento"),
        ATESTADO("Atestado"),
        HORAS_EXTRAS_COMPENSADAS("Horas Extras Compensadas"),
        ERRO_APONTAMENTO("Erro de Apontamento"),
        ESQUECIMENTO("Esquecimento de Apontamento"),
        FERIAS("Férias"),
        FOLGA("Folga"),
        NAO_ATUOU("Não Atuou"),
        ONBOARDING("Onboarding"),
        SAIDA_ANTECIPADA("Saída Antecipada"),
        TRABALHOU_EM_OUTRO_PROJETO("Trabalhou em Outro Projeto"),
        TROCA_FERIADO("Troca de Feriado");

        private final String descricao;

        TipoOcorrencia(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
