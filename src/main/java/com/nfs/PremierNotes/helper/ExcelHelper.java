package com.nfs.PremierNotes.helper;

import com.nfs.PremierNotes.models.NotaFiscalModel;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
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
                    primeiraLinha = false;
                    continue;
                }

                if (row == null || row.getCell(1) == null) {
                    continue;
                }

                NotaFiscalModel nota = new NotaFiscalModel();
                lerData(row.getCell(1), nota);
                nota.setCnpjTomador(lerString(row.getCell(2)));
                nota.setTomador(lerString(row.getCell(3)));
                nota.setValorNF(lerDouble(row.getCell(4)));
                nota.setValorDeducoes(lerDouble(row.getCell(5)));
                nota.setValorBase(lerDouble(row.getCell(6)));
                nota.setAliquota(lerDouble(row.getCell(7)));
                nota.setValorIssqn(lerDouble(row.getCell(8))); // <--- CORREÇÃO APLICADA AQUI
                nota.setRetido(lerString(row.getCell(9)));
                nota.setStatus(lerString(row.getCell(10)));
                nota.setLocalRecolhimento(lerString(row.getCell(11)));
                notas.add(nota);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao ler o conteúdo do Excel. Verifique o formato e o conteúdo do arquivo.");
        }
        return notas;
    }

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