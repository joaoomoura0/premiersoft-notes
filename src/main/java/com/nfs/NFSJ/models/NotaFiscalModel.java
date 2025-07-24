package com.nfs.NFSJ.models;

import jakarta.persistence.*;
import java.time.LocalDate;

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
}