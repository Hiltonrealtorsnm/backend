package com.realestate.realestate.service;

import com.realestate.realestate.model.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerService {

    // Create new seller OR update existing one based on phone
    Seller addOrUpdateSeller(Seller seller);

    Seller getSeller(int sellerId);

    Page<Seller> getAllSellers(Pageable pageable);

    Seller updateSeller(int sellerId, Seller seller);

    boolean deleteSeller(int sellerId);

    // Find seller by phone (unique identifier for seller)
    Seller findByPhone(String phone);

    // Update only seller type (OWNER / AGENT)
    Seller updateSellerType(int sellerId, String sellerType);
}
