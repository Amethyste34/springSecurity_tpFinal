package com.example.demo.controller;

import com.example.demo.models.JobOffer;
import com.example.demo.models.UserApp;
import com.example.demo.repositories.JobOfferRepository;
import com.example.demo.repositories.UserAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jobs")
public class JobOfferController {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private UserAppRepository userAppRepository;

    // GET /jobs - accessible par tous
    @GetMapping
    public List<JobOffer> getAll() {
        return jobOfferRepository.findAll();
    }

    // POST /jobs - utilisateur connecté
    @PostMapping
    public ResponseEntity<?> addJob(@RequestBody JobOffer jobOffer) {
        UserApp currentUser = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        jobOffer.setCreator(currentUser);
        jobOffer.setCreatedAt(LocalDateTime.now());
        jobOfferRepository.save(jobOffer);
        return ResponseEntity.ok(jobOffer);
    }

    // DELETE /jobs/{id} - utilisateur ou admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        UserApp currentUser = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<JobOffer> jobOpt = jobOfferRepository.findById(id);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        JobOffer job = jobOpt.get();

        // Vérifie si l'utilisateur est ADMIN ou le créateur
        if (currentUser.getRole().equals("ADMIN") || job.getCreator().getId().equals(currentUser.getId())) {
            jobOfferRepository.delete(job);
            return ResponseEntity.ok("Job deleted");
        } else {
            return ResponseEntity.status(403).body("Forbidden");
        }
    }
}