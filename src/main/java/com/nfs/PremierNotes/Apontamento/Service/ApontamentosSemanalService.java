package com.nfs.PremierNotes.Apontamento.Service;

import com.nfs.PremierNotes.Apontamento.Model.ApontamentoSemanal;
import com.nfs.PremierNotes.Apontamento.Model.Cliente;
import com.nfs.PremierNotes.Apontamento.Repository.ApontamentoSemanalRepository;
import com.nfs.PremierNotes.Apontamento.Repository.ClienteRepository;
import com.nfs.PremierNotes.colaboradores.models.ColaboradorSeguroModel;
import com.nfs.PremierNotes.colaboradores.repository.ColaboradorSeguroRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApontamentosSemanalService {

    private final ApontamentoSemanalRepository apontamentosSemanalRepository;
    private final ColaboradorSeguroRepository colaboradorSeguroRepository;
    private final ClienteRepository clienteRepository;
    private final ApontamentoSemanalRepository apontamentoSemanalRepository;

    @Transactional
    public void importarSemana(MultipartFile arquivo){

        try (Workbook workbook = new XSSFWorkbook(arquivo.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

        List<LocalDate> datasEncontradas = new ArrayList<>();
            Map<String, ApontamentoSemanal> mapaAgrupado = new HashMap<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue;

                String username = row.getCell(4).getStringCellValue().trim();

                String nomeCliente = row.getCell(1)
                        .getStringCellValue()
                        .trim()
                        .toUpperCase();

                String descricao = row.getCell(2) != null
                        ? row.getCell(2).getStringCellValue()
                        : null;

                LocalDate data = row.getCell(9)
                        .getLocalDateTimeCellValue()
                        .toLocalDate();

                BigDecimal horas = BigDecimal.valueOf(
                        row.getCell(14).getNumericCellValue()
                );

                boolean possuiDescricao = descricao != null && !descricao.isBlank();
                datasEncontradas.add(data);

                ColaboradorSeguroModel colaborador = colaboradorSeguroRepository
                        .findByUsernameImportacaoIgnoreCase(username)
                        .orElse(null);

                if (colaborador == null) {
                    System.out.println("Colaborador não encontrado: " + username);
                    continue;
                }

                Cliente cliente = clienteRepository
                        .findByNomeCliente(nomeCliente)
                        .orElseGet(() -> {
                            Cliente novoCliente = new Cliente();
                            novoCliente.setNomeCliente(nomeCliente);
                            return clienteRepository.save(novoCliente);
                        });

                String chave = colaborador.getId() + "-" + data;

                if (mapaAgrupado.containsKey(chave)) {
                    ApontamentoSemanal existente = mapaAgrupado.get(chave);
                    existente.setHoras(existente.getHoras().add(horas));
                } else {
                    ApontamentoSemanal novo = new ApontamentoSemanal();
                    novo.setData(data);
                    novo.setHoras(horas);
                    novo.setPossuiDescricao(possuiDescricao);
                    novo.setColaborador(colaborador);
                    novo.setCliente(cliente); // vai pegar o primeiro cliente do dia

                    mapaAgrupado.put(chave, novo);
                }
            }

            // Descobrir menor e maior data
            LocalDate menorData = datasEncontradas.stream()
                    .min(LocalDate::compareTo)
                    .orElseThrow(() -> new RuntimeException("Nenhuma data encontrada no Excel"));

            LocalDate maiorData = datasEncontradas.stream()
                    .max(LocalDate::compareTo)
                    .orElseThrow(() -> new RuntimeException("Nenhuma data encontrada no Excel"));

            // Ajusta para segunda e domingo
            LocalDate inicioSemana = menorData.with(DayOfWeek.MONDAY);
            LocalDate fimSemana = maiorData.with(DayOfWeek.SUNDAY);

            System.out.println("Semana detectada: " + inicioSemana + " até " + fimSemana);

            apontamentoSemanalRepository.deleteByDataBetween(inicioSemana, fimSemana);
            apontamentoSemanalRepository.saveAll(mapaAgrupado.values());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar arquivo Excel", e);
        }
    }

    public  List<ApontamentoSemanal> buscarSemana(LocalDate dataReferencia){

        LocalDate inicioSemana = dataReferencia.with(DayOfWeek.MONDAY);
        LocalDate fimSemana = dataReferencia.with(DayOfWeek.SUNDAY);

        return apontamentoSemanalRepository.findByDataBetween(inicioSemana, fimSemana);
    }
}
