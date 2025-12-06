package com.realestate.realestate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.realestate.model.Seller;
import com.realestate.realestate.model.SellerType;

public interface SellerRepository extends JpaRepository<Seller, Integer> {

    // ✔ Unique seller check
    Optional<Seller> findByPhone(String phone);

    // ⭐ Find by type (OWNER or AGENT)
    List<Seller> findBySellerType(SellerType sellerType);

    // ⭐ Check if phone exists
    boolean existsByPhone(String phone);

    // ⭐ Search by name
    List<Seller> findBySellerNameContainingIgnoreCase(String name);
}
