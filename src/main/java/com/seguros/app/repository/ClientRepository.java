
package com.seguros.app.repository;

import com.seguros.app.model.Client;
import com.seguros.app.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ClientRepository extends JpaRepository<Client, Long>{
    
    //Busca clientes por nombre
    List<Client> findByNameContainingIgnoreCase(String name);
    
    //Buscar cliente por usuario
    Client findByUser(User user);
    
    //Busca cliente asociado al username
    Client findByUserUsername(String username);
    
    
}
