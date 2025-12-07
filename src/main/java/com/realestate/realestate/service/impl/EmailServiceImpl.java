package com.realestate.realestate.service.impl;

import com.realestate.realestate.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ FORCE VERIFIED DOMAIN EMAIL (BREVO)
    private static final String SENDER_EMAIL = "office@hiltonrealtorsnm.com";

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // ✅ true = multipart + HTML enabled
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(SENDER_EMAIL);   // ✅ MUST BE YOUR VERIFIED DOMAIN
            helper.setTo(to);
            helper.setSubject(subject);

            // ✅ true = send as HTML
            helper.setText(body, true);

            mailSender.send(message);

            System.out.println("✅ Email sent successfully to: " + to);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Failed to send email via Brevo: " + e.getMessage());
        }
    }
}
