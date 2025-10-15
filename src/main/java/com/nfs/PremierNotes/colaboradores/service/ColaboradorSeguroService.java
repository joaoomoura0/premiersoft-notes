package com.nfs.PremierNotes.colaboradores.service;

import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import com.nfs.PremierNotes.colaboradores.repository.ColaboradorSeguroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ColaboradorSeguroService {

    @Autowired
    private ColaboradorSeguroRepository colaboradorSeguroRepository;

    public List<ColaboradorSeguroModel> listarTodosColaboradores() {
        return colaboradorSeguroRepository.findAll();
    }

    public List<ColaboradorSeguroModel> listarColaboradoresAtivos() {
        return colaboradorSeguroRepository.findByAtivoNoSeguro(true);
    }

    public List<ColaboradorSeguroModel> listarColaboradoresInativos() {
        return colaboradorSeguroRepository.findByAtivoNoSeguro(false);
    }

    public Optional<ColaboradorSeguroModel> buscarColaboradorPorId(Long id) {
        return colaboradorSeguroRepository.findById(id);
    }

    public ColaboradorSeguroModel salvarColaborador(ColaboradorSeguroModel colaborador) throws IllegalArgumentException {

        if (colaborador.getId() == null) {
            if (colaboradorSeguroRepository.findByCpf(colaborador.getCpf()).isPresent()) {
                throw new IllegalArgumentException("CPF já cadastrado para outro colaborador.");
            }
        } else {
            Optional<ColaboradorSeguroModel> existingColaboradorWithCpf = colaboradorSeguroRepository.findByCpfAndIdNot(colaborador.getCpf(), colaborador.getId());
            if (existingColaboradorWithCpf.isPresent()) {
                throw new IllegalArgumentException("CPF já cadastrado para outro colaborador.");
            }
        }

        if (colaborador.getDataNascimento() == null || colaborador.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de Nascimento inválida.");
        }
        if (colaborador.getDataAdmissao() == null || colaborador.getDataAdmissao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de Admissão inválida.");
        }

        if (colaborador.getId() == null) {
            colaborador.setAtivoNoSeguro(true);
        }

        return colaboradorSeguroRepository.save(colaborador);
    }

    public boolean inativarColaborador(Long id) {
        Optional<ColaboradorSeguroModel> colaboradorOptional = colaboradorSeguroRepository.findById(id);
        if (colaboradorOptional.isPresent()) {
            ColaboradorSeguroModel colaborador = colaboradorOptional.get();
            if (colaborador.getAtivoNoSeguro()) { // Só inativa se já não estiver inativo
                colaborador.setAtivoNoSeguro(false);
                colaboradorSeguroRepository.save(colaborador);
                return true;
            }
        }
        return false;
    }

    public boolean reativarColaborador(Long id) {
        Optional<ColaboradorSeguroModel> colaboradorOptional = colaboradorSeguroRepository.findById(id);
        if (colaboradorOptional.isPresent()) {
            ColaboradorSeguroModel colaborador = colaboradorOptional.get();
            if (!colaborador.getAtivoNoSeguro()) {
                colaborador.setAtivoNoSeguro(true);
                colaboradorSeguroRepository.save(colaborador);
                return true;
            }
        }
        return false;
    }

    public boolean removerColaboradorFisicamente(Long id) {
        if (colaboradorSeguroRepository.existsById(id)) {
            colaboradorSeguroRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public List<String> getNomesCompletosDosColaboradores() {
        return colaboradorSeguroRepository.findAll().stream()
                .map(colaborador -> colaborador.getNomeCompleto())
                .collect(Collectors.toList());
    }

    public List<ColaboradorSeguroModel> buscarColaboradoresPorNome(String nomeCompleto, Sort sort) {
        return colaboradorSeguroRepository.findByNomeCompleto(nomeCompleto, sort);
    }

    public List<ColaboradorSeguroModel> listarTodosColaboradores(Sort sort) {
        return colaboradorSeguroRepository.findAll(sort);
    }

    public List<ColaboradorSeguroModel> buscarColaboradoresPorStatusAtivo(Boolean ativo, Sort sort) {
        return colaboradorSeguroRepository.findByAtivoNoSeguro(ativo, sort);
    }
}