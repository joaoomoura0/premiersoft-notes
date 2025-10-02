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

    @Column(unique = true, nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer prazoPagamentoDias;

    @Column(nullable = false)
    private Boolean ativo;

    @PrePersist
    public void prePersist() {
        if (this.prazoPagamentoDias == null) {
            this.prazoPagamentoDias = 30;
        }
        if (this.ativo == null) {
            this.ativo = true;
        }
    }
}