
package com.seguros.app.service;

import com.seguros.app.model.Insurance;
import com.seguros.app.repository.InsuranceRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InsuranceService {
    
    private final InsuranceRepository repo;
    
    public InsuranceService(InsuranceRepository repo) {
        this.repo = repo;
    }
    
    public List<Insurance> listAll() {
        return repo.findAll();
    }
    
    public Insurance save(Insurance i) {
        return repo.save(i);
    }
    
    public Insurance get(Long id) {
        return repo.findById(id).orElse(null);
    }
    
    public void delete(Long id) {
        repo.deleteById(id);
    }
    public List<Insurance> searchByType(String type) {
        return repo.findByTypeContainingIgnoreCase(type);
    }
}
