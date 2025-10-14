package com.nfs.PremierNotes.colaboradores.repository;

import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColaboradorSeguroRepository extends JpaRepository<ColaboradorSeguroModel, Long> {

    List<ColaboradorSeguroModel> findByAtivoNoSeguro(boolean ativo);
    List<ColaboradorSeguroModel> findByNomeCompleto(String nome);

    List<ColaboradorSeguroModel> findByAtivoNoSeguro(boolean ativo, SpringDataWebProperties.Sort sort);
    List<ColaboradorSeguroModel> findByNomeCompleto(String nome, SpringDataWebProperties.Sort sort);

    Optional<ColaboradorSeguroModel> findByCpf(String cpf);
    Optional<ColaboradorSeguroModel> findByCpfAndIdNot(String cpf, Long id);

}