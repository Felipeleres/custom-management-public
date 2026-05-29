package com.felipeleres.customermanagement.entities;

import com.felipeleres.customermanagement.dto.PagamentoReturnDTO;
import com.felipeleres.customermanagement.enums.StatusPagamento;
import com.felipeleres.customermanagement.enums.FormaPagamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;
    @OneToOne
    @JoinColumn(name = "processo_id")
    private Processo processo;
    private Integer quantidadeParcelas;
    private BigDecimal valorTotal;



    @OneToMany(mappedBy = "pagamento",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Parcela> parcelas = new ArrayList<>();

    public Pagamento(){

    }

    public Pagamento(Long id, StatusPagamento statusPagamento, Processo processo, Integer quantidadeParcelas,BigDecimal valorTotal) {
        this.id = id;
        this.statusPagamento = statusPagamento;
        this.processo = processo;
        this.quantidadeParcelas = quantidadeParcelas;
        this.valorTotal =  valorTotal;
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

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public Integer getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(Integer quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public List<Parcela> getParcelas(){
        return parcelas;
    }

    public void addParcela(Parcela parcela){
        parcelas.add(parcela);
        parcela.setPagamento(this);
    }

    public void removerParcela(Parcela parcela){
        parcelas.remove(parcela);
        parcela.setPagamento(null);

    }

    public void limparParcelas(){
        parcelas.clear();
    }

    public BigDecimal getValorTotal(){
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valor){
        this.valorTotal = valor;
    }



}
