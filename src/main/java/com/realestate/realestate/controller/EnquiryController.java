package com.realestate.realestate.controller;

import com.realestate.realestate.controller.dto.EnquiryRequest;
import com.realestate.realestate.model.Enquiry;
import com.realestate.realestate.model.Property;
import com.realestate.realestate.service.EmailService;
import com.realestate.realestate.service.EnquiryService;
import com.realestate.realestate.service.PropertyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/enquiry")
@CrossOrigin
public class EnquiryController {

    @Autowired
    private EnquiryService enquiryService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private EmailService emailService;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${app.url}")
    private String appUrl;


    /** Buyer sends enquiry */
    @PostMapping("/add")
    public ResponseEntity<?> sendEnquiry(@Valid @RequestBody EnquiryRequest req) {

        Property property = propertyService.getById(req.getPropertyId());
        if (property == null) {
            return ResponseEntity.badRequest().body("Invalid property ID");
        }

        Enquiry enquiry = new Enquiry();
        enquiry.setBuyerName(req.getBuyerName());
        enquiry.setPhone(req.getPhone());
        enquiry.setEmail(req.getEmail());
        enquiry.setMessage(req.getMessage());
        enquiry.setProperty(property);

        Enquiry saved = enquiryService.saveEnquiry(enquiry);

        /* ==========================================================
           SEND EMAIL TO ADMIN WITH PROPERTY LINK + BUYER DETAILS
        ========================================================== */

        String propertyLink = appUrl + "/property/" + property.getPropertyId();

        String subject = "New Enquiry - " + property.getTitle();

        String body =
                "A new enquiry has been submitted\n\n" +

                "PROPERTY DETAILS\n" +
                "--------------------------------\n" +
                "Title: " + property.getTitle() + "\n" +
                "City: " + property.getAddress().getCity() + "\n" +
                "Seller Name: " + property.getSeller().getSellerName() + "\n" +
                "Seller Email: " + property.getSeller().getEmail() + "\n\n" +

                "View Property: " + propertyLink + "\n\n" +

                "BUYER DETAILS\n" +
                "--------------------------------\n" +
                "Name: " + req.getBuyerName() + "\n" +
                "Phone: " + req.getPhone() + "\n" +
                "Email: " + req.getEmail() + "\n\n" +

                "MESSAGE\n" +
                "--------------------------------\n" +
                req.getMessage();

        // Send to ADMIN
        emailService.sendEmail(adminEmail, subject, body);

        // Optional — notify seller also
        // emailService.sendEmail(property.getSeller().getEmail(), subject, body);

        return ResponseEntity.ok(saved);
    }



    /** Admin: get enquiries by property (PAGINATION) */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<?> getEnquiriesByProperty(
            @PathVariable int propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Enquiry> result = enquiryService.findByPropertyId(propertyId, pageable);

        return ResponseEntity.ok(result);
    }



    /** Admin: get all enquiries (PAGINATION) */
    @GetMapping("/all")
    public ResponseEntity<?> getAllEnquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Enquiry> result = enquiryService.findAll(pageable);

        return ResponseEntity.ok(result);
    }
}
