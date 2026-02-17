package com.nfs.PremierNotes.Apontamento.Repository;

import com.nfs.PremierNotes.Apontamento.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

    public interface ClienteRepository extends JpaRepository<Cliente, Long>{

        Optional<Cliente> findByNomeCliente (String nome);

    }

