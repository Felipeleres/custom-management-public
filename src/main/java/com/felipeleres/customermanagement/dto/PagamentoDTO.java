package com.felipeleres.customermanagement.dto;

import com.felipeleres.customermanagement.entities.Pagamento;
import com.felipeleres.customermanagement.entities.Parcela;
import com.felipeleres.customermanagement.entities.Processo;
import com.felipeleres.customermanagement.enums.StatusPagamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDTO {


    private Long id;
    private StatusPagamento statusPagamento;
    private Long processoID;
    private Integer quantidadeParcelas;
    private BigDecimal valorTotal;
    private List<ParcelaDTO> parcelas = new ArrayList<>();

    public PagamentoDTO(){

    }

    public PagamentoDTO(Long id, StatusPagamento statusPagamento, Long processo, Integer quantidadeParcelas,BigDecimal valorTotal) {
        this.id = id;
        this.statusPagamento = statusPagamento;
        this.processoID = processo;
        this.quantidadeParcelas = quantidadeParcelas;
        this.valorTotal =  valorTotal;
    }


    public PagamentoDTO(Pagamento pagamento) {
        id = pagamento.getId();
        statusPagamento = pagamento.getStatusPagamento();
        processoID = pagamento.getProcesso().getId();
        quantidadeParcelas = pagamento.getQuantidadeParcelas();
        valorTotal = pagamento.getValorTotal();

        for(Parcela par : pagamento.getParcelas()){
            parcelas.add(new ParcelaDTO(par));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public Long getProcessoID() {
        return processoID;
    }

    public void setProcessoID(Long processoID) {
        this.processoID = processoID;
    }

    public Integer getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(Integer quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public void addParcelas(ParcelaDTO parcela){
        parcelas.add(parcela);
    }

    public List<ParcelaDTO> getParcelas(){
        return parcelas;
    }

    public void removerParcela(ParcelaDTO parcela){
        parcelas.remove(parcela);
    }

    public BigDecimal getValorTotal() {return valorTotal;}

    public void setValorTotal(BigDecimal valorTotal) {this.valorTotal = valorTotal;}
}
