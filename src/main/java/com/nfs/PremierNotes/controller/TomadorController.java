package com.nfs.PremierNotes.controller;

import com.nfs.PremierNotes.models.TomadorModel;
import com.nfs.PremierNotes.service.TomadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tomadores")
public class TomadorController {

    @Autowired
    private TomadorService tomadorService;

    @GetMapping
    public String gerenciarTomadores(@RequestParam(required = false) String busca, Model model) {
        List<TomadorModel> tomadores;
        if (busca != null && !busca.trim().isEmpty()) {
            tomadores = tomadorService.buscarTomadoresPorNomeParcial(busca);
        } else {
            tomadores = tomadorService.listarTodosTomadores();
        }
        model.addAttribute("tomadores", tomadores);
        model.addAttribute("buscaAtual", busca);
        return "gerenciar-tomadores"; // Nome do seu arquivo HTML (sem a extensão)
    }

    @PostMapping("/atualizar") // Mapeia para /tomadores/atualizar
    public String atualizarTomador(@ModelAttribute TomadorModel tomadorAtualizado, RedirectAttributes redirectAttributes) {
        try {
            tomadorService.atualizarTomador(tomadorAtualizado);
            redirectAttributes.addFlashAttribute("success", "Tomador '" + tomadorAtualizado.getNome() + "' atualizado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar tomador: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocorreu um erro inesperado ao atualizar o tomador.");
        }
        return "redirect:/tomadores";
    }
}