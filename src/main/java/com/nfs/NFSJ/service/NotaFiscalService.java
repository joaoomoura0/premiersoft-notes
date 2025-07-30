package com.nfs.NFSJ.service;

import com.nfs.NFSJ.models.NotaFiscalModel;
import com.nfs.NFSJ.repository.NotaFiscalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository repository;

    // Usando injeção de dependência via construtor (melhor prática)
    public NotaFiscalService(NotaFiscalRepository repository) {
        this.repository = repository;
    }

    public List<NotaFiscalModel> listarNotas() {
        return repository.findAll();
    }

    // MODIFICAÇÃO AQUI: Padroniza o campo 'tomador' para maiúsculas antes de salvar.
    public NotaFiscalModel salvarNota(NotaFiscalModel nota) {
        if (nota.getTomador() != null && !nota.getTomador().trim().isEmpty()) {
            nota.setTomador(nota.getTomador().trim().toUpperCase());
        }
        return repository.save(nota);
    }
}