package com.nfs.NFSJ.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    private String retido; // Pode ser enum também

    private String status;

    private String localRecolhimento;

    private String statusPagamento;

    // funcao de prazo pagamento

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Transient
    private String statusPrazo;

    @Transient
    private Long diasParaVencer;

    @Transient
    private Long diasPagamento;

    @Transient // Também ignorado pelo banco, é apenas um método auxiliar
    public String getDataVencimentoFormatada() {
        if (this.dataEmissao == null) {
            return "N/A";
        }
        // Lógica de prazo: Data de Emissão + 30 dias.
        // Você pode ajustar o número de dias aqui se a regra mudar.
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


    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getCnpjTomador() {
        return cnpjTomador;
    }

    public void setCnpjTomador(String cnpjTomador) {
        this.cnpjTomador = cnpjTomador;
    }

    public String getTomador() {
        return tomador;
    }

    public void setTomador(String tomador) {
        this.tomador = tomador;
    }

    public Double getValorNF() {
        return valorNF;
    }

    public void setValorNF(Double valorNF) {
        this.valorNF = valorNF;
    }

    public Double getValorDeducoes() {
        return valorDeducoes;
    }

    public void setValorDeducoes(Double valorDeducoes) {
        this.valorDeducoes = valorDeducoes;
    }

    public Double getValorBase() {
        return valorBase;
    }

    public void setValorBase(Double valorBase) {
        this.valorBase = valorBase;
    }

    public Double getAliquota() {
        return aliquota;
    }

    public void setAliquota(Double aliquota) {
        this.aliquota = aliquota;
    }

    public Double getValorIssqn() {
        return valorIssqn;
    }

    public void setValorIssqn(Double valorIssqn) {
        this.valorIssqn = valorIssqn;
    }

    public String getRetido() {
        return retido;
    }

    public void setRetido(String retido) {
        this.retido = retido;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocalRecolhimento() {
        return localRecolhimento;
    }

    public void setLocalRecolhimento(String localRecolhimento) {
        this.localRecolhimento = localRecolhimento;
    }


    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }


    // get e set -> funcao prazo pagamento

public LocalDate getDataPagamento() {
    return dataPagamento;
}

public void setDataPagamento(LocalDate dataPagamento) {
    this.dataPagamento = dataPagamento;
}

public String getStatusPrazo() {
    return statusPrazo;
}

public void setStatusPrazo(String statusPrazo) {
    this.statusPrazo = statusPrazo;
}

public Long getDiasParaVencer() {
    return diasParaVencer;
}

public void setDiasParaVencer(Long diasParaVencer) {
    this.diasParaVencer = diasParaVencer;
}

public Long getDiasPagamento() {
    return diasPagamento;
}

public void setDiasPagamento(Long diasPagamento) {
    this.diasPagamento = diasPagamento;
}
}
