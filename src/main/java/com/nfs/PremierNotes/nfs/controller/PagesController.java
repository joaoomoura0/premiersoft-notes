package com.nfs.PremierNotes.nfs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/notas/importar")
    public String importar() {
        return "importar";
    }
}