package com.nfs.PremierNotes.nfs.repository;

import com.nfs.PremierNotes.nfs.models.NotaFiscalModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotaFiscalRepository extends JpaRepository<NotaFiscalModel, Long> {

    // --- IMPORTAÇÃO: Verifica duplicidade ---
    boolean existsByDataEmissaoAndCnpjTomadorAndValorNF(LocalDate dataEmissao, String cnpjTomador, Double valorNF);

    // --- FILTROS PARA OS DROPDOWNS DO HTML ---

    // Lista nomes únicos para o <select> de Tomadores
    @Query("SELECT DISTINCT n.tomador.nome FROM NotaFiscalModel n WHERE n.tomador IS NOT NULL ORDER BY n.tomador.nome ASC")
    List<String> findDistinctTomadores();

    // Lista anos únicos para o <select> de Anos
    @Query("SELECT DISTINCT YEAR(n.dataEmissao) FROM NotaFiscalModel n ORDER BY YEAR(n.dataEmissao) DESC")
    List<Integer> findDistinctAnos();

    // --- NOVA QUERY PODEROSA (FILTROS COMBINADOS) ---
    // Esta query substitui findByAno, findByTomador e findByStatusPagamento individuais.
    // Lógica: Se o parâmetro for NULL, ele ignora aquela parte do filtro (OR ... IS NULL).
    @Query("SELECT n FROM NotaFiscalModel n WHERE " +
            "(:nomeTomador IS NULL OR UPPER(n.tomador.nome) LIKE UPPER(CONCAT('%', :nomeTomador, '%'))) AND " +
            "(:statusPagamento IS NULL OR n.statusPagamento = :statusPagamento) AND " +
            "(:ano IS NULL OR YEAR(n.dataEmissao) = :ano)")
    List<NotaFiscalModel> buscarComFiltros(
            @Param("nomeTomador") String nomeTomador,
            @Param("statusPagamento") String statusPagamento,
            @Param("ano") Integer ano,
            Sort sort
    );

    // --- SUGESTÃO ---
    // Você pode manter o findAll padrão do JpaRepository, não precisa declarar.
    // Os métodos antigos (findByAno, etc) podem ser apagados se você atualizar o Controller.
}