package com.felipeleres.customermanagement.services;

import com.felipeleres.customermanagement.dto.*;
import com.felipeleres.customermanagement.entities.Pagamento;
import com.felipeleres.customermanagement.entities.Parcela;
import com.felipeleres.customermanagement.entities.Processo;
import com.felipeleres.customermanagement.enums.StatusPagamento;
import com.felipeleres.customermanagement.repositories.PagamentoRepository;
import com.felipeleres.customermanagement.repositories.ParcelaRepository;
import com.felipeleres.customermanagement.repositories.ProcessoRepository;
import com.felipeleres.customermanagement.services.exception.DataBaseException;
import com.felipeleres.customermanagement.services.exception.PagamentoException;
import com.felipeleres.customermanagement.services.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private ProcessoRepository processoRepository;


    @Transactional(readOnly = true)
    public Page<PagamentoReturnDTO> buscarTodos(Pageable page){

        Page<PagamentoReturnDTO> pag = pagamentoRepository.buscarPagamentos(page);
        return pag;
    }
    @Transactional(readOnly = true)
    public PagamentoReturnDTO buscarPagamento (Long id){

        Optional<Pagamento> resultado = pagamentoRepository.findById(id);
        Pagamento pagamento = resultado.orElseThrow(()-> new ResourceNotFoundException("Pagamento não encontrado"));
        PagamentoReturnDTO dto = new PagamentoReturnDTO(pagamento);
        return dto;
    }




    @Transactional(readOnly = true)
    public FinanceiroDTO totalRecebido(){

        List<Parcela> parcelas = parcelaRepository.buscarFinanceiro();

        BigDecimal totalPago = BigDecimal.ZERO;
        BigDecimal totalEmAberto = BigDecimal.ZERO;
        BigDecimal totalEmAtraso = BigDecimal.ZERO;

        LocalDate hoje = LocalDate.now();


        for(Parcela par : parcelas){

            if (par.getDataParcela().isAfter(hoje)){
                totalEmAberto = totalEmAberto.add(par.getValor());
            }

            if (par.getDataParcela().isBefore(hoje) && (par.getStatusPagamento() == StatusPagamento.EM_ATRASO || par.getStatusPagamento() == StatusPagamento.AGUARDANDO_PAGAMENTO) ){
                totalEmAtraso = totalEmAtraso.add(par.getValor());
            }

            if(par.getStatusPagamento() == StatusPagamento.PAGO) {
                totalPago = totalPago.add(par.getValor());
            }
        }

        FinanceiroDTO dto =  new FinanceiroDTO(totalPago,totalEmAberto,totalEmAtraso);
        return dto;
    }


    @Transactional
    public PagamentoDTO cadastrarPagamento (PagamentoDTO pagamentoDTO){

        Pagamento pagamento = new Pagamento();
        Processo processo =  processoRepository.getReferenceById(pagamentoDTO.getProcessoID());

        pagamento.setStatusPagamento(pagamentoDTO.getStatusPagamento());
        pagamento.setProcesso(processo);
        pagamento.setQuantidadeParcelas(pagamentoDTO.getQuantidadeParcelas());
        pagamento.setValorTotal(pagamentoDTO.getValorTotal());

        for(ParcelaDTO par: pagamentoDTO.getParcelas()){
            Parcela parcela = dtoToEntity(par);
            parcela.setPagamento(pagamento);
            pagamento.addParcela(parcela);
        }

        pagamento = pagamentoRepository.save(pagamento);

        PagamentoDTO dto = new PagamentoDTO(pagamento);
        for(Parcela parcela : pagamento.getParcelas()){
            dto.addParcelas(new ParcelaDTO(parcela));
        }
        return dto;
    }

    @Transactional
    public PagamentoDTO atualizarPagamento(Long id, PagamentoDTO pagamentoDTO){


         if(!pagamentoRepository.existsById(id)){
             throw new ResourceNotFoundException("Pagamento não encontrado!");
         }

        Pagamento pagamento = pagamentoRepository.getReferenceById(id);

        for(Parcela par : pagamento.getParcelas()){
            if (par.getStatusPagamento().equals(StatusPagamento.AGUARDANDO_PAGAMENTO)||par.getStatusPagamento().equals(StatusPagamento.EM_ATRASO)){
                throw new PagamentoException("Existem pendências no pagamento");
            }
        }
        pagamento.setStatusPagamento(pagamentoDTO.getStatusPagamento());
        pagamento.setQuantidadeParcelas(pagamentoDTO.getQuantidadeParcelas());
        pagamento.setValorTotal(pagamentoDTO.getValorTotal());

        pagamento.limparParcelas();

        for(ParcelaDTO par : pagamentoDTO.getParcelas()){
            Parcela parcela = new Parcela();
            parcela.setStatusPagamento(par.getStatusPagamento());
            parcela.setDataParcela(par.getDataParcela());
            parcela.setValor(par.getValor());
            pagamento.addParcela(parcela);
        }

         pagamento = pagamentoRepository.save(pagamento);

         PagamentoDTO dto = new PagamentoDTO(pagamento);

         for(Parcela parce : pagamento.getParcelas()){
             dto.addParcelas(new ParcelaDTO(parce));
         }
         return dto;

    }

    public Parcela dtoToEntity(ParcelaDTO parcelaDTO){
        Parcela parcela = new Parcela();
        parcela.setValor(parcelaDTO.getValor());
        parcela.setDataParcela(parcelaDTO.getDataParcela());
        parcela.setStatusPagamento(parcelaDTO.getStatusPagamento());
        return parcela;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deletar (Long id){
        if(!pagamentoRepository.existsById(id)){
            throw new ResourceNotFoundException("Pagamento não localizado! ");
        }
        try {
            pagamentoRepository.deleteById(id);
        }catch(DataIntegrityViolationException e){
            throw new DataBaseException("Falha de integridade referencial!");
        }
    }

}
