package com.nfs.PremierNotes.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nota_fiscal")
public class NotaFiscalModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dataEmissao;
    private String cnpjTomador;
    private Double valorNF;
    private Double valorDeducoes;
    private Double valorBase;
    private Double aliquota;
    private Double valorIssqn;
    private String retido;
    private String status;
    private String localRecolhimento;
    private String statusPagamento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tomador_id")
    private TomadorModel tomadorModel;

    @Transient
    public String getTomador() {
        if (this.tomadorModel != null) {
            return this.tomadorModel.getNome();
        }

        return "N/D (Antigo)"; // Retorne algo que indique que a ligação está faltando
    }

    @Transient
    private String statusPrazo;

    @Transient
    private Long diasParaVencer;

    @Transient
    private Long diasPagamento;

    @Transient
    public String getDataVencimentoFormatada() {
        if (this.dataEmissao == null || this.tomadorModel == null || this.tomadorModel.getPrazoPagamentoDias() == null) {
            return "N/A";
        }
        Integer prazo = this.tomadorModel.getPrazoPagamentoDias();
        LocalDate dataVencimento = this.dataEmissao.plusDays(prazo);
        return dataVencimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Transient
    public String getDataPagamentoFormatada() {
        if (this.dataPagamento == null) {
            return "N/A";
        }
        return this.dataPagamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}