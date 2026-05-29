package com.felipeleres.customermanagement.dto;


import com.felipeleres.customermanagement.entities.Cliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class ClienteDTO {


    private Long id;

    @NotBlank
    @Size(min = 10,message = "Campo requerido, deve informar o nome do cliente!")
    private String name;

    private String cpf;
    private String email;
    private String telefone;


    public ClienteDTO (){

    }

    public ClienteDTO(Long id, String name, String cpf, String email, String telefone) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }

    public ClienteDTO (Cliente cliente){
        id = cliente.getId();
        name = cliente.getName();
        cpf = cliente.getCpf();
        email = cliente.getEmail();
        telefone = cliente.getTelefone();
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

    public void setEmail (String email){
        this.email = email;
    }

    public String getTelefone(){
        return telefone;
    }

    public void setTelefone(String telefone){
        this.telefone = telefone;
    }


}