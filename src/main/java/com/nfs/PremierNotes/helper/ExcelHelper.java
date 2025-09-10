package com.nfs.PremierNotes.helper;

import com.nfs.PremierNotes.models.NotaFiscalModel;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelHelper {

    public static List<NotaFiscalModel> lerNotasDoExcel(InputStream is) {
        try {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            List<NotaFiscalModel> todasAsNotas = new ArrayList<>();

            System.out.println("--- INICIANDO IMPORTAÇÃO ---");

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow == null) continue;

                if (isHeaderRow(currentRow)) {
                    System.out.println("\n>>> Bloco de Notas Encontrado na Linha " + (i + 1) + " <<<");

                    Map<String, Integer> columnMap = mapColumns(currentRow);
                    System.out.println("Mapa de Colunas: " + columnMap);

                    for (int j = i + 1; j <= sheet.getLastRowNum(); j++) {
                        Row dataRow = sheet.getRow(j);
                        if (dataRow == null) break;

                        String primeiraColuna = getCellValueAsString(dataRow.getCell(0));

                        if (!primeiraColuna.matches("\\d+.*")) { // Verifica se a célula NÃO começa com um número
                            System.out.println("Fim do bloco de dados (linha de total/resumo) na linha " + (j + 1));
                            i = j; // Pula o contador principal para depois da linha de total
                            break; // Sai do loop interno e volta a procurar o próximo cabeçalho
                        }

                        try {
                            NotaFiscalModel nota = new NotaFiscalModel();

                            nota.setDataEmissao(getCellValueAsDate(dataRow.getCell(columnMap.get("Dt. Emissão"))));
                            nota.setCnpjTomador(getCellValueAsString(dataRow.getCell(columnMap.get("CNPJ Tomador"))));
                            // era pra ter o metodo tomador aqui
                            nota.setValorNF(getCellValueAsDouble(dataRow.getCell(columnMap.get("Vl. NF."))));
                            nota.setValorDeducoes(getCellValueAsDouble(dataRow.getCell(columnMap.get("Vl. Ded."))));
                            nota.setValorBase(getCellValueAsDouble(dataRow.getCell(columnMap.get("Vl. Base"))));
                            nota.setAliquota(getCellValueAsDouble(dataRow.getCell(columnMap.get("Alíq"))));
                            nota.setValorIssqn(getCellValueAsDouble(dataRow.getCell(columnMap.get("Vl.ISSQN"))));
                            nota.setRetido(getCellValueAsString(dataRow.getCell(columnMap.get("Retido"))));
                            nota.setStatus(getCellValueAsString(dataRow.getCell(columnMap.get("Status"))));
                            nota.setLocalRecolhimento(getCellValueAsString(dataRow.getCell(columnMap.get("Local do Recolhimento"))));

                            todasAsNotas.add(nota);
                        } catch (Exception e) {
                            System.err.println("AVISO: Ignorando linha " + (j + 1) + ". Causa: " + e.getMessage());
                        }
                    }
                }
            }

            workbook.close();
            System.out.println("\n--- IMPORTAÇÃO FINALIZADA. TOTAL DE NOTAS LIDAS: " + todasAsNotas.size() + " ---");
            return todasAsNotas;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Falha crítica ao processar o arquivo Excel: " + e.getMessage());
        }
    }

    private static boolean isHeaderRow(Row row) {
        if (row == null) return false;
        boolean hasDtEmissao = false;
        boolean hasCnpj = false;
        for (Cell cell : row) {
            String value = getCellValueAsString(cell).toLowerCase();
            if (value.contains("dt. emissão")) hasDtEmissao = true; // Com espaço
            if (value.contains("cnpj tomador")) hasCnpj = true;
        }
        return hasDtEmissao && hasCnpj;
    }

    private static Map<String, Integer> mapColumns(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String headerText = getCellValueAsString(cell).replace("\"", "").trim();
            if (!headerText.isEmpty()) {
                columnMap.put(headerText, cell.getColumnIndex());
            }
        }
        return columnMap;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    private static Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 0.0;
        String value = getCellValueAsString(cell);
        try {
            return Double.parseDouble(value.replace(".", "").replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static LocalDate getCellValueAsDate(Cell cell) {
        // 1. Lemos a célula como texto, que é como ela realmente está no arquivo.
        String dateStr = getCellValueAsString(cell);

        // 2. Se o texto estiver vazio, não há o que converter.
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            // 3. Tentamos converter o texto para o formato de data que conhecemos.
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            // 4. Se a conversão falhar, registramos o erro e retornamos nulo.
            System.err.println("--> ERRO DE DATA: O texto '" + dateStr + "' não está no formato dd/MM/yyyy.");
            return null;
        }
    }
}