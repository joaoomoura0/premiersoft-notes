package com.nfs.PremierNotes.nfs.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "nota_fiscal")
public class NotaFiscalModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dataEmissao;
    private String cnpjTomador;

    // private String tomador;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tomador_id", nullable = false)
    private TomadorModel tomador;

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

    @Column(name = "prazo_pagamento_dias")
    private Integer prazoPagamentoDias;

    @Transient
    private String statusPrazo;

    @Transient
    private String nomeTomadorString;

    @Transient
    private Long diasParaVencer;

    @Transient
    private Long diasPagamento;

    @Transient
    public String getDataVencimentoFormatada() {
        if (this.dataEmissao == null) {
            return "N/A";
        }
        Integer prazo = (this.prazoPagamentoDias != null && this.prazoPagamentoDias > 0) ? this.prazoPagamentoDias : 30;
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