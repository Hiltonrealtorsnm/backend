package com.realestate.realestate.service.impl;

import com.realestate.realestate.model.Enquiry;
import com.realestate.realestate.repository.EnquiryRepository;
import com.realestate.realestate.service.EnquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnquiryServiceImpl implements EnquiryService {

    @Autowired
    private EnquiryRepository repo;

    // -------------------------------
    // SAVE ENQUIRY
    // -------------------------------
    @Override
    public Enquiry saveEnquiry(Enquiry enquiry) {
        return repo.save(enquiry);
    }

    // -------------------------------
    // FIND BY PROPERTY (LIST)
    // -------------------------------
    @Override
    public List<Enquiry> findByPropertyId(int propertyId) {
        return repo.findByPropertyPropertyId(propertyId);
    }

    // -------------------------------
    // FIND ALL (LIST)
    // -------------------------------
    @Override
    public List<Enquiry> findAll() {
        return repo.findAll();
    }

    // -------------------------------
    // FIND ALL (PAGINATED)
    // -------------------------------
    @Override
    public Page<Enquiry> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    // -------------------------------
    // FIND BY PROPERTY (PAGINATED)
    // -------------------------------
    @Override
    public Page<Enquiry> findByPropertyId(int propertyId, Pageable pageable) {
        return repo.findByPropertyPropertyId(propertyId, pageable);
    }
}
