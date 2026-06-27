package com.hospital.controller;

import com.hospital.entity.Hospital;
import com.hospital.entity.User;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private HospitalRepository hospitalRepo;

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email and password are required."));
        }

        // 1️⃣  Try Hospital Admin login
        Optional<Hospital> hospitalOpt = hospitalRepo.findByEmail(email);
        if (hospitalOpt.isPresent()) {
            Hospital h = hospitalOpt.get();
            if (h.getPassword().equals(password)) {
                Map<String, Object> res = new HashMap<>();
                res.put("id",           h.getId());
                res.put("hospitalId",   h.getId());
                res.put("hospitalName", h.getHospitalName());
                res.put("email",        h.getEmail());
                res.put("contactNo",    h.getContactNo());
                res.put("ownerName",    h.getOwnerName());
                res.put("role",         "HOSPITAL_ADMIN");
                res.put("active",       h.isActive());
                return ResponseEntity.ok(res);
            } else {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Invalid password."));
            }
        }

        // 2️⃣  Try User (Doctor / Receptionist / Sub-Admin) login
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            if (!u.isActive()) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Your account is inactive. Contact admin."));
            }
            if (u.getPassword().equals(password)) {
                // Fetch parent hospital name
                String hospitalName = hospitalRepo.findById(u.getHospitalId())
                        .map(Hospital::getHospitalName).orElse("");

                Map<String, Object> res = new HashMap<>();
                res.put("id",             u.getUserId());
                res.put("userId",         u.getUserId());
                res.put("hospitalId",     u.getHospitalId());
                res.put("hospitalName",   hospitalName);
                res.put("email",          u.getEmail());
                res.put("contactNo",      u.getContact());
                res.put("ownerName",      u.getUserName());
                res.put("role",           u.getRole());
                res.put("specialization", u.getSpecialization());
                res.put("education",      u.getEducation());
                res.put("timing",         u.getTiming());
                return ResponseEntity.ok(res);
            } else {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Invalid password."));
            }
        }

        return ResponseEntity.status(401)
                .body(Map.of("error", "No account found with this email."));
    }
}
