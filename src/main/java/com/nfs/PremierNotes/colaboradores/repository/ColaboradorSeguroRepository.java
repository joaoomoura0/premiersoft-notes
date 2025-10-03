package com.nfs.PremierNotes.colaboradores.repository;

import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColaboradorSeguroRepository extends JpaRepository<ColaboradorSeguroModel, Long> {

    List<ColaboradorSeguroModel> findByAtivoNoSeguro(boolean ativo);
    Optional<ColaboradorSeguroModel> findByCpf(String cpf);
    Optional<ColaboradorSeguroModel> findByCpfAndIdNot(String cpf, Long id);
}