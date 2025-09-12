package com.nfs.PremierNotes.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tomadores") // Nome da tabela no banco de dados
public class TomadorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false) // Garante que o nome do tomador seja único e não nulo
    private String nome; // Nome do tomador (ex: "SOPRANO INDUSTRIA ELETROMETALURGICA EIRELI")

    @Column(nullable = false)
    private Integer prazoPagamentoDias; // Prazo de pagamento padrão para este tomador

    @Column(nullable = false)
    private Boolean ativo; // Se o tomador está ativo ou inativo para o cálculo de prazo/filtragem

    @PrePersist // Método que será executado antes de persistir um novo objeto
    public void prePersist() {
        if (this.prazoPagamentoDias == null) {
            this.prazoPagamentoDias = 30; // Prazo padrão se não for especificado
        }
        if (this.ativo == null) {
            this.ativo = true; // Ativo por padrão se não for especificado
        }
    }
}