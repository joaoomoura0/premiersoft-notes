package com.nfs.NFSJ.controller;

import com.nfs.NFSJ.models.NotaFiscalModel;
import com.nfs.NFSJ.service.NotaFiscalService;
import com.nfs.NFSJ.helper.ExcelHelper;
import org.apache.poi.EncryptedDocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/importar")
    public String importarExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String filename = file.getOriginalFilename();

        // Verifica se o nome é nulo ou a extensão não é .xls ou .xlsx
        if (filename == null || !(filename.endsWith(".xls") || filename.endsWith(".xlsx"))) {
            redirectAttributes.addFlashAttribute("error", "Por favor, envie um arquivo Excel válido (.xls ou .xlsx).");
            return "redirect:/notas";
        }

        try {
            // Tenta ler as notas com o helper
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
}