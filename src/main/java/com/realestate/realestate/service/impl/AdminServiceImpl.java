package com.realestate.realestate.service.impl;

import com.realestate.realestate.model.Admin;
import com.realestate.realestate.repository.AdminRepository;
import com.realestate.realestate.service.AdminService;
import com.realestate.realestate.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    // ✅ OTP Store with Expiry (email -> OTP + expiry)
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    // ✅ OTP valid for 10 minutes
    private static final long OTP_EXPIRY_MS = 10 * 60 * 1000;

    // ================== LOGIN ==================
    @Override
    public Admin login(String email, String password) {
        Admin admin = repo.findByEmail(email);

        if (admin != null && encoder.matches(password, admin.getPassword())) {
            return admin;
        }
        return null;
    }

    // ================== CREATE ADMIN ==================
    @Override
    public Admin createAdmin(Admin admin) {

        Admin exists = repo.findByEmail(admin.getEmail());
        if (exists != null) {
            throw new RuntimeException("Admin with this email already exists!");
        }

        admin.setPassword(encoder.encode(admin.getPassword()));
        return repo.save(admin);
    }

    // ================== FIND BY EMAIL ==================
    @Override
    public Admin findByEmail(String email) {
        return repo.findByEmail(email);
    }

    // ================== FORGOT PASSWORD ==================
    @Override
    public ResponseEntity<?> forgotPassword(String email) {

        Admin admin = repo.findByEmail(email);

        if (admin == null) {
            return ResponseEntity.status(404).body(
                    Map.of("error", "Email not found")
            );
        }

        // ✅ Generate secure 6-digit OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        // ✅ Store OTP with expiry
        otpStore.put(email, new OtpData(otp, Instant.now().toEpochMilli()));

        // ✅ HTML Email Body (Brevo safe)
        String subject = "Admin Password Reset OTP - Hilton Realtors";

        String body = """
            <div style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>Password Reset OTP</h2>
                <p>Your OTP is:</p>
                <h1 style="letter-spacing: 4px;">%s</h1>
                <p>This OTP is valid for <b>10 minutes</b>.</p>
                <p>If you did not request this, please ignore this email.</p>
            </div>
            """.formatted(otp);

        emailService.sendEmail(email, subject, body);

        return ResponseEntity.ok(
                Map.of("message", "OTP sent to your email")
        );
    }

    // ================== RESET PASSWORD ==================
    @Override
    public ResponseEntity<?> resetPassword(String email, String otp, String newPassword) {

        if (!otpStore.containsKey(email)) {
            return ResponseEntity.status(400).body(
                    Map.of("error", "OTP not generated")
            );
        }

        OtpData data = otpStore.get(email);

        long now = Instant.now().toEpochMilli();

        // ✅ Check OTP Expiry (10 min)
        if (now - data.createdAt > OTP_EXPIRY_MS) {
            otpStore.remove(email);
            return ResponseEntity.status(400).body(
                    Map.of("error", "OTP expired. Please request again.")
            );
        }

        // ✅ OTP Match Check
        if (!data.otp.equals(otp)) {
            return ResponseEntity.status(400).body(
                    Map.of("error", "Invalid OTP")
            );
        }

        Admin admin = repo.findByEmail(email);

        if (admin == null) {
            return ResponseEntity.status(404).body(
                    Map.of("error", "Admin not found")
            );
        }

        // ✅ Secure password update
        admin.setPassword(encoder.encode(newPassword));
        repo.save(admin);

        // ✅ Clear OTP after success
        otpStore.remove(email);

        return ResponseEntity.ok(
                Map.of("message", "Password reset successful")
        );
    }

    // ================== OTP HOLDER ==================
    private static class OtpData {
        String otp;
        long createdAt;

        OtpData(String otp, long createdAt) {
            this.otp = otp;
            this.createdAt = createdAt;
        }
    }
}
