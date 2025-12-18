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

    @Column(nullable = false)
    private LocalDate dataEmissao;

    // Guardamos o CNPJ na nota pois ele vem na linha do Excel
    private String cnpjTomador;

    // Relacionamento com o Tomador (quem define o prazo de pagamento)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tomador_id", nullable = false)
    private TomadorModel tomador;

    // --- VALORES ESSENCIAIS (Do Excel) ---
    private Double valorNF;    // Equivalente ao "Vl. NF." (e ao Vl. Base)
    private Double valorIssqn; // Equivalente ao "Vl.ISSQN"

    // --- INFORMAÇÕES DE ESTADO ---
    private String status;            // "NORMAL" ou "CANCELADA"
    private String localRecolhimento; // "BLUMENAU - SC" ou "EXTERIOR - EX"

    // --- CONTROLE FINANCEIRO INTERNO ---
    private String statusPagamento; // Ex: "Pendente", "Pago"

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    // Caso a nota tenha um prazo específico diferente do padrão do tomador
    @Column(name = "prazo_pagamento_dias")
    private Integer prazoPagamentoDias;

    // --- CAMPOS TRANSIENT (Não salvam no banco, servem para o Java/Front) ---

    @Transient
    private String statusPrazo; // Para lógica de "Vencendo", "Atrasado"

    @Transient
    private String nomeTomadorString; // Usado APENAS durante a importação do Excel antes de achar o ID

    @Transient
    private Long diasParaVencer;

    @Transient
    private Long diasPagamento;

    // --- MÉTODOS AUXILIARES E DE FORMATAÇÃO ---

    // Ajuda no filtro: Verifica se é Nota do Exterior
    @Transient
    public boolean isExterior() {
        if (this.localRecolhimento != null && this.localRecolhimento.toUpperCase().contains("EXTERIOR")) {
            return true;
        }
        // Verifica também se o CNPJ está zerado (comum em notas de exterior)
        return this.cnpjTomador != null && this.cnpjTomador.startsWith("00.000");
    }

    // Ajuda no filtro: Verifica se está Cancelada
    @Transient
    public boolean isCancelada() {
        return "CANCELADA".equalsIgnoreCase(this.status);
    }

    @Transient
    public String getDataVencimentoFormatada() {
        if (this.dataEmissao == null) return "N/A";

        // Se a nota tiver prazo específico usa ele, senão usa o do Tomador, senão 30 dias padrão
        Integer prazo = (this.prazoPagamentoDias != null && this.prazoPagamentoDias > 0)
                ? this.prazoPagamentoDias
                : (this.tomador != null ? this.tomador.getPrazoPagamentoDias() : 30);

        LocalDate dataVencimento = this.dataEmissao.plusDays(prazo);
        return dataVencimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Transient
    public String getDataPagamentoFormatada() {
        return (this.dataPagamento == null) ? "N/A" : this.dataPagamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Transient
    public String getDataEmissaoFormatada() {
        return (this.dataEmissao == null) ? "" : this.dataEmissao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}