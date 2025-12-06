package com.realestate.realestate.service;

import com.realestate.realestate.model.Admin;
import org.springframework.http.ResponseEntity;

public interface AdminService {

    Admin login(String email, String password);

    Admin createAdmin(Admin admin);

    Admin findByEmail(String email);

    ResponseEntity<?> forgotPassword(String email);

    ResponseEntity<?> resetPassword(String email, String otp, String newPassword);
}
