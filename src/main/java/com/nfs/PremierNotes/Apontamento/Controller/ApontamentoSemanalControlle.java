package com.nfs.PremierNotes.Apontamento.Controller;

import com.nfs.PremierNotes.Apontamento.Service.ApontamentosSemanalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/apontamentos")
@RequiredArgsConstructor
public class ApontamentoSemanalControlle {

    private final ApontamentosSemanalService service;

    @PostMapping("/importar")
    public ResponseEntity<String> importar(@RequestParam("file") MultipartFile file){
        try {
            service.importarSemana(file);
            return ResponseEntity.ok("Importação processada com sucesso");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }
}
