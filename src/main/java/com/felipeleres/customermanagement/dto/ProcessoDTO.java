package com.felipeleres.customermanagement.dto;

import com.felipeleres.customermanagement.entities.Processo;
import com.felipeleres.customermanagement.enums.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProcessoDTO {

    private Long id;
    private String numero;
    private String descricao;
    private LocalDate data;
    private String situacao;
    private String clienteNome;
    private Long clienteId;

    public ProcessoDTO(){

    }

    public ProcessoDTO(Long id, String descricao, String numero, LocalDate data, String situacao,String clienteNome,Long clienteId) {
        this.id = id;
        this.descricao = descricao;
        this.numero = numero;
        this.data = data;
        this.situacao = situacao;
        this.clienteNome = clienteNome;
        this.clienteId = clienteId;
    }

    public ProcessoDTO(Processo processo) {
        id = processo.getId();
        descricao = processo.getDescricao();
        numero = processo.getNumero();
        data = processo.getData();
        situacao = processo.getSituacao();
        clienteNome = processo.getCliente().getName();
        clienteId = processo.getCliente().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
}