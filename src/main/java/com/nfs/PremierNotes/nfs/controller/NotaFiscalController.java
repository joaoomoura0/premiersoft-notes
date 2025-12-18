package com.nfs.PremierNotes.nfs.controller;

import com.nfs.PremierNotes.nfs.helper.ExcelHelper;
import com.nfs.PremierNotes.nfs.models.NotaFiscalModel;
import com.nfs.PremierNotes.nfs.models.TomadorModel;
import com.nfs.PremierNotes.nfs.repository.NotaFiscalRepository;
import com.nfs.PremierNotes.nfs.repository.TomadorRepository;
import com.nfs.PremierNotes.nfs.service.NotaFiscalService;
import org.apache.poi.EncryptedDocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/notas")
public class NotaFiscalController {

    @Autowired
    private NotaFiscalRepository repository;

    @Autowired
    private NotaFiscalService service;

    @Autowired
    private TomadorRepository tomadorRepository;

    // --- CADASTRO MANUAL ---
    @GetMapping("/cadastrar")
    public String showCadastroForm(Model model) {
        model.addAttribute("notaFiscal", new NotaFiscalModel());
        model.addAttribute("tomadores", tomadorRepository.findByAtivoTrueOrderByNomeAsc()); // Só mostra ativos
        return "nfs/cadastrar";
    }

    @PostMapping("/cadastrar")
    public String cadastrarNota(@ModelAttribute NotaFiscalModel notaFiscal, RedirectAttributes redirectAttributes) {
        try {
            // MUDANÇA: Usa o método específico para manual que criamos na Service
            service.salvarNotaManual(notaFiscal);
            redirectAttributes.addFlashAttribute("success", "Nota fiscal cadastrada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao cadastrar: " + e.getMessage());
        }
        return "redirect:/notas";
    }

    @PostMapping("/importar")
    public String importarExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String filename = file.getOriginalFilename();
        if (filename == null || !(filename.endsWith(".xls") || filename.endsWith(".xlsx"))) {
            redirectAttributes.addFlashAttribute("error", "Arquivo inválido. Envie um Excel (.xls ou .xlsx).");
            return "redirect:/notas";
        }

        try {
            List<NotaFiscalModel> notasLidas = ExcelHelper.lerNotasDoExcel(file.getInputStream());

            if (notasLidas.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Nenhuma nota válida encontrada na planilha.");
                return "redirect:/notas";
            }


            service.salvarNotasImportadas(notasLidas);

            redirectAttributes.addFlashAttribute("success", "Processamento concluído! Verifique o console para detalhes de duplicatas.");

        } catch (EncryptedDocumentException e) {
            redirectAttributes.addFlashAttribute("error", "O arquivo Excel tem senha.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erro crítico na importação: " + e.getMessage());
        }
        return "redirect:/notas";
    }

    @GetMapping
    public String listarNotas(@RequestParam(required = false) String filtroTomador,
                              @RequestParam(required = false) String filtroStatus,
                              @RequestParam(required = false) Integer ano,
                              @RequestParam(defaultValue = "desc") String sort,
                              Model model) {

        // 1. Define ordenação
        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortByDate = Sort.by(direction, "dataEmissao");

        // 2. Prepara os filtros (trata string vazia como null)
        String nomeTomador = (filtroTomador != null && !filtroTomador.trim().isEmpty()) ? filtroTomador.trim() : null;
        String status = (filtroStatus != null && !filtroStatus.isEmpty())
                ? (filtroStatus.equals("true") ? "PAGO" : "PENDENTE")
                : null;

        // 3. Chama a Query Poderosa
        List<NotaFiscalModel> notas = repository.buscarComFiltros(nomeTomador, status, ano, sortByDate);

        // 4. Calcula prazos para visualização
        notas.forEach(n -> service.calcularDetalhesDePrazo(n));

        model.addAttribute("notas", notas);
        model.addAttribute("tomadores", repository.findDistinctTomadores());
        model.addAttribute("anos", repository.findDistinctAnos());

        // Mantém os filtros selecionados na tela
        model.addAttribute("filtroTomador", filtroTomador);
        model.addAttribute("filtroStatus", filtroStatus);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentAno", ano);

        return "nfs/NFS";
    }

    // --- ATUALIZAÇÃO DE STATUS (AJAX) ---
    @PostMapping("/atualizar-status/{id}")
    @ResponseBody
    public String atualizarStatus(@PathVariable Long id, @RequestBody StatusRequest statusRequest) {
        try {
            // MUDANÇA: Nome do método atualizado na Service
            service.atualizarStatusPagamento(id, statusRequest.isStatus());
            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERRO";
        }
    }

    // --- DETALHES ---
    @GetMapping("/{id}")
    public String verDetalhesDaNota(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<NotaFiscalModel> notaOptional = repository.findById(id);

        if (notaOptional.isPresent()) {
            NotaFiscalModel nota = notaOptional.get();
            service.calcularDetalhesDePrazo(nota); // Calcula para exibir no detalhe
            model.addAttribute("nota", nota);
            return "nfs/detalhes-nota";
        } else {
            redirectAttributes.addFlashAttribute("error", "Nota não encontrada.");
            return "redirect:/notas";
        }
    }

    // --- EXCLUSÃO ---
    @PostMapping("/excluir-multiplos")
    public String excluirNotas(@RequestParam("ids") List<Long> idsSelecionados, RedirectAttributes redirectAttributes) {
        try {
            if (idsSelecionados != null && !idsSelecionados.isEmpty()) {
                repository.deleteAllById(idsSelecionados);
                redirectAttributes.addFlashAttribute("success", "Notas excluídas com sucesso!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir notas.");
        }
        return "redirect:/notas";
    }

    // DTO auxiliar para o JSON do AJAX
    static class StatusRequest {
        private boolean status;
        public boolean isStatus() { return status; }
        public void setStatus(boolean status) { this.status = status; }
    }
}