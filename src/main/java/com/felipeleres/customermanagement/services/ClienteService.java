package com.felipeleres.customermanagement.services;

import aj.org.objectweb.asm.commons.TryCatchBlockSorter;
import com.felipeleres.customermanagement.dto.ClienteDTO;
import com.felipeleres.customermanagement.dto.ClienteProDTO;
import com.felipeleres.customermanagement.entities.Cliente;
import com.felipeleres.customermanagement.repositories.ClienteRepository;
import com.felipeleres.customermanagement.services.exception.DadosIncompletoException;
import com.felipeleres.customermanagement.services.exception.DataBaseException;
import com.felipeleres.customermanagement.services.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public Page<ClienteDTO> buscarTodos(Pageable pageable){
        Page<Cliente> clientes = clienteRepository.findAll(pageable);
        return clientes.map(x -> new ClienteDTO(x));
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscarCliente(Long id){

            Optional<Cliente> resultado = clienteRepository.findById(id);
            Cliente cliente = resultado.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado!"));
            return new ClienteDTO(cliente);
    }
    @Transactional(readOnly = true)
    public List<ClienteProDTO> buscarClientePorNome(String nome){
        List<Cliente> clientes = clienteRepository.buscarClientePorNome(nome);
        return clientes.stream().map(x -> new ClienteProDTO(x)).toList();
    }

    @Transactional
    public ClienteDTO inserir (ClienteDTO clienteDTO){

        Cliente cliente = new Cliente();
        cliente.setName(clienteDTO.getName());

        String cpf = clienteDTO.getCpf();
/*
        if(cpf != null){

            cpf = cpf.replaceAll("\\D","");

            if(cpf.length() != 11){
                throw new DadosIncompletoException("Cpf inválido!!");
            }

        }
*/
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());

        cliente = clienteRepository.save(cliente);

        return new ClienteDTO(cliente);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO clienteDTO){

        try {

            Cliente cliente = clienteRepository.getReferenceById(id);

            cliente.setName(clienteDTO.getName());
            cliente.setCpf(clienteDTO.getCpf());
            cliente.setEmail(clienteDTO.getEmail());
            cliente.setTelefone(clienteDTO.getTelefone());
            cliente = clienteRepository.save(cliente);
            return new ClienteDTO(cliente);
        } catch(EntityNotFoundException e){
            throw new ResourceNotFoundException("Cliente não encontrado!");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deletar(Long id){
        if(!clienteRepository.existsById(id)){
            throw new ResourceNotFoundException("Cliente não encontrado!!");
        }
        try {
            clienteRepository.deleteById(id);
        }
        catch(DataIntegrityViolationException e) {
            throw new DataBaseException("Falha de integridade referencial!");
        }

    }

}
