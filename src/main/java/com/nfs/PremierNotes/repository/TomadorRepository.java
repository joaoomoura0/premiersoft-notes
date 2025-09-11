package com.nfs.PremierNotes.repository; // Verifique seu pacote

import com.nfs.PremierNotes.models.TomadorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TomadorRepository extends JpaRepository<TomadorModel, Long> {

    Optional<TomadorModel> findByNome(String nome);

    List<TomadorModel> findByAtivoTrueOrderByNomeAsc(); // Para o dropdown de cadastro

    @Query("SELECT t FROM TomadorModel t WHERE t.nome LIKE %:nome%")
    List<TomadorModel> findByNomeContainingIgnoreCase(String nome);
}