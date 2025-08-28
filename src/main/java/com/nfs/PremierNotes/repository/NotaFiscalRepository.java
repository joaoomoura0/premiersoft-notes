package com.nfs.PremierNotes.repository;

import com.nfs.PremierNotes.models.NotaFiscalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscalModel, Long> {

    List<NotaFiscalModel> findByTomador(String tomador);

    List<NotaFiscalModel> findByStatusPagamento(String statusPagamento);

    @Query("SELECT DISTINCT UPPER(TRIM(n.tomador)) FROM NotaFiscalModel n ORDER BY UPPER(TRIM(n.tomador)) ASC")
    List<String> findDistinctTomadores();
}