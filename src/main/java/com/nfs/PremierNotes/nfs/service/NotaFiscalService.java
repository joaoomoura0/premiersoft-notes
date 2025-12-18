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
        return repository.findAll(); // Sugestão: OrderByDataEmissaoDesc() no repositório fica melhor
    }

    /**
     * Processa a lista vinda do ExcelHelper
     */
    @Transactional
    public void salvarNotasImportadas(List<NotaFiscalModel> notasImportadas) {
        System.out.println("Processando " + notasImportadas.size() + " notas...");

        for (NotaFiscalModel nota : notasImportadas) {
            try {
                // 1. Verificação de Duplicidade
                // Evita salvar a mesma nota se rodar a importação 2 vezes
                // Precisamos criar esse método no Repository
                boolean existe = repository.existsByDataEmissaoAndCnpjTomadorAndValorNF(
                        nota.getDataEmissao(),
                        nota.getCnpjTomador(),
                        nota.getValorNF()
                );

                if (existe) {
                    System.out.println("Nota duplicada ignorada: " + nota.getNomeTomadorString() + " - " + nota.getValorNF());
                    continue; // Pula para a próxima
                }

                // 2. Vincula o Tomador (Busca ou Cria)
                TomadorModel tomador = tomadorService.buscarOuCriarTomador(nota.getNomeTomadorString());
                nota.setTomador(tomador);

                // Define o prazo baseando-se no tomador (se a nota não tiver vindo com prazo específico)
                if (nota.getPrazoPagamentoDias() == null) {
                    nota.setPrazoPagamentoDias(tomador.getPrazoPagamentoDias());
                }

                // 3. Regras de Negócio para Status
                if (nota.isCancelada()) {
                    nota.setStatusPagamento("CANCELADO");
                    nota.setDataPagamento(null);
                } else {
                    // Se for normal, nasce como PENDENTE
                    if (nota.getStatusPagamento() == null) {
                        nota.setStatusPagamento("PENDENTE");
                    }
                }

                repository.save(nota);

            } catch (Exception e) {
                System.err.println("Erro ao salvar nota individual: " + e.getMessage());
                // Não damos throw aqui para não cancelar a importação inteira por causa de uma nota ruim
            }
        }
    }

    // Mantido para cadastros manuais
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

    // Método chamado pelo Controller antes de enviar para o Front
    public void calcularDetalhesDePrazo(NotaFiscalModel nota) {
        // Notas canceladas não têm cálculo de prazo
        if (nota.isCancelada() || "CANCELADO".equals(nota.getStatusPagamento())) {
            nota.setStatusPrazo("CANCELADO");
            return;
        }

        Integer prazoDias = (nota.getPrazoPagamentoDias() != null && nota.getPrazoPagamentoDias() > 0)
                ? nota.getPrazoPagamentoDias() : 30;

        if (nota.getDataEmissao() == null) return;

        LocalDate hoje = LocalDate.now();
        LocalDate dataVencimento = nota.getDataEmissao().plusDays(prazoDias);

        // Define dias para vencer (transient)
        long diasRestantes = ChronoUnit.DAYS.between(hoje, dataVencimento);
        nota.setDiasParaVencer(diasRestantes);

        if ("PAGO".equals(nota.getStatusPagamento()) && nota.getDataPagamento() != null) {
            nota.setStatusPrazo("PAGO");
            // Calcula se pagou adiantado ou atrasado em relação ao vencimento
            long diferenca = ChronoUnit.DAYS.between(nota.getDataPagamento(), dataVencimento);
            nota.setDiasPagamento(diferenca);
        } else {
            // Lógica PENDENTE
            if (diasRestantes < 0) {
                nota.setStatusPrazo("VENCIDO");
            } else if (diasRestantes <= 7) { // 7 Dias para alerta
                nota.setStatusPrazo("ATENCAO");
            } else {
                nota.setStatusPrazo("NO_PRAZO");
            }
        }
    }
}