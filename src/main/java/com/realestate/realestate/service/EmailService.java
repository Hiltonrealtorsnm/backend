package com.realestate.realestate.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
