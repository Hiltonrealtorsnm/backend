package com.realestate.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.realestate.realestate.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByEmail(String email);
}
