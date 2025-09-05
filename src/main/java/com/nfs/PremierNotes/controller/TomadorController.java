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

    // Rota para LISTAR todos os tomadores
    @GetMapping
    public String listarTomadores(Model model) {
        List<TomadorModel> tomadores = tomadorRepository.findAll();
        model.addAttribute("tomadores", tomadores);
        return "tomadores"; // Renderiza o arquivo tomadores.html
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