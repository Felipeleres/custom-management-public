package com.felipeleres.customermanagement.entities;

import com.felipeleres.customermanagement.enums.FormaPagamento;
import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_processo")
public class Processo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numero;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private String descricao;

    @OneToOne(mappedBy = "processo")
    private Pagamento pagamento;
    private String situacao;
    private LocalDate data;


    public Processo(){

    }

    public Processo(Long id, String numero, Cliente cliente, String descricao,Pagamento pagamento, String situacao, LocalDate data) {
        this.id = id;
        this.numero = numero;
        this.cliente = cliente;
        this.descricao = descricao;
        this.situacao = situacao;
        this.data = data;
        this.pagamento = pagamento;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }
}
