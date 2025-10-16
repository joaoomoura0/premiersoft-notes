package com.nfs.PremierNotes.diarioOcorrencias.repository;

import com.nfs.PremierNotes.diarioOcorrencias.model.DiarioOcorrenciaModel;
import com.nfs.PremierNotes.diarioOcorrencias.model.StatusOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DiarioOcorrenciaRepository extends JpaRepository<DiarioOcorrenciaModel, Long> {

    List<DiarioOcorrenciaModel> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);
    List<DiarioOcorrenciaModel> findByData(LocalDate data);
    List<DiarioOcorrenciaModel> findByColaborador_NomeCompletoContainingIgnoreCase(String nome);
    List<DiarioOcorrenciaModel> findByStatus(StatusOcorrencia status);

}
