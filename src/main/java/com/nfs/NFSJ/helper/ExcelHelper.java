package com.nfs.NFSJ.helper;

import com.nfs.NFSJ.models.NotaFiscalModel;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {

    public static List<NotaFiscalModel> lerNotasDoExcel(InputStream is) {
        List<NotaFiscalModel> notas = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            boolean primeiraLinha = true;
            for (Row row : sheet) {
                if (primeiraLinha) {
                    primeiraLinha = false; // Pula o cabeçalho
                    continue;
                }

                // Ignora linhas completamente vazias
                if (row == null || row.getCell(1) == null) {
                    continue;
                }

                NotaFiscalModel nota = new NotaFiscalModel();

                // Coluna 1: Data Emissao
                lerData(row.getCell(1), nota);

                // Coluna 2: CNPJ Tomador
                nota.setCnpjTomador(lerString(row.getCell(2)));

                // Coluna 3: Tomador
                nota.setTomador(lerString(row.getCell(3)));

                // Coluna 4: Valor NF
                nota.setValorNF(lerDouble(row.getCell(4)));

                // Coluna 5: Valor Deducoes
                nota.setValorDeducoes(lerDouble(row.getCell(5)));

                // Coluna 6: Valor Base
                nota.setValorBase(lerDouble(row.getCell(6)));

                // Coluna 7: Aliquota
                nota.setAliquota(lerDouble(row.getCell(7)));

                // Coluna 8: Valor ISSQN
                nota.setValorIssqn(lerDouble(row.getCell(8))); // <--- CORREÇÃO APLICADA AQUI

                // Coluna 9: Retido
                nota.setRetido(lerString(row.getCell(9)));

                // Coluna 10: Status
                nota.setStatus(lerString(row.getCell(10)));

                // Coluna 11: Local Recolhimento
                nota.setLocalRecolhimento(lerString(row.getCell(11)));

                notas.add(nota);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao ler o conteúdo do Excel. Verifique o formato e o conteúdo do arquivo.");
        }
        return notas;
    }

    // Funções auxiliares para evitar repetição de código e tratar células vazias
    private static String lerString(Cell cell) {
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    private static Double lerDouble(Cell cell) {
        if (cell == null) return 0.0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().replace(",", "."));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private static void lerData(Cell cell, NotaFiscalModel nota) {
        if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            nota.setDataEmissao(cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }
}