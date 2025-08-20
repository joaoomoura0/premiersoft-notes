package com.nfs.NFSJ.service;

import com.nfs.NFSJ.models.NotaFiscalModel;
import com.nfs.NFSJ.repository.NotaFiscalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository repository;

    public NotaFiscalService(NotaFiscalRepository repository) {
        this.repository = repository;
    }

    public List<NotaFiscalModel> listarNotas() {
        return repository.findAll();
    }

    public NotaFiscalModel salvarNota(NotaFiscalModel nota) {
        if (nota.getTomador() != null && !nota.getTomador().trim().isEmpty()) {
            nota.setTomador(nota.getTomador().trim().toUpperCase());
        }
        if (nota.getStatusPagamento() == null) {
            nota.setStatusPagamento("PENDENTE");
        }

        return repository.save(nota);
    }
    public void atualizarStatus(Long id, boolean status) {
        NotaFiscalModel nota = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota não encontrada"));
        nota.setStatusPagamento(status ? "PAGO" : "PENDENTE");
        repository.save(nota);
    }
}