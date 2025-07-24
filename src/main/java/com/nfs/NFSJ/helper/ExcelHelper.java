package com.nfs.NFSJ.helper;

import com.nfs.NFSJ.models.NotaFiscalModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ExcelHelper {

    public static List<NotaFiscalModel> lerNotasDoExcel(InputStream is) {
        List<NotaFiscalModel> notas = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0); // Pega a primeira aba

            Iterator<Row> rows = sheet.iterator();

            // Pula a primeira linha (cabeçalho)
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                NotaFiscalModel nota = new NotaFiscalModel();

                // Supondo a ordem das colunas conforme seu modelo:
                // 0: dataEmissao (Date)
                // 1: cnpjTomador (String)
                // 2: tomador (String)
                // 3: valorNF (Double)
                // 4: valorDeducoes (Double)
                // 5: valorBase (Double)
                // 6: aliquota (Double)
                // 7: valorIssqn (Double)
                // 8: retido (String)
                // 9: status (String)
                // 10: localRecolhimento (String)

                Cell cell;

                // Data Emissão
                cell = currentRow.getCell(0);
                if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                    LocalDate data = cell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    nota.setDataEmissao(data);
                }

                // CNPJ Tomador
                cell = currentRow.getCell(1);
                if (cell != null) {
                    nota.setCnpjTomador(cell.getStringCellValue());
                }

                // Tomador
                cell = currentRow.getCell(2);
                if (cell != null) {
                    nota.setTomador(cell.getStringCellValue());
                }

                // Valor NF
                cell = currentRow.getCell(3);
                if (cell != null) {
                    nota.setValorNF(cell.getNumericCellValue());
                }

                // Valor Deduções
                cell = currentRow.getCell(4);
                if (cell != null) {
                    nota.setValorDeducoes(cell.getNumericCellValue());
                }

                // Valor Base
                cell = currentRow.getCell(5);
                if (cell != null) {
                    nota.setValorBase(cell.getNumericCellValue());
                }

                // Alíquota
                cell = currentRow.getCell(6);
                if (cell != null) {
                    nota.setAliquota(cell.getNumericCellValue());
                }

                // Valor ISSQN
                cell = currentRow.getCell(7);
                if (cell != null) {
                    nota.setValorIssqn(cell.getNumericCellValue());
                }

                // Retido
                cell = currentRow.getCell(8);
                if (cell != null) {
                    nota.setRetido(cell.getStringCellValue());
                }

                // Status
                cell = currentRow.getCell(9);
                if (cell != null) {
                    nota.setStatus(cell.getStringCellValue());
                }

                // Local Recolhimento
                cell = currentRow.getCell(10);
                if (cell != null) {
                    nota.setLocalRecolhimento(cell.getStringCellValue());
                }

                notas.add(nota);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notas;
    }
}
