package com.hospital.controller;

import com.hospital.entity.User;
import com.hospital.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository repo;

    @GetMapping
    public List<User> getAll() {
        log.info("Fetching all users");
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        log.info("Fetching user by id={}", id);
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** All staff for a hospital */
    @GetMapping("/hospital/{hospitalId}")
    public List<User> getByHospital(@PathVariable Long hospitalId) {
        log.info("Fetching users for hospitalId={}", hospitalId);
        return repo.findByHospitalIdAndDeletedFalse(hospitalId);
    }

    /** Staff filtered by role (DOCTOR / RECEPTIONIST / ADMIN) */
    @GetMapping("/hospital/{hospitalId}/role/{role}")
    public List<User> getByRole(@PathVariable Long hospitalId,
                                @PathVariable String role) {
        log.info("Fetching users for hospitalId={} and role={}", hospitalId, role);
        return repo.findByHospitalIdAndRoleAndDeletedFalse(hospitalId, role);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody User u) {
        log.info("Saving user with email={}", u.getEmail());
        if (u.getEmail() != null && repo.findByEmail(u.getEmail()).isPresent()&&(u.getContact() != null && repo.findByContact(u.getContact()).isPresent())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email already in use."));
        }else{
            if (u.getContact() != null && repo.findByContact(u.getContact()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email or Contact already in use."));
            }
        }
        if (u.getCreatedDate() == null) {
            u.setCreatedDate(LocalDate.now());
        }
        User saved = repo.save(u);
        log.info("User saved successfully with id={}", saved.getUserId());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User u) {
        log.info("Updating user id={}", id);
        // if password is blank (edit mode), keep existing password
        if (u.getPassword() == null || u.getPassword().isBlank()) {
            repo.findById(id).ifPresent(existing -> u.setPassword(existing.getPassword()));
        }
        u.setUserId(id);
        User updated = repo.save(u);
        log.info("User updated successfully with id={}", updated.getUserId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting user id={}", id);
        repo.findById(id).ifPresent(u -> {
            u.setDeleted(true);
            u.setActive(false);
            repo.save(u);
        });
        return ResponseEntity.ok().build();
    }
}
