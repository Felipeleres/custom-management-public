package com.felipeleres.customermanagement.dto;

import com.felipeleres.customermanagement.entities.Pagamento;
import com.felipeleres.customermanagement.entities.Parcela;
import com.felipeleres.customermanagement.enums.StatusPagamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PagamentoReturnDTO {


    private Long id;
    private StatusPagamento statusPagamento;
    private Long processoId;
    private Integer quantidadeParcelas;
    private BigDecimal valorTotal;
    private String nomeCliente;

    public PagamentoReturnDTO(){

    }

    public PagamentoReturnDTO(Long id, StatusPagamento statusPagamento, Long processoId, Integer quantidadeParcelas,BigDecimal valorTotal,String nomeCliente) {
        this.id = id;
        this.statusPagamento = statusPagamento;
        this.processoId = processoId;
        this.quantidadeParcelas = quantidadeParcelas;
        this.valorTotal = valorTotal;
        this.nomeCliente = nomeCliente;
    }


    public PagamentoReturnDTO(Pagamento pagamento) {
        id = pagamento.getId();
        statusPagamento = pagamento.getStatusPagamento();
        processoId = pagamento.getProcesso().getId();
        quantidadeParcelas = pagamento.getQuantidadeParcelas();
        valorTotal = pagamento.getValorTotal();
        nomeCliente = pagamento.getProcesso().getCliente().getName();
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

    public Long getProcessoId() {
        return processoId;
    }

    public void setProcessoId(Long processoId) {
        this.processoId = processoId;
    }

    public Integer getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(Integer quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public BigDecimal getValorTotal() {return valorTotal;}

    public void setValorTotal(BigDecimal valorTotal) {this.valorTotal = valorTotal;}

    public String getNomeCliente(){
        return nomeCliente;
    }
    public void setNomeCliente(String nome){
        nomeCliente = nome;
    }

}
