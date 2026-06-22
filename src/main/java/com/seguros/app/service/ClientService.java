
package com.seguros.app.service;

import com.seguros.app.model.Client;
import com.seguros.app.repository.ClientRepository;
import com.seguros.app.repository.UserRepository;
import com.seguros.app.model.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    
    //Repositorio de clientes
    private final ClientRepository repo;
    
    private final UserRepository userRepo;
    
    //Constructor con inyeccion de dependencais
    public ClientService(ClientRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }
    
    
    // Lista todos los clientes
    public List<Client> listAll() {
        return repo.findAll();
    }
    
    //Guarda los clientes
    public Client save(Client c) {
        return repo.save(c);
    }
    
    // Obtiene los clienets por su id
    public Client get(Long id) {
        return repo.findById(id).orElse(null);
    }
    
    
    public void delete(Long id) {
        repo.deleteById(id);
    }
    
    //Busca cliente por nombre
    public List<Client> searchByName(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }
    
    //Busca cliente por username
    public Client findByUsername(String username
    ) {
        User user =
                userRepo.findByUsername(
                        username
                );
        
        return repo.findByUser(user);
    }
    
    
    
}
