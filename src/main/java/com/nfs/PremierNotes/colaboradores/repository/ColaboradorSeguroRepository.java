package com.nfs.PremierNotes.colaboradores.repository;

import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColaboradorSeguroRepository extends JpaRepository<ColaboradorSeguroModel, Long> {

    List<ColaboradorSeguroModel> findByNomeCompleto(String nome, Sort sort);

    List<ColaboradorSeguroModel> findByTipoContrato(String tipoContrato, Sort sort);

    Optional<ColaboradorSeguroModel> findByCpf(String cpf);
    Optional<ColaboradorSeguroModel> findByCpfAndIdNot(String cpf, Long id);

    @Query("SELECT DISTINCT c.tipoContrato FROM ColaboradorSeguroModel c ORDER BY c.tipoContrato")
    List<String> findDistinctTipoContrato();

    @Query("SELECT c FROM ColaboradorSeguroModel c WHERE " +
            "(:nomeCompleto IS NULL OR LOWER(c.nomeCompleto) LIKE LOWER(CONCAT('%', :nomeCompleto, '%'))) AND " +
            "(:tipoContrato IS NULL OR LOWER(c.tipoContrato) LIKE LOWER(CONCAT('%', :tipoContrato, '%')))")
    List<ColaboradorSeguroModel> findByFilters(
            @Param("nomeCompleto") String nomeCompleto,
            @Param("tipoContrato") String tipoContrato,
            Sort sort);
}