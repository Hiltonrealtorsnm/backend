package com.realestate.realestate.controller;

import com.realestate.realestate.model.Property;
import com.realestate.realestate.service.EmailService;
import com.realestate.realestate.service.PropertyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/property")
@CrossOrigin
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private EmailService emailService;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${app.url}")
    private String appUrl;

    /* -------------------------------------------------------
       ADD NEW PROPERTY + SEND EMAIL NOTIFICATION TO ADMIN
    -------------------------------------------------------- */
    @PostMapping("/add")
    public ResponseEntity<?> addProperty(@RequestBody Property property) {

        Property saved = propertyService.addProperty(property);

        // Seller details
        String sellerName = saved.getSeller().getSellerName();
        String sellerEmail = saved.getSeller().getEmail();

        String propertyLink = appUrl + "/admin/property/" + saved.getPropertyId();

        String subject = "New Property Listed: " + saved.getTitle();

        String body = "A new property has been added to your RealEstate Platform:\n\n" +
                "PROPERTY DETAILS\n" +
                "------------------------------\n" +
                "Title: " + saved.getTitle() + "\n" +
                "Type: " + saved.getPropertyType() + "\n" +
                "City: " + saved.getAddress().getCity() + "\n" +
                "Price: ₹" + saved.getPrice() + "\n\n" +

                "SELLER DETAILS\n" +
                "------------------------------\n" +
                "Name: " + sellerName + "\n" +
                "Email: " + sellerEmail + "\n\n" +

                "View Property (Admin Panel): " + propertyLink + "\n\n" +

                "This is an automated notification from RealEstate App.";

        // Send email to admin
        emailService.sendEmail(adminEmail, subject, body);

        return ResponseEntity.ok(saved);
    }



    /* -------------------------------------------------------
       UPDATE PROPERTY
    -------------------------------------------------------- */
    @PutMapping("/update/{propertyId}")
    public ResponseEntity<?> updateProperty(
            @PathVariable int propertyId,
            @RequestBody Property propertyRequest) {

        Property updated = propertyService.updateProperty(propertyId, propertyRequest);
        if (updated == null) {
            return ResponseEntity.badRequest().body("Property not found");
        }

        return ResponseEntity.ok(updated);
    }



    /* -------------------------------------------------------
       GET SINGLE PROPERTY
    -------------------------------------------------------- */
    @GetMapping("/{propertyId}")
    public ResponseEntity<?> getById(@PathVariable int propertyId) {
        Property p = propertyService.getById(propertyId);
        if (p == null)
            return ResponseEntity.badRequest().body("Property not found");

        return ResponseEntity.ok(p);
    }



    /* -------------------------------------------------------
       RENT ONLY
    -------------------------------------------------------- */
    @GetMapping("/rent")
    public ResponseEntity<?> getAllRentProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(propertyService.getAllByListingType("rent", pageable));
    }



    /* -------------------------------------------------------
       APPROVE PROPERTY + SEND EMAIL TO SELLER
    -------------------------------------------------------- */
    @PutMapping("/approve/{propertyId}")
    public ResponseEntity<?> approveProperty(@PathVariable int propertyId) {

        Property updated = propertyService.approveProperty(propertyId);
        if (updated == null) {
            return ResponseEntity.badRequest().body("Property not found");
        }

        String sellerEmail = updated.getSeller().getEmail();
        String sellerName = updated.getSeller().getSellerName();

        String subject = "Your Property Has Been Approved!";
        String body = "Hello " + sellerName + ",\n\n" +
                "Great news! Your property titled '" + updated.getTitle() + "' has been approved and is now visible to buyers.\n\n" +
                "Thank you for using Hilton Realtors.\n\nRegards,\nRealEstate Team";

        emailService.sendEmail(sellerEmail, subject, body);

        return ResponseEntity.ok(updated);
    }



    /* -------------------------------------------------------
       REJECT PROPERTY + SEND EMAIL TO SELLER
    -------------------------------------------------------- */
    @PutMapping("/reject/{propertyId}")
    public ResponseEntity<?> rejectProperty(@PathVariable int propertyId) {

        Property updated = propertyService.rejectProperty(propertyId);
        if (updated == null) {
            return ResponseEntity.badRequest().body("Property not found");
        }

        String sellerEmail = updated.getSeller().getEmail();
        String sellerName = updated.getSeller().getSellerName();

        String subject = "Your Property Listing Was Rejected";
        String body = "Hello " + sellerName + ",\n\n" +
                "We could not approve your property titled '" + updated.getTitle() + "'.\n" +
                "Please review the listing and try again.\n\n" +
                "Regards,\nHilton Realtors Team";

        emailService.sendEmail(sellerEmail, subject, body);

        return ResponseEntity.ok(updated);
    }



    /* -------------------------------------------------------
       MARK AS SOLD + SEND EMAIL TO SELLER
    -------------------------------------------------------- */
    @PutMapping("/sold/{propertyId}")
    public ResponseEntity<?> markAsSold(@PathVariable int propertyId) {

        Property updated = propertyService.markAsSold(propertyId);
        if (updated == null) {
            return ResponseEntity.badRequest().body("Property not found");
        }

        String sellerEmail = updated.getSeller().getEmail();
        String sellerName = updated.getSeller().getSellerName();

        String subject = "Congratulations! Your Property Has Been Sold";
        String body = "Hello " + sellerName + ",\n\n" +
                "Good news! Your property titled '" + updated.getTitle() + "' has been marked as SOLD.\n\n" +
                "Thank you for working with Hilton Realtors.\n\nRegards,\nRealEstate Team";

        emailService.sendEmail(sellerEmail, subject, body);

        return ResponseEntity.ok(updated);
    }



    /* -------------------------------------------------------
       DELETE PROPERTY
    -------------------------------------------------------- */
    @DeleteMapping("/delete/{propertyId}")
    public ResponseEntity<?> deleteProperty(@PathVariable int propertyId) {
        boolean removed = propertyService.deleteProperty(propertyId);
        if (!removed)
            return ResponseEntity.badRequest().body("Property not found");

        return ResponseEntity.ok("Property deleted successfully");
    }



    /* -------------------------------------------------------
       GET ALL PROPERTIES (PAGINATION)
    -------------------------------------------------------- */
    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "propertyId,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sort)));
        Page<Property> list = propertyService.getAllProperties(pageable);
        return ResponseEntity.ok(list);
    }



    /* -------------------------------------------------------
       FILTER BY STATUS
    -------------------------------------------------------- */
    @GetMapping("/status")
    public ResponseEntity<?> getByStatus(
            @RequestParam Property.Status value,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "propertyId,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sort)));
        Page<Property> list = propertyService.getAllByStatus(value, pageable);
        return ResponseEntity.ok(list);
    }



    /* -------------------------------------------------------
       SEARCH PROPERTIES
    -------------------------------------------------------- */
    @GetMapping("/search")
    public ResponseEntity<?> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) Integer bathrooms,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "propertyId,desc") String[] sort
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sort)));

        Page<Property> result = propertyService.searchProperties(
                city, type, minPrice, maxPrice, bedrooms, bathrooms, pageable
        );

        return ResponseEntity.ok(result);
    }



    /** Sort Utility */
    private Sort.Order createSortOrder(String[] sort) {
        if (sort.length == 2) {
            return new Sort.Order(
                    sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                    sort[0]
            );
        }
        return new Sort.Order(Sort.Direction.DESC, "propertyId");
    }
}
