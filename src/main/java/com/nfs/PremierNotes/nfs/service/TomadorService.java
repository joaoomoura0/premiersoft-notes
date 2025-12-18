package com.nfs.PremierNotes.nfs.service;

import com.nfs.PremierNotes.nfs.models.TomadorModel;
import com.nfs.PremierNotes.nfs.repository.TomadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TomadorService {

    private final TomadorRepository tomadorRepository;

    public TomadorService(TomadorRepository tomadorRepository) {
        this.tomadorRepository = tomadorRepository;
    }


    public List<TomadorModel> listarTodosTomadores() {
        return tomadorRepository.findAll();
    }

    public Optional<TomadorModel> buscarTomadorPorId(Long id) {
        return tomadorRepository.findById(id);
    }

    public List<TomadorModel> buscarTomadoresPorNomeParcial(String busca) {
        if (busca == null || busca.trim().isEmpty()) {
            return tomadorRepository.findAll();
        }
        return tomadorRepository.findByNomeContainingIgnoreCase(busca.trim());
    }

    public List<TomadorModel> buscarPorStatus(Boolean ativo) {
        if (ativo == null) {
            return tomadorRepository.findAll();
        }
        return tomadorRepository.findByAtivo(ativo);
    }

    @Transactional
    public TomadorModel buscarOuCriarTomador(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            nome = "GENERICO"; // Fallback de segurança
        }

        String nomeNormalizado = nome.trim().toUpperCase();

        return tomadorRepository.findByNomeIgnoreCase(nomeNormalizado)
                .orElseGet(() -> {
                    TomadorModel novo = new TomadorModel();
                    novo.setNome(nomeNormalizado);
                    return tomadorRepository.save(novo);
                });
    }

    @Transactional
    public TomadorModel salvarTomador(TomadorModel tomador) {
        if (tomador.getNome() != null) {
            tomador.setNome(tomador.getNome().trim().toUpperCase());
        }
        return tomadorRepository.save(tomador);
    }

    @Transactional
    public TomadorModel atualizarTomador(TomadorModel tomadorAtualizado) {
        return tomadorRepository.findById(tomadorAtualizado.getId())
                .map(tomadorExistente -> {
                    tomadorExistente.setPrazoPagamentoDias(tomadorAtualizado.getPrazoPagamentoDias());
                    tomadorExistente.setAtivo(tomadorAtualizado.getAtivo());
                    return tomadorRepository.save(tomadorExistente);
                }).orElseThrow(() -> new RuntimeException("Tomador não encontrado ID: " + tomadorAtualizado.getId()));
    }
}