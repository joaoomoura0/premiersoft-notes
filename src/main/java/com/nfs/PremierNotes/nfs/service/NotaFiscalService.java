package com.nfs.PremierNotes.nfs.service;

import com.nfs.PremierNotes.nfs.models.NotaFiscalModel;
import com.nfs.PremierNotes.nfs.models.TomadorModel;
import com.nfs.PremierNotes.nfs.repository.NotaFiscalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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


    @Transactional
    public void salvarNotasImportadas(List<NotaFiscalModel> notasImportadas) {
        System.out.println("Processando " + notasImportadas.size() + " notas...");

        for (NotaFiscalModel nota : notasImportadas) {
            try {

                boolean existe = repository.existsByDataEmissaoAndCnpjTomadorAndValorNF(
                        nota.getDataEmissao(),
                        nota.getCnpjTomador(),
                        nota.getValorNF()
                );

                if (existe) {
                    System.out.println("Nota duplicada ignorada: " + nota.getNomeTomadorString() + " - " + nota.getValorNF());
                    continue;
                }

                TomadorModel tomador = tomadorService.buscarOuCriarTomador(nota.getNomeTomadorString());
                nota.setTomador(tomador);

                if (nota.getPrazoPagamentoDias() == null) {
                    nota.setPrazoPagamentoDias(tomador.getPrazoPagamentoDias());
                }

                if (nota.isCancelada()) {
                    nota.setStatusPagamento("CANCELADO");
                    nota.setDataPagamento(null);
                } else {
                    if (nota.getStatusPagamento() == null) {
                        nota.setStatusPagamento("PENDENTE");
                    }
                }

                repository.save(nota);

            } catch (Exception e) {
                System.err.println("Erro ao salvar nota individual: " + e.getMessage());
            }
        }
    }

    @Transactional
    public NotaFiscalModel salvarNotaManual(NotaFiscalModel nota) {
        // Lógica similar à importação, mas para uma única nota
        if (nota.getTomador() == null && nota.getNomeTomadorString() != null) {
            TomadorModel t = tomadorService.buscarOuCriarTomador(nota.getNomeTomadorString());
            nota.setTomador(t);
        }

        if (nota.getStatusPagamento() == null) {
            nota.setStatusPagamento("PENDENTE");
        }
        return repository.save(nota);
    }

    public void atualizarStatusPagamento(Long id, boolean pago) {
        NotaFiscalModel nota = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota não encontrada"));

        if (pago) {
            nota.setStatusPagamento("PAGO");
            nota.setDataPagamento(LocalDate.now());
        } else {
            nota.setStatusPagamento("PENDENTE");
            nota.setDataPagamento(null);
        }
        repository.save(nota);
    }

    public void calcularDetalhesDePrazo(NotaFiscalModel nota) {
        if (nota.isCancelada() || "CANCELADO".equals(nota.getStatusPagamento())) {
            nota.setStatusPrazo("CANCELADO");
            return;
        }

        Integer prazoDias = (nota.getPrazoPagamentoDias() != null && nota.getPrazoPagamentoDias() > 0)
                ? nota.getPrazoPagamentoDias() : 30;

        if (nota.getDataEmissao() == null) return;

        LocalDate hoje = LocalDate.now();
        LocalDate dataVencimento = nota.getDataEmissao().plusDays(prazoDias);


        long diasRestantes = ChronoUnit.DAYS.between(hoje, dataVencimento);
        nota.setDiasParaVencer(diasRestantes);

        if ("PAGO".equals(nota.getStatusPagamento()) && nota.getDataPagamento() != null) {
            nota.setStatusPrazo("PAGO");
            long diferenca = ChronoUnit.DAYS.between(nota.getDataPagamento(), dataVencimento);
            nota.setDiasPagamento(diferenca);
        } else {
            if (diasRestantes < 0) {
                nota.setStatusPrazo("VENCIDO");
            } else if (diasRestantes <= 7) {
                nota.setStatusPrazo("ATENCAO");
            } else {
                nota.setStatusPrazo("NO_PRAZO");
            }
        }
    }
}