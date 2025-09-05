package com.nfs.PremierNotes.controller;

import com.nfs.PremierNotes.service.NotaFiscalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

    @Autowired
    private NotaFiscalService service;

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/notas/importar")
    public String importar() {
        return "importar";
    }
}
