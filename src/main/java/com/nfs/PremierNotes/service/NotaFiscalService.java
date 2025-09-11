package com.nfs.PremierNotes.service;

import com.nfs.PremierNotes.models.NotaFiscalModel;
import com.nfs.PremierNotes.models.TomadorModel;
import com.nfs.PremierNotes.repository.NotaFiscalRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository repository;
    private final TomadorService tomadorService;

    public NotaFiscalService(NotaFiscalRepository repository, TomadorService tomadorService) {
        this.repository = repository;
        this.tomadorService = tomadorService;
    }

    public List<NotaFiscalModel> listarNotas() {
        return repository.findAll();
    }

    public NotaFiscalModel salvarNota(NotaFiscalModel nota) {
        if (nota.getTomador() != null && !nota.getTomador().trim().isEmpty()) {
            String nomeTomadorNormalizado = nota.getTomador().trim().toUpperCase();
            nota.setTomador(nomeTomadorNormalizado);

            Optional<TomadorModel> tomadorOptional = tomadorService.buscarTomadorPorNome(nomeTomadorNormalizado);
            TomadorModel tomadorConfig;

            if (tomadorOptional.isPresent()) {
                tomadorConfig = tomadorOptional.get();
            } else {
                tomadorConfig = new TomadorModel();
                tomadorConfig.setNome(nomeTomadorNormalizado);
                tomadorConfig.setPrazoPagamentoDias(30);
                tomadorConfig.setAtivo(true);
                tomadorConfig = tomadorService.salvarTomador(tomadorConfig);
            }

            nota.setPrazoPagamentoDias(tomadorConfig.getPrazoPagamentoDias());

        } else {
            nota.setPrazoPagamentoDias(30);
        }

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
        Integer prazoDias = nota.getPrazoPagamentoDias();

        if (prazoDias == null || prazoDias <= 0) {
            prazoDias = 30;
        }

        final int DIAS_PARA_ENTRAR_EM_ATENCAO = 7;

        if (nota.getDataEmissao() == null) {
            return;
        }

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
        }
        else if ("PAGO".equals(nota.getStatusPagamento()) && nota.getDataPagamento() != null) {
            long diferencaDias = ChronoUnit.DAYS.between(nota.getDataPagamento(), dataVencimento);
            nota.setDiasPagamento(diferencaDias);
        }
    }
}