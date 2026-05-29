package com.felipeleres.customermanagement.controllers;

import com.felipeleres.customermanagement.dto.ParcelaDTO;
import com.felipeleres.customermanagement.services.ParcelaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/parcela")
@Tag(name="Parcela", description= "Operações com parcela")
public class ParcelaController {

    @Autowired
    ParcelaService parcelaService;

    @PutMapping(value = "/{id}")
    @Operation(summary = "Atualizar Parcela", description = "Essa operação atualiza uma parcela")
    public ResponseEntity<ParcelaDTO> atualizarParcela(@PathVariable Long id,  @RequestBody  ParcelaDTO parcelaDTO){
        ParcelaDTO dto = parcelaService.atualizarParcela(id,parcelaDTO);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Consultar Parcela", description = "Essa operação consulta uma parcela")
    public ResponseEntity<ParcelaDTO> consultarParcela(@PathVariable Long id){
        ParcelaDTO dto = parcelaService.consultarParcela(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(summary = "Consultar Parcelas", description = "Essa operação consulta todas as parcelas")
    public ResponseEntity<Page<ParcelaDTO>> consultarParcelas(Pageable pageable){
        Page<ParcelaDTO> dto = parcelaService.consultarParcelas(pageable);
        return ResponseEntity.ok(dto);
    }

}
