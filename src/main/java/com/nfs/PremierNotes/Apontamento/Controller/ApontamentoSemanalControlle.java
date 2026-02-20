package com.nfs.PremierNotes.Apontamento.Controller;

import com.nfs.PremierNotes.Apontamento.Model.ApontamentoSemanal;
import com.nfs.PremierNotes.Apontamento.Service.ApontamentosSemanalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/semana")
    public List<ApontamentoSemanal> buscarSemana(
            @RequestParam LocalDate data) {

        return service.buscarSemana(data);
    }
}
