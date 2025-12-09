package com.nfs.PremierNotes.colaboradores.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import com.nfs.PremierNotes.colaboradores.repository.ColaboradorSeguroRepository;
import com.nfs.PremierNotes.colaboradores.service.ColaboradorSeguroService;
import com.nfs.PremierNotes.diarioOcorrencias.repository.DiarioOcorrenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/seguro")
public class ColaboradorSeguroController {

    @Autowired
    private ColaboradorSeguroService colaboradorSeguroService;

    @GetMapping
    public String listarColaboradoresSeguro(
            @RequestParam(value = "filtroColaborador", required = false) String filtroColaborador,
            @RequestParam(value = "filtroCargo", required = false) String filtroCargo,
            @RequestParam(defaultValue = "nomeCompleto") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            Model model) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        List<ColaboradorSeguroModel> colaboradores;


        if (filtroColaborador != null && !filtroColaborador.isEmpty()) {
            colaboradores = colaboradorSeguroService.buscarColaboradoresPorNome(filtroColaborador, sort);

        } else if (filtroCargo != null && !filtroCargo.isEmpty()) {
            colaboradores = colaboradorSeguroService.buscarPorCargo(filtroCargo, sort);

        } else {
            colaboradores = colaboradorSeguroService.listarTodosColaboradores(sort);
        }

        model.addAttribute("colaboradores", colaboradores);

        List<String> nomeCompletosParaFiltro = colaboradorSeguroService.getNomesCompletosDosColaboradores();
        model.addAttribute("nomeCompletos", nomeCompletosParaFiltro);

        List<String> tipoContratosParaFiltro = colaboradorSeguroService.getTiposContratoDosColaboradores();
        model.addAttribute("tipoContratos", tipoContratosParaFiltro);

        model.addAttribute("filtroColaborador", filtroColaborador);
        model.addAttribute("filtroCargo", filtroCargo);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDirection", sortDirection);

        return "seguro/listaColaboradores";
    }

    @GetMapping("/novo")
    public String exibirFormularioNovoColaborador(Model model) {
        model.addAttribute("colaborador", new ColaboradorSeguroModel());
        model.addAttribute("activePage", "seguro");
        return "seguro/formColaborador";
    }

    @PostMapping("/salvar")
    public String salvarColaborador(@Valid @ModelAttribute("colaborador") ColaboradorSeguroModel colaborador,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {

        if (result.hasErrors()) {
            model.addAttribute("activePage", "seguro");
            return "seguro/formColaborador";
        }

        try {
            colaboradorSeguroService.salvarColaborador(colaborador);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Colaborador salvo com sucesso!");
            return "redirect:/seguro";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("activePage", "seguro");
            return "seguro/formColaborador";
        }
    }

    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicaoColaborador(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ColaboradorSeguroModel> colaboradorOptional = colaboradorSeguroService.buscarColaboradorPorId(id);
        if (colaboradorOptional.isPresent()) {
            model.addAttribute("colaborador", colaboradorOptional.get());
            model.addAttribute("activePage", "seguro");
            return "seguro/formColaborador";
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Colaborador não encontrado para edição.");
            return "redirect:/seguro";
        }
    }

    @PostMapping("/remover/{id}")
    public String removerColaboradorFisicamente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (colaboradorSeguroService.removerColaboradorFisicamente(id)) {
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Colaborador removido permanentemente!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao remover colaborador. Não encontrado.");
        }
        return "redirect:/seguro";
    }

    // adicionar colaboradores

    @RestController
    @RequestMapping("/colaboradores")
    public class ColaboradorImportController {

        @Autowired
        private ColaboradorSeguroRepository.colaboradorSeguroRepository repository;

        @PostMapping("/importar")
        public String importarColaboradores() throws Exception {
            InputStream inputStream = getClass().getResourceAsStream("/colaboradores.json");
            if (inputStream == null) {
                return "Arquivo NÃO encontrado";
            }

            ObjectMapper mapper = new ObjectMapper();
            List<ColaboradorSeguroModel> lista =
                    mapper.readValue(inputStream, new TypeReference<List<ColaboradorSeguroModel>>() {});

            repository.saveAll(lista);
            return "Importação concluída. Total: " + lista.size();
        }
    }

}