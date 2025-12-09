package com.nfs.PremierNotes.diarioOcorrencias.repository;

import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import com.nfs.PremierNotes.diarioOcorrencias.model.DiarioOcorrenciaModel;
import com.nfs.PremierNotes.diarioOcorrencias.model.StatusOcorrencia;
import com.nfs.PremierNotes.diarioOcorrencias.model.TipoOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiarioOcorrenciaRepository extends JpaRepository<DiarioOcorrenciaModel, Long> {

    List<DiarioOcorrenciaModel> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);
    List<DiarioOcorrenciaModel> findByData(LocalDate data);
    List<DiarioOcorrenciaModel> findByColaborador_NomeCompletoContainingIgnoreCase(String nome);
    List<DiarioOcorrenciaModel> findByStatus(StatusOcorrencia status);

    public interface ColaboradorSeguroRepository extends JpaRepository<ColaboradorSeguroModel, Long> {
    }



    @Query("SELECT o FROM DiarioOcorrenciaModel o JOIN FETCH o.colaborador c " +
            "WHERE o.data BETWEEN :dataInicio AND :dataFim " +
            "AND (:tipo IS NULL OR o.tipo = :tipo)")
    List<DiarioOcorrenciaModel> findByDataBetweenAndTipoFetchColaborador(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("tipo") TipoOcorrencia tipo
    );


    @Query("SELECT o FROM DiarioOcorrenciaModel o JOIN FETCH o.colaborador c WHERE o.data BETWEEN :dataInicio AND :dataFim")
    List<DiarioOcorrenciaModel> findByDataBetweenFetchColaborador(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);

    @Query("SELECT o FROM DiarioOcorrenciaModel o JOIN FETCH o.colaborador c WHERE o.data = :data")
    List<DiarioOcorrenciaModel> findByDataFetchColaborador(@Param("data") LocalDate data);
}
