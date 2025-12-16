package com.nfs.PremierNotes.diarioOcorrencias.model;

    public enum TipoOcorrencia {
        AFASTAMENTO("Afastamento"),
        ATESTADO("Atestado"),
        DESALOCADO ("Desalocado(a)"),
        DESLIGADO ("Desligado(a)"),
        ESQUECIMENTO("Esquecimento"),
        FERIAS("Férias"),
        FOLGA("Folga"),
        NAOㅤATUOU("Não atuou"),
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
