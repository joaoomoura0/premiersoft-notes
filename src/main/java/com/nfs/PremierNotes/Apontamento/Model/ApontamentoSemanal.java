package com.nfs.PremierNotes.Apontamento.Model;

import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "apontamento_semanal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ApontamentoSemanal {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private BigDecimal horas;

    @Column(nullable = false)
    private Boolean possuiDescricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private ColaboradorSeguroModel colaborador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
