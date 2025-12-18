package com.nfs.PremierNotes.nfs.helper;

import com.nfs.PremierNotes.nfs.models.NotaFiscalModel;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelHelper {

    // MAPA RIGOROSO: Baseado no print da Prefeitura de Blumenau
    private static final Map<String, List<String>> HEADER_ALIASES = new HashMap<>();

    static {
        // Coluna "Dt."
        HEADER_ALIASES.put("DATA", Arrays.asList("dt.", "data", "dt. emissão", "emissao"));

        // Coluna "CNPJ Tomador"
        HEADER_ALIASES.put("CNPJ", Arrays.asList("cnpj tomador", "cnpj", "cpf/cnpj"));

        // Coluna "Tomador"
        HEADER_ALIASES.put("NOME", Arrays.asList("tomador", "nome", "razão social", "cliente"));

        // Coluna "Vl. NF." (Removi "valor" genérico para não confundir com outras colunas)
        HEADER_ALIASES.put("VALOR_NF", Arrays.asList("vl. nf.", "vl. nota", "valor nota", "valor total"));

        // Coluna "Vl.ISSQN"
        HEADER_ALIASES.put("ISS", Arrays.asList("vl.issqn", "valor iss", "issqn"));

        // Coluna "Status"
        HEADER_ALIASES.put("STATUS", Arrays.asList("status", "situação"));

        // Coluna "Local do Recolhimento"
        HEADER_ALIASES.put("LOCAL", Arrays.asList("local do recolhimento", "local", "município"));
    }

    public static List<NotaFiscalModel> lerNotasDoExcel(InputStream is) {
        try {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            List<NotaFiscalModel> todasAsNotas = new ArrayList<>();

            System.out.println("--- INICIANDO IMPORTAÇÃO OTIMIZADA ---");

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 1. Identifica as colunas da linha atual
                Map<String, Integer> mapColunas = identificarColunas(row);

                // 2. Validação: Só processa se achar DATA e CNPJ (garantia que é cabeçalho de nota)
                if (mapColunas.containsKey("DATA") && mapColunas.containsKey("CNPJ")) {
                    System.out.println(">>> Cabeçalho encontrado na linha " + (i + 1));

                    // 3. Loop interno: Lê os dados abaixo do cabeçalho
                    for (int j = i + 1; j <= sheet.getLastRowNum(); j++) {
                        Row dataRow = sheet.getRow(j);
                        if (dataRow == null) break;

                        // Verifica a coluna NOME ou CNPJ para saber se o bloco acabou
                        String nome = getCellValueAsString(dataRow, mapColunas.get("NOME"));

                        // Critério de parada: Linha vazia ou linha de Totais do rodapé
                        if (nome.isEmpty() || nome.toLowerCase().contains("total")) {
                            System.out.println("Fim do bloco na linha " + (j + 1));
                            i = j; // Avança o contador externo
                            break;
                        }

                        try {
                            NotaFiscalModel nota = new NotaFiscalModel();

                            // Leitura Mapeada (Cada dado na sua coluna correta)
                            nota.setDataEmissao(getCellValueAsDate(dataRow, mapColunas.get("DATA")));
                            nota.setCnpjTomador(getCellValueAsString(dataRow, mapColunas.get("CNPJ")));
                            nota.setNomeTomadorString(nome.toUpperCase().trim()); // Salva temporário para o Service processar

                            // Valores Numéricos
                            nota.setValorNF(getCellValueAsDouble(dataRow, mapColunas.get("VALOR_NF")));
                            nota.setValorIssqn(getCellValueAsDouble(dataRow, mapColunas.get("ISS")));

                            // Detalhes
                            nota.setStatus(getCellValueAsString(dataRow, mapColunas.get("STATUS")).toUpperCase());
                            nota.setLocalRecolhimento(getCellValueAsString(dataRow, mapColunas.get("LOCAL")).toUpperCase());

                            todasAsNotas.add(nota);
                        } catch (Exception e) {
                            System.err.println("Erro na linha " + (j + 1) + ": " + e.getMessage());
                        }
                    }
                }
            }

            workbook.close();
            System.out.println("Total de notas lidas: " + todasAsNotas.size());
            return todasAsNotas;

        } catch (Exception e) {
            throw new RuntimeException("Erro crítico ao ler Excel: " + e.getMessage());
        }
    }

    // --- LÓGICA DE IDENTIFICAÇÃO DE COLUNAS ---
    private static Map<String, Integer> identificarColunas(Row row) {
        Map<String, Integer> map = new HashMap<>();

        for (Cell cell : row) {
            String valorCelula = getCellValueAsString(cell).toLowerCase().trim();
            if (valorCelula.isEmpty()) continue;

            for (Map.Entry<String, List<String>> entry : HEADER_ALIASES.entrySet()) {
                for (String alias : entry.getValue()) {
                    // MUDANÇA IMPORTANTE:
                    // Usamos equals() para colunas curtas (evita que 'Valor' pegue 'Valor ISS')
                    // Usamos contains() apenas para textos longos se necessário
                    if (valorCelula.equals(alias) || (alias.length() > 5 && valorCelula.contains(alias))) {
                        map.put(entry.getKey(), cell.getColumnIndex());
                        // Se achou, para de procurar apelidos para essa chave e vai pra próxima célula
                        break;
                    }
                }
            }
        }
        return map;
    }

    // --- MÉTODOS DE LEITURA SEGURA (Mantidos pois funcionam bem) ---

    private static String getCellValueAsString(Row row, Integer index) {
        if (index == null) return "";
        return getCellValueAsString(row.getCell(index));
    }

    private static Double getCellValueAsDouble(Row row, Integer index) {
        if (index == null) return 0.0;
        return getCellValueAsDouble(row.getCell(index));
    }

    private static LocalDate getCellValueAsDate(Row row, Integer index) {
        if (index == null) return null;
        return getCellValueAsDate(row.getCell(index));
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return new DataFormatter().formatCellValue(cell).trim();
    }

    private static Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 0.0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else {
            String val = getCellValueAsString(cell)
                    .replace("R$", "").replace(" ", "").trim();
            if (val.isEmpty()) return 0.0;
            try {
                return Double.parseDouble(val.replace(".", "").replace(",", "."));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }

    private static LocalDate getCellValueAsDate(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        String dateStr = getCellValueAsString(cell);
        if (dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return null;
        }
    }
}