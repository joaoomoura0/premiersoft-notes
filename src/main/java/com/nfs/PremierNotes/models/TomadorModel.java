package com.nfs.PremierNotes.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tomadores")
public class TomadorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    private Integer prazoPagamentoDias;

    private boolean ativo = true;

    public TomadorModel(String nome, Integer prazo) {
        this.nome = nome;
        this.prazoPagamentoDias = prazo;
    }
}