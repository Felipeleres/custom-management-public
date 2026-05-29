package com.felipeleres.customermanagement.repositories;

import com.felipeleres.customermanagement.entities.Processo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProcessoRepository extends JpaRepository<Processo,Long > {

    @Query("""
            SELECT p FROM Processo p 
            LEFT JOIN FETCH p.cliente
            LEFT JOIN FETCH p.pagamento
""")
    public List<Processo> buscarTodosProcessos();

}
