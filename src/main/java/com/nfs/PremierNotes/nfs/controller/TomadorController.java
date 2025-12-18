package com.nfs.PremierNotes.nfs.controller;

import com.nfs.PremierNotes.nfs.dto.TomadoresFormWrapper; // Importe o DTO que você criou
import com.nfs.PremierNotes.nfs.models.TomadorModel;
import com.nfs.PremierNotes.nfs.service.TomadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tomadores")
public class TomadorController {

    @Autowired
    private TomadorService tomadorService;

    // --- LISTAGEM (PREPARA A TELA DE EDIÇÃO EM MASSA) ---
    @GetMapping
    public String gerenciarTomadores(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String filtroAtivo,
            Model model) {

        List<TomadorModel> tomadores;

        // Mantivemos sua lógica de filtros original
        if (busca != null && !busca.trim().isEmpty()) {
            tomadores = tomadorService.buscarTomadoresPorNomeParcial(busca);
        } else if (filtroAtivo != null && !filtroAtivo.isEmpty()) {
            Boolean ativo = Boolean.parseBoolean(filtroAtivo);
            tomadores = tomadorService.buscarPorStatus(ativo);
        } else {
            tomadores = tomadorService.listarTodosTomadores();
        }

        // NOVO: Embrulha a lista no DTO para o Thymeleaf conseguir editar todos de uma vez
        TomadoresFormWrapper formWrapper = new TomadoresFormWrapper();
        formWrapper.setTomadores(tomadores);

        model.addAttribute("formWrapper", formWrapper); // Objeto principal da tabela
        model.addAttribute("novoTomador", new TomadorModel()); // Objeto para o Modal de cadastro
        model.addAttribute("buscaAtual", busca);
        model.addAttribute("filtroAtivo", filtroAtivo);

        return "nfs/gerenciar-tomadores";
    }

    // --- AÇÃO: SALVAR TODOS (EDIÇÃO EM MASSA) ---
    @PostMapping("/salvar-todos")
    public String salvarTodos(@ModelAttribute TomadoresFormWrapper formWrapper, RedirectAttributes redirectAttributes) {
        try {
            if (formWrapper.getTomadores() != null) {
                for (TomadorModel tomador : formWrapper.getTomadores()) {
                    // O JPA identifica pelo ID que é uma atualização
                    tomadorService.salvarTomador(tomador);
                }
            }
            redirectAttributes.addFlashAttribute("success", "Todas as alterações foram salvas com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao salvar alterações: " + e.getMessage());
        }
        return "redirect:/tomadores";
    }

    // --- AÇÃO: NOVO TOMADOR (VIA MODAL) ---
    @PostMapping("/novo")
    public String novoTomador(@ModelAttribute TomadorModel novoTomador, RedirectAttributes redirectAttributes) {
        try {
            tomadorService.salvarTomador(novoTomador);
            redirectAttributes.addFlashAttribute("success", "Tomador '" + novoTomador.getNome() + "' cadastrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao cadastrar: " + e.getMessage());
        }
        return "redirect:/tomadores";
    }
}