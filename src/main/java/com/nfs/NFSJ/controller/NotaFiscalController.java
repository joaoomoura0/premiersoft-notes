package com.nfs.NFSJ.controller;

import com.nfs.NFSJ.models.NotaFiscalModel;
import com.nfs.NFSJ.repository.NotaFiscalRepository;
import com.nfs.NFSJ.service.NotaFiscalService;
import com.nfs.NFSJ.helper.ExcelHelper;
import org.apache.poi.EncryptedDocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class NotaFiscalController {

    @Autowired
    private NotaFiscalRepository repository;  // Injetar repository aqui

    @Autowired
    private NotaFiscalService service;

    @PostMapping("/cadastrar")
    public String cadastrarNota(@ModelAttribute NotaFiscalModel notaFiscal, RedirectAttributes redirectAttributes) {
        service.salvarNota(notaFiscal);
        redirectAttributes.addFlashAttribute("success", "Nota fiscal cadastrada com sucesso!");
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
            List<NotaFiscalModel> notas = ExcelHelper.lerNotasDoExcel(file.getInputStream(), filename);

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

    // Lista notas, com filtro opcional
    @GetMapping
    public String listarNotas(@RequestParam(required = false) String filtroTomador, Model model) {
        List<NotaFiscalModel> notas;

        if (filtroTomador != null && !filtroTomador.isEmpty()) {
            filtroTomador = filtroTomador.trim().toUpperCase();
            notas = repository.findByTomador(filtroTomador);
        } else {
            notas = repository.findAll();
        }

        List<String> tomadoresUnicos = repository.findDistinctTomadores();
        model.addAttribute("notas", notas);
        model.addAttribute("tomadores", repository.findDistinctTomadores());
        model.addAttribute("filtroTomador", filtroTomador);  // Para manter o filtro selecionado no select
        return "NFS";
    }

    // Excluir notas selecionadas
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
}