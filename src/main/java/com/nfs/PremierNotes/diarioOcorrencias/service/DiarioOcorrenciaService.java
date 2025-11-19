package com.nfs.PremierNotes.diarioOcorrencias.service;

import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import com.nfs.PremierNotes.colaboradores.service.ColaboradorSeguroService;
import com.nfs.PremierNotes.diarioOcorrencias.model.DiarioOcorrenciaModel;
import com.nfs.PremierNotes.diarioOcorrencias.model.StatusOcorrencia;
import com.nfs.PremierNotes.diarioOcorrencias.model.TipoOcorrencia;
import com.nfs.PremierNotes.diarioOcorrencias.repository.DiarioOcorrenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void salvarOcorrenciaPorPeriodo(Long colaboradorId, TipoOcorrencia tipo,
                                           LocalDate dataInicio, LocalDate dataFim,
                                           StatusOcorrencia status, String descricao,
                                           String account, String origemTipo, String clockifyCliente) {

        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data de início não pode ser posterior à data de fim.");
        }

        ColaboradorSeguroModel colaborador = colaboradorService.buscarColaboradorPorId(colaboradorId)
                .orElseThrow(() -> new IllegalArgumentException("Colaborador não encontrado."));

        if (colaborador == null) {
            throw new IllegalArgumentException("Colaborador não encontrado.");
        }

        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)) {

            DiarioOcorrenciaModel novaOcorrencia = new DiarioOcorrenciaModel();

            novaOcorrencia.setColaborador(colaborador);
            novaOcorrencia.setTipo(tipo);
            novaOcorrencia.setStatus(status);
            novaOcorrencia.setDescricao(descricao);

            novaOcorrencia.setData(dataAtual);

            novaOcorrencia.setAccount(account);

            String origemCompleta = origemTipo;
            if ("Clockify".equals(origemTipo) && clockifyCliente != null && !clockifyCliente.isEmpty()) {
                origemCompleta += " - " + clockifyCliente;
            }
            novaOcorrencia.setOrigem(origemCompleta);

            repository.save(novaOcorrencia);

            dataAtual = dataAtual.plusDays(1);
        }
    }

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

    public List<DiarioOcorrenciaModel> buscarOcorrenciasDoMes(int mes, int ano, TipoOcorrencia tipo) {
        YearMonth anoMes = YearMonth.of(ano, mes);
        LocalDate dataInicio = anoMes.atDay(1);
        LocalDate dataFim = anoMes.atEndOfMonth();

        return repository.findByDataBetweenAndTipoFetchColaborador(dataInicio, dataFim, tipo);
    }

    public List<DiarioOcorrenciaModel> buscarOcorrenciasDoDia(String dataString) {
        LocalDate data = LocalDate.parse(dataString, DateTimeFormatter.ISO_LOCAL_DATE);
        return repository.findByDataFetchColaborador(data);
    }

    public List<DiarioOcorrenciaModel> buscarOcorrenciasPorStatus(StatusOcorrencia status) {
        return repository.findByStatus(status);
    }

    private void validarOcorrencia(DiarioOcorrenciaModel ocorrencia) {

        if (ocorrencia.getColaborador() == null || ocorrencia.getColaborador().getId() == null) {
            throw new IllegalArgumentException("O colaborador deve ser selecionado.");
        }

        colaboradorService.buscarColaboradorPorId(ocorrencia.getColaborador().getId());

        if (ocorrencia.getDescricao() == null || ocorrencia.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }
    }
}
