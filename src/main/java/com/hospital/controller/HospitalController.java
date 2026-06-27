package com.hospital.controller;

import com.hospital.entity.Hospital;
import com.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospital")
@CrossOrigin(origins = "*")
public class HospitalController {

    @Autowired
    private HospitalRepository repo;

    @GetMapping
    public List<Hospital> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Hospital h) {
        if (repo.findByEmail(h.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("message", "Email already registered."));
        }
        Hospital saved = repo.save(h);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hospital> update(@PathVariable Long id, @RequestBody Hospital h) {
        h.setId(id);
        return ResponseEntity.ok(repo.save(h));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Legacy login kept for backward compat — real login is via /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> legacyLogin(@RequestBody java.util.Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");
        return repo.findByEmail(email)
                .filter(h -> h.getPassword().equals(password))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401)
                        .body(java.util.Map.of("error", "Invalid credentials.")));
    }
}
