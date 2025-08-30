package com.example.demo.controller;

import com.example.demo.models.UserApp;
import com.example.demo.repositories.UserAppRepository;
import com.example.demo.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserAppRepository userAppRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    /** LOGIN : vérifie username+password et renvoie un JWT */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserApp userApp) throws Exception {
        Optional<UserApp> userAppOptional = userAppRepository.findByUsername(userApp.getUsername());

        if (userAppOptional.isPresent()) {
            UserApp dbUser = userAppOptional.get();

            // Vérifie le hash Bcrypt
            if (passwordEncoder.matches(userApp.getPassword(), dbUser.getPassword())) {
                ResponseCookie cookie = jwtService.createAuthenticationToken(dbUser);
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body("connected as " + dbUser.getUsername() + " (" + dbUser.getRole() + ")");
            }
        }
        throw new Exception("Invalid credentials");
    }

    /** REGISTER : enregistre un nouvel utilisateur avec mdp hashé */
    @PostMapping("/register")
    public void register(@RequestBody UserApp userApp) throws Exception {
        Optional<UserApp> userAppOptional = userAppRepository.findByUsername(userApp.getUsername());
        if (userAppOptional.isEmpty()) {
            userApp.setPassword(passwordEncoder.encode(userApp.getPassword()));
            if (userApp.getRole() == null) {
                userApp.setRole("USER"); // rôle par défaut
            }
            userAppRepository.save(userApp);
        } else {
            throw new Exception("Username already exists");
        }
    }
}