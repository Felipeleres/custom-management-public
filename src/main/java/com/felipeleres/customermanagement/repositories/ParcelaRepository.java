package com.felipeleres.customermanagement.repositories;


import com.felipeleres.customermanagement.dto.FinanceiroDTO;
import com.felipeleres.customermanagement.dto.ParcelaDTO;
import com.felipeleres.customermanagement.entities.Parcela;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParcelaRepository extends JpaRepository<Parcela,Long > {

    @Query("""
            SELECT NEW com.felipeleres.customermanagement.dto.ParcelaDTO(p.id,p.valor,p.dataParcela,p.statusPagamento,pag.id,proc.numero,proc.id,cli.id,cli.name)
            FROM Parcela p
            LEFT JOIN p.pagamento pag
            LEFT JOIN pag.processo proc
            LEFT JOIN proc.cliente cli
            """)
    Page<ParcelaDTO> buscarParcelas(Pageable pageable);

    @Query("""
            SELECT p FROM Parcela p 
            LEFT JOIN FETCH p.pagamento pag
            LEFT JOIN FETCH pag.processo pro
            LEFT JOIN FETCH pro.cliente 
            """)
    List<Parcela> buscarFinanceiro();

}
