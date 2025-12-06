package com.realestate.realestate.controller;

import com.realestate.realestate.model.Admin;
import com.realestate.realestate.security.JwtService;
import com.realestate.realestate.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AdminService adminService;

    // ---------------- LOGIN ---------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest req) {

        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        Admin admin = adminService.findByEmail(req.getEmail());

        String token = jwtService.generateToken(
                User.withUsername(admin.getEmail())
                        .password(admin.getPassword())
                        .roles("ADMIN")
                        .build()
        );

        return ResponseEntity.ok(
                Map.of(
                        "adminId", admin.getAdminId(),
                        "email", admin.getEmail(),
                        "name", admin.getName(),
                        "token", token
                )
        );
    }

    // ---------------- CREATE ADMIN ---------------------
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Admin admin) {

        try {
            Admin saved = adminService.createAdmin(admin);
            saved.setPassword(null);
            return ResponseEntity.ok(saved);

        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    // ---------------- FORGOT PASSWORD (OTP) --------------------
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        return adminService.forgotPassword(body.get("email"));
    }

    // ---------------- RESET PASSWORD ----------------------
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        return adminService.resetPassword(
                body.get("email"),
                body.get("otp"),
                body.get("newPassword")
        );
    }
}
