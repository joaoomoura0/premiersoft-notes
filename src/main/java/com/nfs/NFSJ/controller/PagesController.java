package com.nfs.NFSJ.controller;

import com.nfs.NFSJ.models.NotaFiscalModel;
import com.nfs.NFSJ.service.NotaFiscalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PagesController {

    @Autowired
    private NotaFiscalService service;

    @GetMapping("/home")
    public String home() {
        return "home"; // home.html
    }

    @GetMapping("/notas")
    public String notas(Model model) {
        List<NotaFiscalModel> notas = service.listarNotas();
        model.addAttribute("notas", notas);
        return "NFS"; // home.html.html
    }

    @GetMapping("/notas/cadastrar")
    public String cadastrar() {
        return "cadastrar"; // cadastrar.html
    }

    @GetMapping("/notas/importar")
    public String importar() {
        return "importar"; // importar.html
    }
}
