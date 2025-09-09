package com.nfs.PremierNotes.controller; // Verifique seu pacote

import com.nfs.PremierNotes.models.TomadorModel;
import com.nfs.PremierNotes.repository.TomadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tomadores") // Todas as rotas aqui começarão com /tomadores
public class TomadorController {

    @Autowired
    private TomadorRepository tomadorRepository;

    @GetMapping
    public String listarTomadores(@RequestParam(required = false) String busca, Model model) {
        List<TomadorModel> tomadores;

        if (busca != null && !busca.trim().isEmpty()) {
            // Se houver uma busca, filtra a lista
            tomadores = tomadorRepository.findByNomeContainingIgnoreCase(busca.trim());
        } else {
            // Senão, lista todos
            tomadores = tomadorRepository.findAll();
        }

        model.addAttribute("tomadores", tomadores);
        model.addAttribute("buscaAtual", busca); // Envia a busca atual de volta para o input
        return "tomadores";
    }

    // Rota para ATUALIZAR um tomador
    @PostMapping("/atualizar")
    public String atualizarTomador(@RequestParam Long id,
                                   @RequestParam Integer prazo,
                                   @RequestParam boolean ativo,
                                   RedirectAttributes redirectAttributes) {

        TomadorModel tomador = tomadorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID do Tomador inválido: " + id));

        tomador.setPrazoPagamentoDias(prazo);
        tomador.setAtivo(ativo);
        tomadorRepository.save(tomador);

        redirectAttributes.addFlashAttribute("success", "Tomador '" + tomador.getNome() + "' atualizado com sucesso!");
        return "redirect:/tomadores";
    }
}