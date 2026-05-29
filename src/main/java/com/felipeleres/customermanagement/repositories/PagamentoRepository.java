package com.felipeleres.customermanagement.repositories;

import com.felipeleres.customermanagement.dto.FinanceiroDTO;
import com.felipeleres.customermanagement.dto.PagamentoReturnDTO;
import com.felipeleres.customermanagement.entities.Pagamento;
import com.felipeleres.customermanagement.entities.Processo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PagamentoRepository extends JpaRepository<Pagamento,Long > {
/*
    @Query(
            value = """
        SELECT DISTINCT p
        FROM Pagamento p
        LEFT JOIN FETCH p.parcelas
    """,
            countQuery = "SELECT COUNT(p) FROM Pagamento p"
    )
    Page<Pagamento> buscarTodosComParcelas(Pageable pageable);
*/

    @Query("""
            SELECT new com.felipeleres.customermanagement.dto.PagamentoReturnDTO(p.id,p.statusPagamento, 
            p.processo.id,p.quantidadeParcelas,p.valorTotal,cli.name) FROM
            Pagamento p
            LEFT JOIN p.processo proce
            LEFT JOIN proce.cliente cli
            """)
    public Page<PagamentoReturnDTO> buscarPagamentos(Pageable pageable);


}
