package com.realestate.realestate.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.realestate.realestate.model.Enquiry;

public interface EnquiryService {
    Enquiry saveEnquiry(Enquiry enquiry);

	List<Enquiry> findByPropertyId(int propertyId);

	List<Enquiry> findAll();

	Page<Enquiry> findAll(Pageable pageable);

	Page<Enquiry> findByPropertyId(int propertyId, Pageable pageable);
}
