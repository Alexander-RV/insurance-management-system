package com.seguros.app.controller;

import com.seguros.app.model.User;
import com.seguros.app.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

// Controlador encargado del registro de nuevos usuarios.
 
@Controller
public class UserController {

    // Servicio encargado de la lógica de usuarios
    private final UserService userService;

    // Constructor con inyección de dependencias
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Muestra el formulario de registro
    @GetMapping("/register")
    public String showRegisterForm(Model model) {

        // Envía un objeto vacío al formulario
        model.addAttribute("user", new User());

        return "register";
    }

    // Procesa el registro de un nuevo usuario
    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           Model model) {

        // Verifica si el username ya existe
        if (userService.existsByUsername(user.getUsername())) {

            // Envía un mensaje de error a la vista
            model.addAttribute(
                    "error",
                    "The username already exists."
            );

            // Mantiene los datos del formulario
            model.addAttribute("user", user);

            return "register";
        }

        // Guarda el usuario con contraseña encriptada
        userService.save(user);

        // Redirige al login
        return "redirect:/login";
    }
}