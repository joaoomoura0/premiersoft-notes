package com.nfs.NFSJ.models;

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
    private String tomador;
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

    @Transient
    private String statusPrazo;

    @Transient
    private Long diasParaVencer;

    @Transient
    private Long diasPagamento;

    @Transient
    public String getDataVencimentoFormatada() {
        if (this.dataEmissao == null) {
            return "N/A";
        }
        LocalDate dataVencimento = this.dataEmissao.plusDays(30);
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
