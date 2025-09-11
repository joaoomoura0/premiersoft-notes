package com.nfs.PremierNotes.service;

import com.nfs.PremierNotes.models.NotaFiscalModel;
import com.nfs.PremierNotes.repository.NotaFiscalRepository;
import com.nfs.PremierNotes.models.TomadorModel;
import com.nfs.PremierNotes.repository.TomadorRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository repository;
    private final TomadorRepository tomadorRepository;

    public NotaFiscalService(NotaFiscalRepository repository, TomadorRepository tomadorRepository) {
        this.repository = repository;
        this.tomadorRepository = tomadorRepository;
    }

    public List<NotaFiscalModel> listarNotas() {
        return repository.findAll();
    }

    public NotaFiscalModel salvarNota(NotaFiscalModel nota) {
        if (nota.getTomador() == null || nota.getTomador().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do tomador não pode ser vazio.");
        }

        String nomeTomador = nota.getTomador().trim().toUpperCase();

        TomadorModel tomador = tomadorRepository.findByNome(nomeTomador)
                .orElseGet(() -> {
                    TomadorModel novoTomador = new TomadorModel(nomeTomador, 30);
                    return tomadorRepository.save(novoTomador);
                });

        nota.setTomadorModel(tomador);

        if (nota.getStatusPagamento() == null) {
            nota.setStatusPagamento("PENDENTE");
        }

        return repository.save(nota);
    }

    public void atualizarStatus(Long id, boolean status) {
        NotaFiscalModel nota = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota não encontrada"));

        if (status) {
            nota.setStatusPagamento("PAGO");
            nota.setDataPagamento(LocalDate.now());
        } else {
            nota.setStatusPagamento("PENDENTE");
            nota.setDataPagamento(null);
        }
        repository.save(nota);
    }

    public void calcularDetalhesDePrazo(NotaFiscalModel nota) {
        if (nota.getDataEmissao() == null || nota.getTomadorModel() == null) {
            return;
        }

        Integer prazoDias = nota.getTomadorModel().getPrazoPagamentoDias();
        if (prazoDias == null || prazoDias <= 0) {
            prazoDias = 30;
        }

        final int DIAS_PARA_ENTRAR_EM_ATENCAO = 7;
        LocalDate hoje = LocalDate.now();
        LocalDate dataVencimento = nota.getDataEmissao().plusDays(prazoDias);

        if ("PENDENTE".equals(nota.getStatusPagamento())) {
            long diasRestantes = ChronoUnit.DAYS.between(hoje, dataVencimento);
            nota.setDiasParaVencer(diasRestantes);

            if (diasRestantes < 0) {
                nota.setStatusPrazo("VENCIDO");
            } else if (diasRestantes <= DIAS_PARA_ENTRAR_EM_ATENCAO) {
                nota.setStatusPrazo("ATENCAO");
            } else {
                nota.setStatusPrazo("NO_PRAZO");
            }
        } else if ("PAGO".equals(nota.getStatusPagamento()) && nota.getDataPagamento() != null) {
            long diferencaDias = ChronoUnit.DAYS.between(nota.getDataPagamento(), dataVencimento);
            nota.setDiasPagamento(diferencaDias);
        }
    }
}