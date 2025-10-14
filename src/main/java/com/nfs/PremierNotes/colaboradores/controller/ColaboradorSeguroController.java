package com.nfs.PremierNotes.colaboradores.controller;

import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import com.nfs.PremierNotes.colaboradores.service.ColaboradorSeguroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/seguro")
public class ColaboradorSeguroController {

    @Autowired
    private ColaboradorSeguroService seguroService;
    @Autowired
    private ColaboradorSeguroService colaboradorSeguroService;

    @GetMapping
    public String listarColaboradores(Model model) {
        List<ColaboradorSeguroModel> colaboradores = seguroService.listarColaboradoresAtivos();
        model.addAttribute("colaboradores", colaboradores);
        model.addAttribute("activePage", "seguro");
        return "listaColaboradores";
    }

    @GetMapping("/todos")
    public String listarTodosColaboradores(Model model) {
        List<ColaboradorSeguroModel> colaboradores = seguroService.listarTodosColaboradores();
        model.addAttribute("colaboradores", colaboradores);
        model.addAttribute("activePage", "seguro");
        model.addAttribute("filtro", "todos");
        return "listaColaboradores";
    }

    @GetMapping("/inativos")
    public String listarColaboradoresInativos(Model model) {
        List<ColaboradorSeguroModel> colaboradores = seguroService.listarColaboradoresInativos();
        model.addAttribute("colaboradores", colaboradores);
        model.addAttribute("activePage", "seguro");
        model.addAttribute("filtro", "inativos");
        return "listaColaboradores";
    }


    @GetMapping("/novo")
    public String exibirFormularioNovoColaborador(Model model) {
        model.addAttribute("colaborador", new ColaboradorSeguroModel());
        model.addAttribute("activePage", "seguro");
        return "formColaborador";
    }

    @PostMapping("/salvar")
    public String salvarColaborador(@Valid @ModelAttribute("colaborador") ColaboradorSeguroModel colaborador,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {

        if (result.hasErrors()) {
            model.addAttribute("activePage", "seguro");
            return "formColaborador";
        }

        try {
            seguroService.salvarColaborador(colaborador);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Colaborador salvo com sucesso!");
            return "redirect:/seguro";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("activePage", "seguro");
            return "formColaborador";
        }
    }

    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicaoColaborador(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ColaboradorSeguroModel> colaboradorOptional = seguroService.buscarColaboradorPorId(id);
        if (colaboradorOptional.isPresent()) {
            model.addAttribute("colaborador", colaboradorOptional.get());
            model.addAttribute("activePage", "seguro");
            return "formColaborador";
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Colaborador não encontrado para edição.");
            return "redirect:/seguro";
        }
    }

    @PostMapping("/inativar/{id}")
    public String inativarColaborador(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (seguroService.inativarColaborador(id)) {
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Colaborador inativado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao inativar colaborador. Não encontrado ou já inativo.");
        }
        return "redirect:/seguro";
    }

    @PostMapping("/reativar/{id}")
    public String reativarColaborador(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (seguroService.reativarColaborador(id)) {
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Colaborador reativado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao reativar colaborador. Não encontrado ou já ativo.");
        }
        return "redirect:/seguro";
    }

    @PostMapping("/remover/{id}")
    public String removerColaboradorFisicamente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (seguroService.removerColaboradorFisicamente(id)) {
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Colaborador removido permanentemente!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao remover colaborador. Não encontrado.");
        }
        return "redirect:/seguro";
    }

    // filtro colaborador

    @GetMapping
    public String listarColaboradoresSeguro(
            @RequestParam(value = "filtroColaborador", required = false) String filtroColaborador,
            @RequestParam(value = "filtroAtivo", required = false) Boolean filtroAtivo,
            @RequestParam(defaultValue = "nomeCompleto") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            Model model) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        List<ColaboradorSeguroModel> colaboradores;


        if (filtroColaborador != null && !filtroColaborador.isEmpty()) {

            colaboradores = colaboradorSeguroService.buscarColaboradoresPorNome(filtroColaborador, sort);
        } else if (filtroAtivo != null) {

            colaboradores = colaboradorSeguroService.buscarColaboradoresPorStatusAtivo(filtroAtivo, sort);
        } else {

            colaboradores = colaboradorSeguroService.listarTodosColaboradores(sort);
        }

        model.addAttribute("colaboradores", colaboradores);

        List<String> nomeCompletosParaFiltro = colaboradorSeguroService.getNomesCompletosDosColaboradores();
        model.addAttribute("nomeCompletos", nomeCompletosParaFiltro);
        model.addAttribute("filtroColaborador", filtroColaborador);
        model.addAttribute("filtroAtivo", filtroAtivo);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDirection", sortDirection);

        return "listaColaboradores";
    }
}