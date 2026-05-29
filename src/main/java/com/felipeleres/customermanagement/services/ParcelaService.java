package com.felipeleres.customermanagement.services;

import com.felipeleres.customermanagement.dto.PagamentoDTO;
import com.felipeleres.customermanagement.dto.ParcelaDTO;
import com.felipeleres.customermanagement.entities.Pagamento;
import com.felipeleres.customermanagement.entities.Parcela;
import com.felipeleres.customermanagement.repositories.ParcelaRepository;
import com.felipeleres.customermanagement.services.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ParcelaService {

    @Autowired
    ParcelaRepository parcelaRepository;

    @Transactional
    public ParcelaDTO atualizarParcela (Long id , ParcelaDTO parcelaDTO){
        Parcela parcela = parcelaRepository.getReferenceById(id);
        if(parcelaRepository.existsById(id)) {

            parcela.setStatusPagamento(parcelaDTO.getStatusPagamento());
            parcela = parcelaRepository.save(parcela);
        }
        else{
            throw new ResourceNotFoundException("Parcela não Encontrada!");
        }
        return new ParcelaDTO(parcela);
    }

    @Transactional(readOnly = true)
    public ParcelaDTO consultarParcela(Long id){

        Optional<Parcela> resultado = parcelaRepository.findById(id);
        Parcela par = resultado.orElseThrow(() -> new ResourceNotFoundException("Parcela não encontrada!"));
        return new ParcelaDTO(par);
    }

    @Transactional(readOnly = true)
    public Page<ParcelaDTO> consultarParcelas(Pageable pageable){
        Page<ParcelaDTO> resultado = parcelaRepository.buscarParcelas(pageable);
        return resultado;
    }

}
