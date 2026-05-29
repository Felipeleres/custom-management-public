package com.felipeleres.customermanagement.dto;

import com.felipeleres.customermanagement.entities.Cliente;
import com.felipeleres.customermanagement.entities.Pagamento;
import com.felipeleres.customermanagement.entities.Parcela;
import com.felipeleres.customermanagement.entities.Processo;
import com.felipeleres.customermanagement.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ParcelaDTO {


    private Long id;
    private BigDecimal valor;
    private LocalDate dataParcela;
    private StatusPagamento statusPagamento;
    private Long pagamentoId;
    private String numeroProcesso;
    private Long processoId;
    private Long clienteId;
    private String nomeCliente;

    public ParcelaDTO(){

    }

    public ParcelaDTO(Long id, BigDecimal valor, LocalDate dataParcela, StatusPagamento statusPagamento, Long pagamentoId,String numeroProcesso,Long processoId,Long clienteId,String nomeCliente ) {
        this.id = id;
        this.valor = valor;
        this.dataParcela = dataParcela;
        this.statusPagamento = statusPagamento;
        this.pagamentoId = pagamentoId;
        this.numeroProcesso = numeroProcesso;
        this.processoId = processoId;
        this.clienteId = clienteId;
        this.nomeCliente = nomeCliente;
    }

    public ParcelaDTO(Parcela parcela) {
        id = parcela.getId();
        valor = parcela.getValor();
        dataParcela = parcela.getDataParcela();
        statusPagamento = parcela.getStatusPagamento();
        if (parcela.getPagamento() != null) {
            Pagamento pagamento = parcela.getPagamento();
            this.pagamentoId = pagamento.getId();

            if (pagamento.getProcesso() != null) {
                Processo processo = pagamento.getProcesso();
                this.processoId = processo.getId();
                this.numeroProcesso = processo.getNumero();

                if (processo.getCliente() != null) {
                    Cliente cliente = processo.getCliente();
                    this.clienteId = cliente.getId();
                    this.nomeCliente = cliente.getName();
                }
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataParcela() {
        return dataParcela;
    }

    public void setDataParcela(LocalDate dataParcela) {
        this.dataParcela = dataParcela;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public Long getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(Long pagamentoId) {
        this.pagamentoId = pagamentoId;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Long getProcessoId() {
        return processoId;
    }

    public void setProcessoId(Long processoId) {
        this.processoId = processoId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }
}
