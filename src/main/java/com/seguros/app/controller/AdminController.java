package com.seguros.app.controller;

import com.seguros.app.model.User;
import com.seguros.app.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controlador temporal encargado de crear
// el administrador principal del sistema

@Controller
public class AdminController {

    // Repositorio de usuarios
    private final UserRepository userRepository;

    // Encriptador de contraseñas
    private final PasswordEncoder passwordEncoder;

    // Constructor con inyección de dependencias
    public AdminController(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Crea el usuario administrador
    @GetMapping("/create-admin")
    public String createAdmin() {

        // Verifica si ya existe el administrador
        User existingAdmin =
                userRepository.findByUsername("admin");

        // Si ya existe, redirecciona al login
        if (existingAdmin != null) {

            return "redirect:/login";
        }

        // Crea nuevo administrador
        User admin = new User();

        // Username del administrador
        admin.setUsername("admin");

        // Contraseña encriptada
        admin.setPassword(
                passwordEncoder.encode("admin123")
        );

        // Rol administrador
        admin.setRole("ROLE_ADMIN");

        // Guarda el usuario
        userRepository.save(admin);

        // Redirecciona al login
        return "redirect:/login";
    }
}