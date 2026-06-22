
package com.seguros.app.repository;

import com.seguros.app.model.Insurance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
    List<Insurance> findByTypeContainingIgnoreCase(String type);
    
}
