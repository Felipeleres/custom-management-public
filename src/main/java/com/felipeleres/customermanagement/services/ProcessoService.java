package com.felipeleres.customermanagement.services;

import com.felipeleres.customermanagement.dto.FinanceiroDTO;
import com.felipeleres.customermanagement.dto.ProcessoCliDTO;
import com.felipeleres.customermanagement.dto.ProcessoDTO;
import com.felipeleres.customermanagement.dto.ProcessoReturnDTO;
import com.felipeleres.customermanagement.entities.Cliente;
import com.felipeleres.customermanagement.entities.Pagamento;
import com.felipeleres.customermanagement.entities.Processo;
import com.felipeleres.customermanagement.repositories.ClienteRepository;
import com.felipeleres.customermanagement.repositories.ProcessoRepository;
import com.felipeleres.customermanagement.services.exception.DataBaseException;
import com.felipeleres.customermanagement.services.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class ProcessoService {

    @Autowired
    public ProcessoRepository processoRepository;

    @Autowired
    public ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ProcessoReturnDTO> buscarProcessos(){
        //List<Processo> processos = processoRepository.findAll();
        List<Processo> processos = processoRepository.buscarTodosProcessos();
        return processos.stream().map(x -> new ProcessoReturnDTO(x)).toList();
    }

    @Transactional(readOnly = true)
    public ProcessoReturnDTO processo (Long id){
        Optional<Processo> resultado = processoRepository.findById(id);
        Processo processo = resultado.orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado!"));
        return new ProcessoReturnDTO(processo);
    }

    @Transactional
    public ProcessoReturnDTO inserirProcesso(ProcessoCliDTO processoCliDTO){
        Cliente cliente = clienteRepository.getReferenceById(processoCliDTO.getClienteId());
        Processo processo = new Processo();
        processo.setNumero(processoCliDTO.getNumero());
        processo.setDescricao(processoCliDTO.getDescricao());
        processo.setData(processoCliDTO.getData());
        processo.setSituacao(processoCliDTO.getSituacao());
        processo.setCliente(cliente);
        processo = processoRepository.save(processo);

        return new ProcessoReturnDTO(processo);
    }

    @Transactional
    public ProcessoDTO atualizar( Long id, ProcessoDTO processoDTO){

        try {
            Processo processo = processoRepository.getReferenceById(id);
            processo.setNumero(processoDTO.getNumero());
            processo.setDescricao(processoDTO.getDescricao());
            processo.setData(processoDTO.getData());
            processo.setSituacao(processoDTO.getSituacao());

            Cliente cliente = clienteRepository.getReferenceById(processo.getCliente().getId()
            );
            processo.setCliente(cliente);
            processo = processoRepository.save(processo);
            return new ProcessoDTO(processo);
        } catch(EntityNotFoundException e){
            throw new ResourceNotFoundException("Processo não encontrado!");
        }
    }

    @Transactional(propagation =  Propagation.SUPPORTS)
    public void deletar(Long id){

        if(!processoRepository.existsById(id)){
            throw new ResourceNotFoundException("Processo não encontrado!");
        }

        try{
            processoRepository.deleteById(id);
        }catch (DataIntegrityViolationException e ){
            throw  new DataBaseException("Falha de integridade referencial!");
        }

    }

}
