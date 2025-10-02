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

    public Optional<TomadorModel> buscarTomadorPorNome(String nome) {
        return tomadorRepository.findByNomeIgnoreCase(nome);
    }

    public List<TomadorModel> buscarTomadoresPorNomeParcial(String busca) {
        if (busca == null || busca.trim().isEmpty()) {
            return tomadorRepository.findAll();
        }
        return tomadorRepository.findByNomeContainingIgnoreCase(busca.trim());
    }

    @Transactional
    public TomadorModel salvarTomador(TomadorModel tomador) {
        if (tomador.getNome() != null) {
            tomador.setNome(tomador.getNome().trim().toUpperCase());
        }
        return tomadorRepository.save(tomador);
    }

    public List<TomadorModel> buscarPorStatus(Boolean ativo) {
        if (ativo == null) {
            return tomadorRepository.findAll();
        }
        return tomadorRepository.findByAtivo(ativo);
    }

    @Transactional
    public TomadorModel criarOuAtualizarTomador(String nomeTomador, Integer prazo, Boolean ativo) {
        if (nomeTomador == null || nomeTomador.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do tomador não pode ser nulo ou vazio.");
        }
        String nomeNormalizado = nomeTomador.trim().toUpperCase();
        Optional<TomadorModel> tomadorOptional = tomadorRepository.findByNomeIgnoreCase(nomeNormalizado);

        TomadorModel tomador;
        if (tomadorOptional.isPresent()) {
            tomador = tomadorOptional.get();
        } else {
            tomador = new TomadorModel();
            tomador.setNome(nomeNormalizado);
        }

        tomador.setPrazoPagamentoDias(prazo != null ? prazo : tomador.getPrazoPagamentoDias());
        tomador.setAtivo(ativo != null ? ativo : tomador.getAtivo());
        return tomadorRepository.save(tomador);
    }

    public Optional<TomadorModel> buscarTomadorPorId(Long id) {
        return tomadorRepository.findById(id);
    }

    @Transactional
    public TomadorModel atualizarTomador(TomadorModel tomadorAtualizado) {
        return tomadorRepository.findById(tomadorAtualizado.getId())
                .map(tomadorExistente -> {
                    tomadorExistente.setPrazoPagamentoDias(tomadorAtualizado.getPrazoPagamentoDias());
                    tomadorExistente.setAtivo(tomadorAtualizado.getAtivo());
                    return tomadorRepository.save(tomadorExistente);
                }).orElseThrow(() -> new RuntimeException("Tomador não encontrado com ID: " + tomadorAtualizado.getId()));
    }
}