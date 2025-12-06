package com.realestate.realestate.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.realestate.model.Enquiry;

public interface EnquiryRepository extends JpaRepository<Enquiry, Integer> {
	 List<Enquiry> findByPropertyPropertyId(int propertyId);
    Page<Enquiry> findByPropertyPropertyId(int propertyId, Pageable pageable);
}
