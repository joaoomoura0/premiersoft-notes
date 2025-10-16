package com.nfs.PremierNotes.diarioOcorrencias.service;

import com.nfs.PremierNotes.colaboradores.service.ColaboradorSeguroService;
import com.nfs.PremierNotes.diarioOcorrencias.model.DiarioOcorrenciaModel;
import com.nfs.PremierNotes.diarioOcorrencias.model.StatusOcorrencia;
import com.nfs.PremierNotes.diarioOcorrencias.repository.DiarioOcorrenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DiarioOcorrenciaService {

    @Autowired
    private DiarioOcorrenciaRepository repository;

    @Autowired
    private ColaboradorSeguroService colaboradorService;

    public DiarioOcorrenciaModel salvarOcorrencia(DiarioOcorrenciaModel ocorrencia) {
        validarOcorrencia(ocorrencia);
        return repository.save(ocorrencia);
    }

    public DiarioOcorrenciaModel buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ocorrência não encontrada."));
    }

    public void removerOcorrencia(Long id) {
        repository.deleteById(id);
    }

    public List<DiarioOcorrenciaModel> buscarOcorrenciasDoMes(int mes, int ano) {
        YearMonth anoMes = YearMonth.of(ano, mes);
        LocalDate dataInicio = anoMes.atDay(1);
        LocalDate dataFim = anoMes.atEndOfMonth();

        return repository.findByDataBetween(dataInicio, dataFim);
    }

    public List<DiarioOcorrenciaModel> buscarOcorrenciasDoDia(String dataString) {
        LocalDate data = LocalDate.parse(dataString, DateTimeFormatter.ISO_LOCAL_DATE);
        return repository.findByData(data);
    }

    public List<DiarioOcorrenciaModel> buscarOcorrenciasPorStatus(StatusOcorrencia status) {
        return repository.findByStatus(status);
    }

    private void validarOcorrencia(DiarioOcorrenciaModel ocorrencia) {
        if (ocorrencia.getData() == null || ocorrencia.getData().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data da ocorrência é inválida (não pode ser futura).");
        }

        if (ocorrencia.getColaborador() == null || ocorrencia.getColaborador().getId() == null) {
            throw new IllegalArgumentException("O colaborador deve ser selecionado.");
        }

        colaboradorService.buscarColaboradorPorId(ocorrencia.getColaborador().getId());

        if (ocorrencia.getDescricao() == null || ocorrencia.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }
    }
}
