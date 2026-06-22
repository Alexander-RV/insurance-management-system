
package com.seguros.app.service;

import com.seguros.app.model.Policy;
import com.seguros.app.repository.PolicyRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PolicyService {
    
    private final PolicyRepository repo;

    public PolicyService(PolicyRepository repo) {
        this.repo = repo;
    }

    public List<Policy> listAll() {
        return repo.findAll();
    }

    public Policy save(Policy p) {
        return repo.save(p);
    }

    public Policy get(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
    
    //Este metodo busca las pólizas por estado (Active, Exprired, etc)
    public List<Policy> searchByState(String state) {
        
        //Consulta al repo y devuelve solo las polizas con estado
        return repo.findByState(state);
    }
    
    //Buscar poliza de un cliente en expecifico
    public List<Policy> findByClient(com.seguros.app.model.Client client) {
        return repo.findByClient(client);
    }
    
    public List<Object[]> countPoliciesByInsurance() {
        return repo.countPoliciesByInsurance();
    }
    
    public List<Object[]> countPoliciesByState() {
        
        //Llama la consulta dl repo y devuelve los resultados
        return repo.countPoliciesByState();
    }
}
