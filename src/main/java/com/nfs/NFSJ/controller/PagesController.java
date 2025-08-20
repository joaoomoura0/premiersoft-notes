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
        return "home";
    }

    @GetMapping("/notas/cadastrar")
    public String cadastrar() {
        return "cadastrar";
    }

    @GetMapping("/notas/importar")
    public String importar() {
        return "importar";
    }
}
