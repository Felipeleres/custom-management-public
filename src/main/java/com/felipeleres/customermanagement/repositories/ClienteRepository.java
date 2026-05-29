package com.felipeleres.customermanagement.repositories;

import com.felipeleres.customermanagement.dto.ClienteDTO;
import com.felipeleres.customermanagement.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {


    public List<Cliente> findByNameContainingIgnoreCase (String nome);

    @Query("""
            SELECT DISTINCT c FROM Cliente c 
            LEFT JOIN FETCH c.processos p 
            LEFT JOIN FETCH p.pagamento 
            WHERE UPPER(c.name) LIKE UPPER(CONCAT('%',:nome,'%')) 
            """)
    public List<Cliente> buscarClientePorNome(@Param("nome") String nome);


}
