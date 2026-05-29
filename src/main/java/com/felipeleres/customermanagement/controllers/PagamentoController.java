package com.felipeleres.customermanagement.controllers;

import com.felipeleres.customermanagement.dto.FinanceiroDTO;
import com.felipeleres.customermanagement.dto.PagamentoDTO;
import com.felipeleres.customermanagement.dto.PagamentoReturnDTO;
import com.felipeleres.customermanagement.services.PagamentoService;
import com.felipeleres.customermanagement.services.ProcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/pagamento")
@Tag(name = "Pagamento",description = "Operações com pagamento")

public class PagamentoController {

    @Autowired
    PagamentoService pagamentoService;

    @GetMapping
    @Operation(summary = "Consultar Pagamentos", description = "Essa operação consulta todos os pagamentos")
    public ResponseEntity<Page<PagamentoReturnDTO>> buscarPagamentos(Pageable page){
        Page<PagamentoReturnDTO> dto =  pagamentoService.buscarTodos(page);
        PageRequest.of(0, 20, Sort.by("id").descending());
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PagamentoReturnDTO> buscarPagamento (@PathVariable Long id){
        PagamentoReturnDTO dto = pagamentoService.buscarPagamento(id);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping(value = "/total-pago")
    @Operation(summary = "Consultar Financeiro", description = "Essa operação consulta todos os valores financeiro")
    public ResponseEntity<FinanceiroDTO> totalRecebido (){
        FinanceiroDTO dto = pagamentoService.totalRecebido();
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    @Operation(summary = "Cadastrar Pagamento", description = "Essa operação cadastra um pagamento")
    public ResponseEntity<PagamentoDTO> cadastrarPagamento(@RequestBody PagamentoDTO pagamentoDTO){
        PagamentoDTO dto = pagamentoService.cadastrarPagamento(pagamentoDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(pagamentoDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Atualizar Pagamento", description = "Essa operação atualiza um pagamento")
    public ResponseEntity<PagamentoDTO> atualizarPagamento(@PathVariable  Long id, @RequestBody PagamentoDTO pagamentoDTO){
        PagamentoDTO dto = pagamentoService.atualizarPagamento(id,pagamentoDTO);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletar Pagamento", description = "Essa operação deleta um pagamento")
    public ResponseEntity<Void> deletar (@PathVariable Long id){
        pagamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
