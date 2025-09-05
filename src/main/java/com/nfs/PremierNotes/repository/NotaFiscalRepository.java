package com.nfs.PremierNotes.repository;

import com.nfs.PremierNotes.models.NotaFiscalModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface NotaFiscalRepository extends JpaRepository<NotaFiscalModel, Long> {

    List<NotaFiscalModel> findByTomador(String tomador, Sort sort);
    List<NotaFiscalModel> findAll(Sort sort);
    List<NotaFiscalModel> findByStatusPagamento(String statusPagamento, Sort sort);

    @Query("SELECT DISTINCT n.tomador FROM NotaFiscalModel n WHERE n.tomador IS NOT NULL AND n.tomador != '' ORDER BY n.tomador ASC")
    List<String> findDistinctTomadores();

    @Query("SELECT DISTINCT YEAR(n.dataEmissao) FROM NotaFiscalModel n ORDER BY YEAR(n.dataEmissao) DESC")
    List<Integer> findDistinctAnos();

    @Query("SELECT n FROM NotaFiscalModel n WHERE YEAR(n.dataEmissao) = :ano")
    List<NotaFiscalModel> findByAno(Integer ano, Sort sort);
}