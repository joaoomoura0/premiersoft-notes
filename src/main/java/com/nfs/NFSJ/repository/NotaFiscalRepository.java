package com.nfs.NFSJ.repository;

import com.nfs.NFSJ.models.NotaFiscalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscalModel, Long> {

    // Buscar notas por tomador
    List<NotaFiscalModel> findByTomador(String tomador);

    // Buscar tomadores únicos para o filtro
    @Query("SELECT DISTINCT UPPER(TRIM(n.tomador)) FROM NotaFiscalModel n ORDER BY UPPER(TRIM(n.tomador)) ASC")
    List<String> findDistinctTomadores();
}
