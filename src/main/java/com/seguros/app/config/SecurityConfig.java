package com.seguros.app.config;

import com.seguros.app.service.UserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Servicio encargado de cargar usuarios
    private final UserService userService;

    // Constructor con inyección de dependencias
    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }


    // =========================
    // PROVEEDOR DE AUTENTICACIÓN
    // =========================

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            BCryptPasswordEncoder passwordEncoder
    ) {

        // Proveedor de autenticación
        DaoAuthenticationProvider auth =
                new DaoAuthenticationProvider();

        // Servicio que carga usuarios
        auth.setUserDetailsService(userService);

        // Encriptador de contraseñas
        auth.setPasswordEncoder(passwordEncoder);

        return auth;
    }

    // =========================
    // AUTHENTICATION MANAGER
    // =========================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

    // =========================
    // CONFIGURACIÓN DE SEGURIDAD
    // =========================

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http

            // Configuración de rutas y permisos
            .authorizeHttpRequests(auth -> auth

                // =========================
                // RUTAS PÚBLICAS
                // =========================

                .requestMatchers(
                        "/login",
                        "/register",
                        "/create-admin",
                        "/h2/**",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                ).permitAll()

                // =========================
                // SOLO ADMIN
                // =========================

                .requestMatchers(
                        "/clients/delete/**",
                        "/payments/delete/**",
                        "/insurances/delete/**",
                        "/policies/delete/**"
                ).hasAuthority("ROLE_ADMIN")

                // =========================
                // ADMIN Y EMPLOYEE
                // =========================

                .requestMatchers(
                        "/dashboard",
                        "/clients/**",
                        "/payments/**",
                        "/policies/**",
                        "/insurances/**"
                ).hasAnyAuthority(
                        "ROLE_ADMIN",
                        "ROLE_EMPLOYEE",
                        "ROLE_CLIENT"
                )

                // =========================
                // CUALQUIER USUARIO AUTENTICADO
                // =========================

                .anyRequest().authenticated()
            )

            // =========================
            // LOGIN PERSONALIZADO
            // =========================

            .formLogin(form -> form

                // Página personalizada de login
                .loginPage("/login")

                // Redirección después de login exitoso
                .defaultSuccessUrl("/dashboard", true)

                .permitAll()
            )

            // =========================
            // LOGOUT
            // =========================

            .logout(logout -> logout.permitAll());

        // =========================
        // CONFIGURACIÓN H2
        // =========================

        // Deshabilita CSRF para H2
        http.csrf(csrf -> csrf.disable());

        // Permite frames para H2 Console
        http.headers(headers ->
                headers.frameOptions(
                        frame -> frame.disable()
                )
        );

        return http.build();
    }
}