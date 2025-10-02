package com.nfs.PremierNotes.nfs.repository;

import com.nfs.PremierNotes.nfs.models.TomadorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TomadorRepository extends JpaRepository<TomadorModel, Long> {

    Optional<TomadorModel> findByNomeIgnoreCase(String nome);
    List<TomadorModel> findByNomeContainingIgnoreCase(String nome);
    List<TomadorModel> findByAtivoTrueOrderByNomeAsc();
    List<TomadorModel> findByAtivo(Boolean ativo);


}