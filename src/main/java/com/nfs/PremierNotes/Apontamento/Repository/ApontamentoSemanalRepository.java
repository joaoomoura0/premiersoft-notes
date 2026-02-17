package com.nfs.PremierNotes.Apontamento.Repository;

import com.nfs.PremierNotes.Apontamento.Model.ApontamentoSemanal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ApontamentoSemanalRepository  extends JpaRepository<ApontamentoSemanal, Long> {

    void deleteByDataBetween(LocalDate inicio, LocalDate fim);

}
