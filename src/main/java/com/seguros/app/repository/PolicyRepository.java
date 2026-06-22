
package com.seguros.app.repository;

import com.seguros.app.model.Policy;
import com.seguros.app.model.Client;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PolicyRepository extends JpaRepository<Policy, Long>{ 
    
    List<Policy> findByState(String state);
    
    // Buscar pólizas por cliente
    List<Policy> findByClient(Client client);
    
    @Query("""
       SELECT p.insurance.name, COUNT(p)
       FROM Policy p
       GROUP BY p.insurance.name
       ORDER BY COUNT(p) DESC
       """)
            
    List<Object[]> countPoliciesByInsurance();
    
    // Devuelve el numero de polizas agrupadas por estado
    // Returns the number of policies grouped by state
    @Query("""
           SELECT p.state, COUNT(p)
           FROM Policy p
           WHERE p.state IS NOT NULL
             AND p.state <> ''
           GROUP BY p.state
           ORDER BY COUNT(p) DESC
           """)
    List<Object[]> countPoliciesByState();
    
    
}
