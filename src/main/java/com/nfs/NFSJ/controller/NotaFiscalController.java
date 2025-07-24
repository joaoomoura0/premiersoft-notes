package com.nfs.NFSJ.controller;

import com.nfs.NFSJ.models.NotaFiscalModel;
import com.nfs.NFSJ.service.NotaFiscalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.nfs.NFSJ.helper.ExcelHelper;

import java.util.List;

@Controller
@RequestMapping("/notas")
public class NotaFiscalController {

    @Autowired
    private NotaFiscalService service;

    // Cadastro via formulário HTML
    @PostMapping("/cadastrar")
    public String cadastrarNota(@ModelAttribute NotaFiscalModel notaFiscal, RedirectAttributes redirectAttributes) {
        service.salvarNota(notaFiscal);
        redirectAttributes.addFlashAttribute("success", "Nota fiscal cadastrada com sucesso!");
        return "redirect:/notas";
    }

    // Importação do Excel continua aqui
    @PostMapping("/importar")
    public String importarExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            List<NotaFiscalModel> notas = ExcelHelper.lerNotasDoExcel(file.getInputStream());
            for (NotaFiscalModel nota : notas) {
                service.salvarNota(nota);
            }
            redirectAttributes.addFlashAttribute("success", "Importação feita com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erro ao importar arquivo.");
        }
        return "redirect:/notas";
    }
}
