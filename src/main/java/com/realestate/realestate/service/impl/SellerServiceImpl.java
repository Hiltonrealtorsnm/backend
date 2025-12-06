package com.realestate.realestate.service.impl;

import com.realestate.realestate.model.Seller;
import com.realestate.realestate.model.SellerType;
import com.realestate.realestate.repository.SellerRepository;
import com.realestate.realestate.service.SellerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepository repo;

    // ⭐ ADD or UPDATE SELLER BASED ON PHONE NUMBER
    @Override
    public Seller addOrUpdateSeller(Seller seller) {

        Optional<Seller> existingOpt = repo.findByPhone(seller.getPhone());

        if (existingOpt.isPresent()) {
            Seller existing = existingOpt.get();
            boolean updated = false;

            // update email
            if (seller.getEmail() != null &&
                !seller.getEmail().equalsIgnoreCase(existing.getEmail())) {
                existing.setEmail(seller.getEmail());
                updated = true;
            }

            // update name
            if (seller.getSellerName() != null &&
                !seller.getSellerName().equalsIgnoreCase(existing.getSellerName())) {
                existing.setSellerName(seller.getSellerName());
                updated = true;
            }

            // ⭐ update seller type (OWNER or AGENT)
            if (seller.getSellerType() != null &&
                seller.getSellerType() != existing.getSellerType()) {
                existing.setSellerType(seller.getSellerType());
                updated = true;
            }

            if (updated) repo.save(existing);
            return existing;
        }

        // No seller found → create new
        return repo.save(seller);
    }

    @Override
    public Seller getSeller(int sellerId) {
        return repo.findById(sellerId).orElse(null);
    }

    @Override
    public Page<Seller> getAllSellers(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Seller updateSeller(int sellerId, Seller data) {
        Seller existing = repo.findById(sellerId).orElse(null);
        if (existing == null) return null;

        if (data.getSellerName() != null)
            existing.setSellerName(data.getSellerName());

        if (data.getPhone() != null)
            existing.setPhone(data.getPhone());

        if (data.getEmail() != null)
            existing.setEmail(data.getEmail());

        if (data.getSellerType() != null)
            existing.setSellerType(data.getSellerType());

        return repo.save(existing);
    }

    @Override
    public boolean deleteSeller(int sellerId) {
        if (!repo.existsById(sellerId)) return false;
        repo.deleteById(sellerId);
        return true;
    }

    @Override
    public Seller findByPhone(String phone) {
        return repo.findByPhone(phone).orElse(null);
    }

    // ⭐ NEW → update only seller type
    @Override
    public Seller updateSellerType(int sellerId, String sellerType) {
        Seller existing = repo.findById(sellerId).orElse(null);
        if (existing == null) return null;

        // Convert string to enum safely
        SellerType type = SellerType.valueOf(sellerType.toUpperCase());

        existing.setSellerType(type);

        return repo.save(existing);
    }
}
