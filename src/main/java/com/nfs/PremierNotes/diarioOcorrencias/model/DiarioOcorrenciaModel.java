package com.nfs.PremierNotes.diarioOcorrencias.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

    @Getter
    @Setter
    @Entity
    @Table(name = "diario_ocorrencias")
    public class DiarioOcorrenciaModel {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotNull(message = "A data da ocorrência é obrigatória.")
        private LocalDate data;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "colaborador_id", nullable = false)
        @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
        @NotNull(message = "O colaborador é obrigatório.")
        private ColaboradorSeguroModel colaborador;


            @Enumerated(EnumType.STRING)
            @NotNull(message = "O tipo de ocorrência é obrigatório.")
            private TipoOcorrencia tipo;

            @Enumerated(EnumType.STRING)
            @NotNull
            private StatusOcorrencia status;


        @Size(max = 1000, message = "A descrição não pode exceder 1000 caracteres.")
        @Column(columnDefinition = "TEXT")
        private String descricao;

        private LocalDateTime dataRegistro;
    }
