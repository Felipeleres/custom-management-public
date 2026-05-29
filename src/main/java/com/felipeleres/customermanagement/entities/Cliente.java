package com.felipeleres.customermanagement.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String cpf;
    @Column(unique = true)
    private String email;
    private String telefone;
    @OneToMany(mappedBy = "cliente")
    private List<Processo> processos = new ArrayList<>();


    public Cliente (){


    }

    public Cliente(Long id, String name, String cpf,String email, String telefone) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email =  email;
    }

    public String getTelefone(){
        return telefone;
    }

    public void setTelefone(String telefone){
        this.telefone = telefone;
    }

    public List<Processo> getProcessos() {
        return processos;
    }

    public void setProcessos(Processo processo) {
        processos.add(processo);
    }
}
