package com.example.demo.initializer;

import com.example.demo.models.UserApp;
import com.example.demo.repositories.UserAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserAppRepository userAppRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Création d'un utilisateur normal
        if(userAppRepository.findByUsername("user").isEmpty()) {
            userAppRepository.save(new UserApp("user", passwordEncoder.encode("password"), "USER"));
        }

        // Création d'un administrateur
        if(userAppRepository.findByUsername("admin").isEmpty()) {
            userAppRepository.save(new UserApp("admin", passwordEncoder.encode("password"), "ADMIN"));
        }
    }
}