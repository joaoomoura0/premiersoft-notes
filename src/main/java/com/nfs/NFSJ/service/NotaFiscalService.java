package com.nfs.NFSJ.service;

import com.nfs.NFSJ.models.NotaFiscalModel;
import com.nfs.NFSJ.repository.NotaFiscalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    // Seu método salvarNota (mantido como estava)
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

        if (status) { // Se o novo status é PAGO
            nota.setStatusPagamento("PAGO");
            nota.setDataPagamento(LocalDate.now()); // PONTO-CHAVE: Registra a data do pagamento
        } else { // Se o novo status é PENDENTE
            nota.setStatusPagamento("PENDENTE");
            nota.setDataPagamento(null); // PONTO-CHAVE: Limpa a data do pagamento
        }
        repository.save(nota);
    }

    public void calcularDetalhesDePrazo(NotaFiscalModel nota) {
        // --- REGRAS DE NEGÓCIO (Fácil de alterar aqui) ---
        final int PRAZO_PADRAO_EM_DIAS = 30;
        final int DIAS_PARA_ENTRAR_EM_ATENCAO = 7;
        // ------------------------------------------------

        if (nota.getDataEmissao() == null) {
            return; // Se não tem data de emissão, não há o que calcular
        }

        LocalDate hoje = LocalDate.now();
        LocalDate dataVencimento = nota.getDataEmissao().plusDays(PRAZO_PADRAO_EM_DIAS);

        // --- LÓGICA PARA NOTAS PENDENTES ---
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
        // --- LÓGICA PARA NOTAS PAGAS ---
        else if ("PAGO".equals(nota.getStatusPagamento()) && nota.getDataPagamento() != null) {
            long diferencaDias = ChronoUnit.DAYS.between(nota.getDataPagamento(), dataVencimento);
            nota.setDiasPagamento(diferencaDias);
        }
    }
}