package com.nfs.NFSJ.helper;

import com.nfs.NFSJ.models.NotaFiscalModel;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {

    public static List<NotaFiscalModel> lerNotasDoExcel(InputStream is, String filename) {
        List<NotaFiscalModel> notas = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            boolean primeiraLinha = true;
            for (Row row : sheet) {
                if (primeiraLinha) {
                    primeiraLinha = false; // pula cabeçalho
                    continue;
                }

                NotaFiscalModel nota = new NotaFiscalModel();

                Cell cell;

                // Data Emissao (col 0)
                cell = row.getCell(1);
                if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                    LocalDate data = cell.getDateCellValue()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    nota.setDataEmissao(data);
                }

                // CNPJ Tomador (col 1)
                cell = row.getCell(2);
                if (cell != null) {
                    cell.setCellType(CellType.STRING);
                    nota.setCnpjTomador(cell.getStringCellValue());
                }

                // Tomador (col 2)
                cell = row.getCell(3);
                if (cell != null) {
                    cell.setCellType(CellType.STRING);
                    nota.setTomador(cell.getStringCellValue());
                }

                // Valor NF (col 3)
                cell = row.getCell(4);
                if (cell != null) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        nota.setValorNF(cell.getNumericCellValue());
                    } else if (cell.getCellType() == CellType.STRING) {
                        try {
                            nota.setValorNF(Double.parseDouble(cell.getStringCellValue().replace(",", ".")));
                        } catch (Exception e) {
                            nota.setValorNF(0.0);
                        }
                    }
                }

                // Valor Deducoes (col 4)
                cell = row.getCell(5);
                if (cell != null) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        nota.setValorDeducoes(cell.getNumericCellValue());
                    } else if (cell.getCellType() == CellType.STRING) {
                        try {
                            nota.setValorDeducoes(Double.parseDouble(cell.getStringCellValue().replace(",", ".")));
                        } catch (Exception e) {
                            nota.setValorDeducoes(0.0);
                        }
                    }
                }

                // Valor Base (col 5)
                cell = row.getCell(6);
                if (cell != null) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        nota.setValorBase(cell.getNumericCellValue());
                    } else if (cell.getCellType() == CellType.STRING) {
                        try {
                            nota.setValorBase(Double.parseDouble(cell.getStringCellValue().replace(",", ".")));
                        } catch (Exception e) {
                            nota.setValorBase(0.0);
                        }
                    }
                }

                // Aliquota (col 6)
                cell = row.getCell(7);
                if (cell != null) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        nota.setAliquota(cell.getNumericCellValue());
                    } else if (cell.getCellType() == CellType.STRING) {
                        try {
                            nota.setAliquota(Double.parseDouble(cell.getStringCellValue().replace(",", ".")));
                        } catch (Exception e) {
                            nota.setAliquota(0.0);
                        }
                    }
                }

                // Valor ISSQN (col 8)
                cell = row.getCell(7);
                if (cell != null) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        nota.setValorIssqn(cell.getNumericCellValue());
                    } else if (cell.getCellType() == CellType.STRING) {
                        try {
                            nota.setValorIssqn(Double.parseDouble(cell.getStringCellValue().replace(",", ".")));
                        } catch (Exception e) {
                            nota.setValorIssqn(0.0);
                        }
                    }
                }

                // Retido (col 8)
                cell = row.getCell(9);
                if (cell != null) {
                    cell.setCellType(CellType.STRING);
                    nota.setRetido(cell.getStringCellValue());
                }

                // Status (col 9)
                cell = row.getCell(10);
                if (cell != null) {
                    cell.setCellType(CellType.STRING);
                    nota.setStatus(cell.getStringCellValue());
                }

                // Local Recolhimento (col 10)
                cell = row.getCell(11);
                if (cell != null) {
                    cell.setCellType(CellType.STRING);
                    nota.setLocalRecolhimento(cell.getStringCellValue());
                }

                notas.add(nota);
            }
        } catch (EncryptedDocumentException e) {
            // Arquivo protegido por senha
            throw e;
        } catch (Exception e) {
            // Outro erro qualquer: formato inválido, conteúdo corrompido etc
            e.printStackTrace(); // assim você vê o erro no terminal
            throw new IllegalArgumentException("Erro ao ler o conteúdo do Excel. Verifique o arquivo enviado.");
        }
        return notas;
    }
}

