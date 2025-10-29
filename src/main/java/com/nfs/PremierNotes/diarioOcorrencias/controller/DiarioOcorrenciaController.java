package com.nfs.PremierNotes.diarioOcorrencias.controller;

import com.nfs.PremierNotes.colaboradores.service.ColaboradorSeguroService;
import com.nfs.PremierNotes.diarioOcorrencias.model.DiarioOcorrenciaModel;
import com.nfs.PremierNotes.diarioOcorrencias.model.StatusOcorrencia;
import com.nfs.PremierNotes.diarioOcorrencias.model.TipoOcorrencia;
import com.nfs.PremierNotes.diarioOcorrencias.service.DiarioOcorrenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/diario")
public class DiarioOcorrenciaController {

    @Autowired
    private DiarioOcorrenciaService diarioService;

    @Autowired
    private ColaboradorSeguroService colaboradorService;

    @GetMapping
    public String exibirDiario(Model model) {

        model.addAttribute("colaboradores", colaboradorService.listarTodosColaboradores(Sort.by("nomeCompleto").ascending()));
        model.addAttribute("tipos", TipoOcorrencia.values());
        model.addAttribute("statusValues", StatusOcorrencia.values());
        model.addAttribute("ocorrencia", new DiarioOcorrenciaModel());

        model.addAttribute("accounts", List.of("Bia", "Jess", "Tau"));
        model.addAttribute("origens", List.of(
                "TimeTracker",
                "AB-InBev",
                "Cheesecake Labs",
                "COAMO",
                "Contabilizei",
                "Credisis",
                "Grupo Potencial",
                "Iconic Hearts",
                "Philips",
                "Serasa",
                "Sicoob SC/RS",
                "Studio Z",
                "Tonic.ai"
        ));

        return "diario";
    }

    @GetMapping("/api/mes")
    @ResponseBody
    public List<DiarioOcorrenciaModel> getOcorrenciasDoMes(@RequestParam int mes, @RequestParam int ano) {
        return diarioService.buscarOcorrenciasDoMes(mes, ano);
    }

    @GetMapping("/api/dia")
    @ResponseBody
    public List<DiarioOcorrenciaModel> getOcorrenciasDoDia(@RequestParam String data) {
        return diarioService.buscarOcorrenciasDoDia(data);
    }

    @GetMapping("/api/detalhe/{id}")
    @ResponseBody
    public DiarioOcorrenciaModel getDetalheOcorrencia(@PathVariable Long id) {
        return diarioService.buscarPorId(id);
    }

    @PostMapping("/salvar")
    public String salvarOcorrencia(@ModelAttribute("ocorrencia") @Valid DiarioOcorrenciaModel ocorrencia, BindingResult result, RedirectAttributes attributes, Model model) {

        if (ocorrencia.getOrigem() == null || ocorrencia.getOrigem().isEmpty()) {
            ocorrencia.setOrigem("DIARIO_WEB");
        }

        if (result.hasErrors()) {
            attributes.addFlashAttribute("mensagemErro", "Falha de validação! Verifique os campos.");
            return "redirect:/diario";
        }

        try {
            diarioService.salvarOcorrencia(ocorrencia);
            attributes.addFlashAttribute("mensagemSucesso", "Ocorrência registrada com sucesso!");
        } catch (IllegalArgumentException e) {
            attributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/diario";
    }

    @PostMapping("/remover/{id}")
    public String removerOcorrencia(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            diarioService.removerOcorrencia(id);
            attributes.addFlashAttribute("mensagemSucesso", "Ocorrência removida.");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao remover ocorrência.");
        }
        return "redirect:/diario";
    }

    // ... Endpoints para Edição e Inativação/Resolução
}
