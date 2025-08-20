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
@RequestMapping("/notas")
public class NotaFiscalController {


    @Autowired
    private NotaFiscalRepository repository;

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
                // A padronização do 'tomador' ocorre no service.salvarNota()
                service.salvarNota(nota);
            }

            redirectAttributes.addFlashAttribute("success", "Importação feita com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erro ao processar o arquivo. Verifique se ele é um Excel válido.");
        }
        return "redirect:/notas";
    }

    @GetMapping
    public String listarNotas(@RequestParam(required = false) String filtroTomador,
                              @RequestParam(required = false) String filtroStatus,
                              Model model) {
        List<NotaFiscalModel> notas;

        if (filtroTomador != null && !filtroTomador.isEmpty()) {
            filtroTomador = filtroTomador.trim().toUpperCase();
            notas = repository.findByTomador(filtroTomador);
        } else if (filtroStatus != null && !filtroStatus.isEmpty()) {

            String statusPagamento = filtroStatus.equals("true") ? "PAGO" : "PENDENTE";
            notas = repository.findByStatusPagamento(statusPagamento);
        } else {
            notas = repository.findAll();
        }

        model.addAttribute("notas", notas);
        model.addAttribute("tomadores", repository.findDistinctTomadores());
        model.addAttribute("filtroTomador", filtroTomador);
        model.addAttribute("filtroStatus", filtroStatus);

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
    class StatusRequest {
        private boolean status;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }
}