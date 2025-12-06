package com.realestate.realestate.service.impl;

import com.realestate.realestate.model.Admin;
import com.realestate.realestate.repository.AdminRepository;
import com.realestate.realestate.service.AdminService;
import com.realestate.realestate.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    // TEMP OTP MEMORY CACHE
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    @Override
    public Admin login(String email, String password) {
        Admin admin = repo.findByEmail(email);

        if (admin != null && encoder.matches(password, admin.getPassword())) {
            return admin;
        }
        return null;
    }

    @Override
    public Admin createAdmin(Admin admin) {

        Admin exists = repo.findByEmail(admin.getEmail());
        if (exists != null) {
            throw new RuntimeException("Admin with this email already exists!");
        }

        admin.setPassword(encoder.encode(admin.getPassword()));

        return repo.save(admin);
    }

    @Override
    public Admin findByEmail(String email) {
        return repo.findByEmail(email);
    }

    // ------------------ FORGOT PASSWORD -----------------------
    @Override
    public ResponseEntity<?> forgotPassword(String email) {
        Admin admin = repo.findByEmail(email);

        if (admin == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Email not found"));
        }

        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStore.put(email, otp);

        emailService.sendEmail(email, "Admin Password Reset OTP",
                "Your OTP is: " + otp + "\nValid for 10 minutes.");

        return ResponseEntity.ok(Map.of("message", "OTP sent to email"));
    }

    // ------------------ RESET PASSWORD -----------------------
    @Override
    public ResponseEntity<?> resetPassword(String email, String otp, String newPassword) {

        if (!otpStore.containsKey(email)) {
            return ResponseEntity.status(400).body("OTP not generated");
        }

        if (!otpStore.get(email).equals(otp)) {
            return ResponseEntity.status(400).body("Invalid OTP");
        }

        Admin admin = repo.findByEmail(email);

        if (admin == null) {
            return ResponseEntity.status(404).body("Admin not found");
        }

        admin.setPassword(encoder.encode(newPassword));
        repo.save(admin);

        otpStore.remove(email);

        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }
}
