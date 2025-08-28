package com.nfs.NFSJ.service;

import com.nfs.NFSJ.models.NotaFiscalModel;
import com.nfs.NFSJ.repository.NotaFiscalRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository repository;

    public NotaFiscalService(NotaFiscalRepository repository) {
        this.repository = repository;
    }

    public List<NotaFiscalModel> listarNotas() {
        return repository.findAll();
    }

    public NotaFiscalModel salvarNota(NotaFiscalModel nota) {
        if (nota.getTomador() != null && !nota.getTomador().trim().isEmpty()) {
            nota.setTomador(nota.getTomador().trim().toUpperCase());
        }
        if (nota.getStatusPagamento() == null) {
            nota.setStatusPagamento("PENDENTE");
        }

        Integer prazoDias = 30;

        if (nota.getTomador() != null) {
            switch (nota.getTomador()) {
                case "SOPRANO INDUSTRIA ELETROMETALURGICA EIRELI":
                    prazoDias = 15;
                    break;
                case "IMOHUB TECNOLOGIA LTDA":
                    prazoDias = 10;
                    break;
                case "TRANSPOTECH PECAS E SERVICOS LTDA.":
                    prazoDias = 15;
                    break;
                case "REUNION SOLUCOES DE MARKETING LTDA":
                    prazoDias = 10;
                    break;
                case "REMOTA TECNOLOGIA EM COMUNICACAO LTDA":
                    prazoDias = 23;
                    break;
                case "HY CITE PARTICIPACOES BRASIL LTDA.":
                    prazoDias = 15;
                    break;
                case "AGNES AG SOLUCOES TECNOLOGICAS LTDA":
                    prazoDias = 5;
                    break;
                case "COOPERATIVA CENTRAL DE CREDITO DE SANTA CATARINA E RIO GRANDE DO":
                    prazoDias = 15;
                    break;
                case "CALCRED S.A. - CREDITO, FINANCIAMENTO E INVESTIMENTO":
                    prazoDias = 60;
                    break;
                case "CALCARD S.A.":
                    prazoDias = 60;
                    break;
                case "CREDISIS - CENTRAL DE COOPERATIVAS DE CREDITO LTDA":
                    prazoDias = 5;
                    break;
                case "OFFICER SOFT INFORMATICA E CONSULTORIA LTDA":
                    prazoDias = 10;
                    break;
                case "COOPERATIVA CENTRAL DE CREDITO - AILOS":
                    prazoDias = 18;
                    break;
                case "APOLO ATACADO TEXTIL LTDA":
                    prazoDias = 30;
                    break;
                case "AMERICANAS S/A  EM RECUPERACAO JUDICIA":
                    prazoDias = 30;
                    break;

            //  case "VKS VISTORIAS LTDA":
                //    prazoDias = 0;
                //    break;

                case "SERASA S.A.":
                    prazoDias = 60;
                    break;
                case "COMPANHIA ULTRAGAZ S.A":
                    prazoDias = 30;
                    break;
                case "ANHEUSER-BUSCH INBEV NV":
                    prazoDias = 120;
                    break;
                case "CHEESECAKE LABS SOFTWARE S/A":
                    prazoDias = 10;
                    break;

            // case "ISABELLA PITAKI MEDICINA LTDA":
                //  prazoDias = 0;
                //  break;

                case "CONTABILIZEI HOLDING LTDA":
                    prazoDias = 30;
                    break;
                case "MAURO RESOLVE PAY LTDA":
                    prazoDias = 15;
                    break;
                case "BEBIDAS FRUKI SA":
                    prazoDias = 30;
                    break;
                case "GUARDIAN CONTROLE PARENTAL LTDA.":
                    prazoDias = 30;
                    break;
                case "PHILIPS CLINICAL INFORMATICS - SISTEMAS DE INFORMACAO LTDA.":
                    prazoDias = 15;
                    break;
                case "CALCENTER - CALCADOS CENTRO-OESTE LTDA":
                    prazoDias = 60;
                    break;
                case "BMW FINANCEIRA SA CREDITO FINANCIAMENTO E INVESTIMENTO":
                    prazoDias = 30;
                    break;
                default:
                    prazoDias = 30;
                    break;
            }
        }

        nota.setPrazoPagamentoDias(prazoDias);
        return repository.save(nota);
    }

    public void atualizarStatus(Long id, boolean status) {
        NotaFiscalModel nota = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota não encontrada"));

        if (status) {
            nota.setStatusPagamento("PAGO");
            nota.setDataPagamento(LocalDate.now());
        } else {
            nota.setStatusPagamento("PENDENTE");
            nota.setDataPagamento(null);
        }
        repository.save(nota);
    }

    public void calcularDetalhesDePrazo(NotaFiscalModel nota) {
        Integer prazoDias = nota.getPrazoPagamentoDias();

        if (prazoDias == null || prazoDias <= 0) {
            prazoDias = 30;
        }

        final int DIAS_PARA_ENTRAR_EM_ATENCAO = 7;

        if (nota.getDataEmissao() == null) {
            return;
        }

        LocalDate hoje = LocalDate.now();
        LocalDate dataVencimento = nota.getDataEmissao().plusDays(prazoDias);

        if ("PENDENTE".equals(nota.getStatusPagamento())) {
            long diasRestantes = ChronoUnit.DAYS.between(hoje, dataVencimento);
            nota.setDiasParaVencer(diasRestantes);

            if (diasRestantes < 0) {
                nota.setStatusPrazo("VENCIDO");
            } else if (diasRestantes <= DIAS_PARA_ENTRAR_EM_ATENCAO) {
                nota.setStatusPrazo("ATENCAO");
            } else {
                nota.setStatusPrazo("NO_PRAZO");
            }
        }
        else if ("PAGO".equals(nota.getStatusPagamento()) && nota.getDataPagamento() != null) {
            long diferencaDias = ChronoUnit.DAYS.between(nota.getDataPagamento(), dataVencimento);
            nota.setDiasPagamento(diferencaDias);
        }
    }
}