package com.nfs.PremierNotes.nfs.controller;

import com.nfs.PremierNotes.nfs.models.NotaFiscalModel;
import com.nfs.PremierNotes.nfs.repository.NotaFiscalRepository;
import com.nfs.PremierNotes.nfs.repository.TomadorRepository;
import com.nfs.PremierNotes.nfs.service.NotaFiscalService;
import com.nfs.PremierNotes.nfs.helper.ExcelHelper;
import org.apache.poi.EncryptedDocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Sort;
import com.nfs.PremierNotes.nfs.models.TomadorModel;

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

    @GetMapping("/cadastrar") // <--- NOVO MÉTODO OU ORDEM ALTERADA
    public String showCadastroForm(Model model) {
        model.addAttribute("notaFiscal", new NotaFiscalModel());
        model.addAttribute("tomadores", tomadorRepository.findAll());
        return "cadastrar";
    }

    @PostMapping("/cadastrar") // ADICIONE ESTE MÉTODO DE POST PARA CADASTRO MANUAL
    public String cadastrarNota(@ModelAttribute NotaFiscalModel notaFiscal, RedirectAttributes redirectAttributes) {
        service.salvarNota(notaFiscal);
        redirectAttributes.addFlashAttribute("success", "Nota fiscal cadastrada com sucesso!!");
        return "redirect:/notas";
    }

    @PostMapping("/importar")
    public String importarExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String filename = file.getOriginalFilename();
        if (filename == null || !(filename.endsWith(".xls") || filename.endsWith(".xlsx"))) {
            redirectAttributes.addFlashAttribute("error", "Por favor, envie um arquivo Excel válido (.xls ou .xlsx).");
            return "redirect:/notas";
        }
        try {
            List<NotaFiscalModel> notas = ExcelHelper.lerNotasDoExcel(file.getInputStream());
            for (NotaFiscalModel nota : notas) {
                service.salvarNota(nota);
            }
            redirectAttributes.addFlashAttribute("success", "Importação feita com sucesso!");
        } catch (EncryptedDocumentException e) {
            redirectAttributes.addFlashAttribute("error", "O arquivo está protegido por senha e não pode ser lido.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erro ao processar o arquivo. Verifique se ele é um Excel válido.");
        }
        return "redirect:/notas";
    }

    @GetMapping
    public String listarNotas(@RequestParam(required = false) String filtroTomador,
                              @RequestParam(required = false) String filtroStatus,
                              @RequestParam(required = false) Integer ano,
                              @RequestParam(defaultValue = "desc") String sort,
                              Model model) {

        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortByDate = Sort.by(direction, "dataEmissao");

        List<NotaFiscalModel> notas;

        if (ano != null) {
            notas = repository.findByAno(ano, sortByDate);
        } else if (filtroTomador != null && !filtroTomador.isEmpty()) {
            filtroTomador = filtroTomador.trim().toUpperCase();

            Optional<TomadorModel> tomadorOpt = tomadorRepository.findByNomeIgnoreCase(filtroTomador);
            if (tomadorOpt.isPresent()) {
                notas = repository.findByTomador(tomadorOpt.get(), sortByDate);
            } else {
                notas = new ArrayList<>();
            }

        } else if (filtroStatus != null && !filtroStatus.isEmpty()) {
            String statusPagamento = filtroStatus.equals("true") ? "PAGO" : "PENDENTE";
            notas = repository.findByStatusPagamento(statusPagamento, sortByDate);
        } else {
            notas = repository.findAll(sortByDate);
        }

        model.addAttribute("notas", notas);
        model.addAttribute("tomadores", repository.findDistinctTomadores());
        model.addAttribute("anos", repository.findDistinctAnos());
        model.addAttribute("filtroTomador", filtroTomador);
        model.addAttribute("filtroStatus", filtroStatus);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentAno", ano);

        return "NFS";
    }

    @PostMapping("/atualizar-status/{id}")
    @ResponseBody
    public String atualizarStatus(@PathVariable Long id, @RequestBody StatusRequest statusRequest) {
        try {
            service.atualizarStatus(id, statusRequest.isStatus());
            return "OK";
        } catch (Exception e) {
            return "ERRO";
        }
    }

    @GetMapping("/{id}")
    public String verDetalhesDaNota(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<NotaFiscalModel> notaOptional = repository.findById(id);

        if (notaOptional.isPresent()) {
            NotaFiscalModel nota = notaOptional.get();
            service.calcularDetalhesDePrazo(nota);
            model.addAttribute("nota", nota);
            return "detalhes-nota";
        } else {
            redirectAttributes.addFlashAttribute("error", "Nota fiscal com ID " + id + " não encontrada.");
            return "redirect:/notas";
        }
    }

    @PostMapping("/excluir-multiplos")
    public String excluirNotas(@RequestParam("ids") List<Long> idsSelecionados, RedirectAttributes redirectAttributes) {
        try {
            repository.deleteAllById(idsSelecionados);
            redirectAttributes.addFlashAttribute("success", "Notas excluídas com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir as notas.");
        }
        return "redirect:/notas";
    }

    static class StatusRequest {
        private boolean status;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }
}
