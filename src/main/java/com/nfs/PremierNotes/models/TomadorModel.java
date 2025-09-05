package com.nfs.PremierNotes.models; // Verifique seu pacote

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

    private boolean ativo = true; // Por padrão, todo novo tomador é ativo

    public TomadorModel(String nome, Integer prazo) {
        this.nome = nome;
        this.prazoPagamentoDias = prazo;
    }
}