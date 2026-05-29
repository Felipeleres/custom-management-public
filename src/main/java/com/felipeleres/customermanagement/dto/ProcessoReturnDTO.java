package com.felipeleres.customermanagement.dto;

import com.felipeleres.customermanagement.entities.Processo;
import com.felipeleres.customermanagement.enums.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProcessoReturnDTO {

    private Long id;
    @NotNull(message = "Campo requirido, informe um número de processo!")
    private String numero;
    private String descricao;
    private LocalDate data;
    @NotNull(message = "Campo requirido, informe um número de processo!")
    @Positive(message = "O valor deve ser positivo!")
    private String situacao;
    private ClienteDTO cliente;

    public ProcessoReturnDTO(){

    }

    public ProcessoReturnDTO(Long id, String descricao, String numero, LocalDate data, String situacao, ClienteDTO cliente) {
        this.id = id;
        this.descricao = descricao;
        this.numero = numero;
        this.data = data;
        this.situacao = situacao;
        this.cliente = cliente;
    }

    public ProcessoReturnDTO(Processo processo) {
        id = processo.getId();
        descricao = processo.getDescricao();
        numero = processo.getNumero();
        data = processo.getData();
        situacao = processo.getSituacao();
        cliente =  new ClienteDTO(processo.getCliente());
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

    public ClienteDTO getCliente(){
        return cliente;
    }

    public void setCliente(ClienteDTO cliente){
        this.cliente = cliente;
    }

}