package com.example.demo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_offer")
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDateTime createdAt;

    // Relation vers l’utilisateur qui a créé l’offre
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private UserApp creator;

    public JobOffer() {
    }

    public JobOffer(String title, String description, LocalDateTime createdAt, UserApp creator) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.creator = creator;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserApp getCreator() {
        return creator;
    }

    public void setCreator(UserApp creator) {
        this.creator = creator;
    }
}