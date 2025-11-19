package com.nfs.PremierNotes.colaboradores.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nfs.PremierNotes.diarioOcorrencias.model.DiarioOcorrenciaModel;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "colaboradores_seguro")
public class ColaboradorSeguroModel {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate dataNascimento;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate dataAdmissao;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(nullable = false)
    private String tipoContrato;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private Boolean ativoNoSeguro;

    @OneToMany(mappedBy = "colaborador", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<DiarioOcorrenciaModel> ocorrenciasDiarias;

    @Column(nullable = false)
    private String account = "Sistema";

    @Column(nullable = false)
    private String origem = "TimeTracker"; // ou "Sistema"

    public ColaboradorSeguroModel() {
    }

    public ColaboradorSeguroModel(String nomeCompleto, LocalDate dataNascimento, LocalDate dataAdmissao,
                                  String tipoContrato, String cpf, Boolean ativoNoSeguro) {
        this.nomeCompleto = nomeCompleto;
        this.dataNascimento = dataNascimento;
        this.dataAdmissao = dataAdmissao;
        this.tipoContrato = tipoContrato;
        this.cpf = cpf;
        this.ativoNoSeguro = ativoNoSeguro;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Boolean getAtivoNoSeguro() {
        return ativoNoSeguro;
    }

    public void setAtivoNoSeguro(Boolean ativoNoSeguro) {
        this.ativoNoSeguro = ativoNoSeguro;
    }

    @Override
    public String toString() {
        return "ColaboradorSeguroModel{" +
                "id=" + id +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", dataAdmissao=" + dataAdmissao +
                ", tipoContrato='" + tipoContrato + '\'' +
                ", cpf='" + cpf + '\'' +
                ", ativoNoSeguro=" + ativoNoSeguro +
                '}';
    }
}