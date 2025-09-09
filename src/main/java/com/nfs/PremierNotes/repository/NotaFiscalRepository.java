package com.nfs.PremierNotes.repository;

import com.nfs.PremierNotes.models.NotaFiscalModel;
import com.nfs.PremierNotes.models.TomadorModel;
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
    List<NotaFiscalModel> findByTomadorModel(TomadorModel tomadorModel, Sort sort);


    @Query("SELECT DISTINCT n.tomadorModel.nome FROM NotaFiscalModel n WHERE n.tomadorModel.nome IS NOT NULL AND n.tomadorModel.nome != '' ORDER BY n.tomadorModel.nome ASC")
    List<String> findDistinctTomadores();

    @Query("SELECT DISTINCT YEAR(n.dataEmissao) FROM NotaFiscalModel n ORDER BY YEAR(n.dataEmissao) DESC")
    List<Integer> findDistinctAnos();

    @Query("SELECT n FROM NotaFiscalModel n WHERE YEAR(n.dataEmissao) = :ano")
    List<NotaFiscalModel> findByAno(Integer ano, Sort sort);
}