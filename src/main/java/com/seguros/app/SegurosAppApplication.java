package com.seguros.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SegurosAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SegurosAppApplication.class, args);
    }

    // 
    @Bean
    public CommandLineRunner testPassword() {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            System.out.println("HASH: " + encoder.encode("1234"));
        };
    }
}