package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    // Accessible par tout le monde
    @GetMapping("/public")
    public ResponseEntity<String> getPublic() {
        return ResponseEntity.ok("Hello getPublic");
    }

    // Accessible par tout utilisateur authentifié (USER ou ADMIN)
    @GetMapping("/private")
    public ResponseEntity<String> getPrivate() {
        return ResponseEntity.ok("Hello getPrivate");
    }

    // Accessible uniquement par les utilisateurs avec le rôle ADMIN
    @GetMapping("/private-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getPrivateAdmin() {
        return ResponseEntity.ok("Hello getPrivateAdmin - accessible uniquement par ADMIN");
    }
}