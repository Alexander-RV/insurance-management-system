package com.seguros.app.service;

import com.seguros.app.model.User;
import com.seguros.app.model.Client;
import com.seguros.app.repository.ClientRepository;
import com.seguros.app.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    // Repositorio para acceder a los usuarios
    private final UserRepository repo;
    
    private final ClientRepository clientRepo;

    // Componente para encriptar contraseñas
    private final PasswordEncoder passwordEncoder;
    
    /// Servicio de clie
    // Constructor con inyección de dependencias
    public UserService(

            UserRepository repo,

            ClientRepository clientRepo,

            PasswordEncoder passwordEncoder

    ) {

        this.repo = repo;

        this.clientRepo = clientRepo;

        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // LOGIN DE USUARIOS
    // =========================

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {

        // Busca el usuario en la base de datos
        User user = repo.findByUsername(username);

        // Si no existe, lanza excepción
        if (user == null) {

            throw new UsernameNotFoundException(
                    "User not found"
            );
        }

        // Retorna el usuario en formato Spring Security
        return org.springframework.security.core.userdetails.User

                .withUsername(user.getUsername())

                .password(user.getPassword())

                // Asigna los permisos/roles
                .authorities(user.getRole())

                .build();
    }

    // =========================
    // REGISTRO DE USUARIOS
    // =========================

   

        // =========================
    // REGISTRO DE USUARIOS
    // =========================

    public void save(User user) {

        // Encripta contraseña
        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        // Guarda usuario
        User savedUser = repo.save(user);

        // =========================
        // SI ES CLIENT
        // =========================

        if (user.getRole()
                .equals("ROLE_CLIENT")) {

            Client client = new Client();

            // Datos personales
            client.setName(user.getName());

            client.setLast_name(
                    user.getLastName()
            );

            client.setEmail(
                    user.getEmail()
            );

            client.setPhone(
                    user.getPhone()
            );

            client.setAddress(
                    user.getAddress()
            );

            client.setDocument(
                    user.getDocument()
            );

            // Datos adicionales
            client.setTypeClient("CLIENT");

            client.setCreditHistory("NEW");

            // Relación User ↔ Client
            client.setUser(savedUser);

            // Guarda cliente
            clientRepo.save(client);
        }
    }
    // =========================
    // VALIDAR USERNAME REPETIDO
    // =========================

    public boolean existsByUsername(
            String username
    ) {

        return repo.findByUsername(username)
                != null;
    }
}