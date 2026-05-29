package com.felipeleres.customermanagement.controllers;

import com.felipeleres.customermanagement.dto.ProcessoCliDTO;
import com.felipeleres.customermanagement.dto.ProcessoDTO;
import com.felipeleres.customermanagement.dto.ProcessoReturnDTO;
import com.felipeleres.customermanagement.services.ProcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/processo")
@Tag(name="Processo",description = "Operações com processos")
public class ProcessoController {

    @Autowired
    private ProcessoService processoService;

    @GetMapping
    @Operation(summary = "Consultar Processos", description = "Essa operação consulta todos os processos")
    public ResponseEntity<List<ProcessoReturnDTO>> buscarProcessos(){
        List<ProcessoReturnDTO> dto = processoService.buscarProcessos();
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Consultar Processo", description = "Essa operação consulta um processo")
    public ResponseEntity<ProcessoReturnDTO> processo(@PathVariable Long id){
        ProcessoReturnDTO processoreturnDTO = processoService.processo(id);
        return ResponseEntity.ok(processoreturnDTO);
    }


    @PostMapping
    @Operation(summary = "Inserir Processo", description = "Essa operação insere um processo")
    public ResponseEntity<ProcessoReturnDTO> inserirProcesso(@RequestBody ProcessoCliDTO processo){
        ProcessoReturnDTO dto = processoService.inserirProcesso(processo);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(processo.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }


    @PutMapping(value = "/{id}")
    @Operation(summary = "Atualizar Processo", description = "Essa operação atualiza um processo")
    public ResponseEntity<ProcessoDTO> atualizarProcesso(@PathVariable Long id, @RequestBody  ProcessoDTO processoDTO){
        processoDTO = processoService.atualizar(id,processoDTO);
        return ResponseEntity.ok(processoDTO);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletar Processo", description = "Essa operação deleta um processo")
    public ResponseEntity<Void> deletarProcesso(@PathVariable Long id){
        processoService.deletar(id);
        return ResponseEntity.noContent().build();
    }


}
