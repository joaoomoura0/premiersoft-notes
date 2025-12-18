package com.nfs.PremierNotes.nfs.repository;

import com.nfs.PremierNotes.nfs.models.TomadorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TomadorRepository extends JpaRepository<TomadorModel, Long> {

    // OBRIGATÓRIO: Usado na importação para achar o cliente sem ligar para maiúsculas/minúsculas
    Optional<TomadorModel> findByNomeIgnoreCase(String nome);

    // Usado para busca/autocomplete
    List<TomadorModel> findByNomeContainingIgnoreCase(String nome);

    // Usado para listar em dropdowns (ordem alfabética)
    List<TomadorModel> findByAtivoTrueOrderByNomeAsc();

    // Filtro administrativo
    List<TomadorModel> findByAtivo(Boolean ativo);
}