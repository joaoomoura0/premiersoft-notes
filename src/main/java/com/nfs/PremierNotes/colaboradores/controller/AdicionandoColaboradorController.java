package com.nfs.PremierNotes.colaboradores.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import com.nfs.PremierNotes.colaboradores.repository.ColaboradorSeguroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/colaboradores")
public class AdicionandoColaboradorController {

    @Autowired
    private ColaboradorSeguroRepository repository;

    @PostMapping("/importar")
    @Transactional
    public String importarColaboradores() throws Exception {

        InputStream inputStream = getClass().getResourceAsStream("/colaboradores.json");

        if (inputStream == null) {
            return "Arquivo NÃO encontrado";
        }

        ObjectMapper mapper = new ObjectMapper();

        // 1. REGISTRE O MÓDULO DE DATAS (Corrige o erro atual)
        mapper.registerModule(new JavaTimeModule());

        // 2. MANTENHA A CONFIGURAÇÃO DE IGNORAR CAMPOS EXTRAS (Corrige o erro anterior do 'cargo')
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<ColaboradorSeguroModel> lista =
                mapper.readValue(inputStream, new TypeReference<List<ColaboradorSeguroModel>>() {});

        repository.saveAll(lista);

        return "Importação concluída. Total: " + lista.size();
    }
}

