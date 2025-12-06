package com.realestate.realestate.controller;

import com.realestate.realestate.model.Seller;
import com.realestate.realestate.model.SellerType;
import com.realestate.realestate.service.SellerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
@CrossOrigin
public class SellerController {

    @Autowired
    private SellerService service;

    // -------------------------------------------------
    // ADD OR UPDATE SELLER (UPSERT based on phone)
    // -------------------------------------------------
    @PostMapping("/add")
    public ResponseEntity<?> addOrUpdateSeller(@RequestBody Seller seller) {
        Seller saved = service.addOrUpdateSeller(seller);
        return ResponseEntity.ok(saved);
    }

    // -------------------------------------------------
    // CHECK SELLER BY PHONE ONLY
    // -------------------------------------------------
    @GetMapping("/check")
    public ResponseEntity<?> checkSeller(@RequestParam String phone) {
        Seller s = service.findByPhone(phone);
        return ResponseEntity.ok(s); // null if not found
    }

    // -------------------------------------------------
    // GET SELLER BY ID
    // -------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable int id) {
        Seller s = service.getSeller(id);
        if (s == null) return ResponseEntity.badRequest().body("Seller not found");
        return ResponseEntity.ok(s);
    }

    // -------------------------------------------------
    // LIST ALL SELLERS
    // -------------------------------------------------
    @GetMapping("/all")
    public ResponseEntity<?> all(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getAllSellers(pageable));
    }

    // -------------------------------------------------
    // UPDATE SELLER BY ID
    // -------------------------------------------------
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Seller seller) {
        Seller updated = service.updateSeller(id, seller);
        if (updated == null) return ResponseEntity.badRequest().body("Seller not found");
        return ResponseEntity.ok(updated);
    }

    // -------------------------------------------------
    // ⭐ UPDATE ONLY SELLER TYPE (OWNER / AGENT)
    // -------------------------------------------------
    @PutMapping("/{id}/type")
    public ResponseEntity<?> updateSellerType(
            @PathVariable int id,
            @RequestParam String type
    ) {
        try {
            SellerType sellerType = SellerType.valueOf(type.toUpperCase());
            Seller updated = service.updateSellerType(id, type);
            if (updated == null) return ResponseEntity.badRequest().body("Seller not found");

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid seller type. Use: OWNER or AGENT");
        }
    }

    // -------------------------------------------------
    // DELETE SELLER
    // -------------------------------------------------
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        boolean removed = service.deleteSeller(id);
        if (!removed) return ResponseEntity.badRequest().body("Seller not found");
        return ResponseEntity.ok("Deleted successfully");
    }
}
