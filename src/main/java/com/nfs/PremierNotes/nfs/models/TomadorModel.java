package com.nfs.PremierNotes.nfs.models;

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
@Table(name = "tomadores")
public class TomadorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome único para evitar duplicidade de cadastro do mesmo cliente
    @Column(unique = true, nullable = false)
    private String nome;

    // O "Pulo do gato": Assim que importamos o Excel, o sistema olha o nome,
    // acha este tomador e descobre qual o prazo de pagamento dele automaticamente.
    @Column(nullable = false)
    private Integer prazoPagamentoDias;

    @Column(nullable = false)
    private Boolean ativo;

    @PrePersist
    public void prePersist() {
        if (this.prazoPagamentoDias == null) {
            this.prazoPagamentoDias = 30; // Padrão de 30 dias se não informado
        }
        if (this.ativo == null) {
            this.ativo = true;
        }
    }
}