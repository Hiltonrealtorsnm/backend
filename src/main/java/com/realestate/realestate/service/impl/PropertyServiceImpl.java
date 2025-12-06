package com.realestate.realestate.service.impl;

import com.realestate.realestate.model.Address;
import com.realestate.realestate.model.Property;
import com.realestate.realestate.model.Seller;
import com.realestate.realestate.repository.AddressRepository;
import com.realestate.realestate.repository.PropertyRepository;
import com.realestate.realestate.repository.SellerRepository;
import com.realestate.realestate.service.PropertyService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    private PropertyRepository repo;

    @Autowired
    private SellerRepository sellerRepo;

    @Autowired
    private AddressRepository addressRepo;

    // ==============================================
    // ADD PROPERTY
    // ==============================================
    @Override
    public Property addProperty(Property property) {

        if (property.getSeller() == null || property.getSeller().getSellerId() == 0)
            throw new RuntimeException("Seller ID is required");

        Seller seller = sellerRepo.findById(property.getSeller().getSellerId())
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        property.setSeller(seller);

        if (property.getAddress() == null)
            throw new RuntimeException("Address is required");

        // Clear rent-only fields if listing is NOT rent
        if (!"rent".equalsIgnoreCase(property.getListingType())) {
            property.setMonthlyRent(null);
            property.setSecurityDeposit(null);
        }

        Address addr = property.getAddress();
        addr.setAddressId(0); // force new address insert
        Address savedAddr = addressRepo.save(addr);
        property.setAddress(savedAddr);

        property.setStatus(Property.Status.pending);

        return repo.save(property);
    }

    // ==============================================
    // UPDATE PROPERTY
    // ==============================================
    @Override
    public Property updateProperty(int id, Property updated) {

        Property existing = repo.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setBedrooms(updated.getBedrooms());
        existing.setBathrooms(updated.getBathrooms());
        existing.setAreaSqft(updated.getAreaSqft());
        existing.setFurnishing(updated.getFurnishing());
        existing.setListingType(updated.getListingType());
        existing.setMonthlyRent(updated.getMonthlyRent());
        existing.setSecurityDeposit(updated.getSecurityDeposit());

        if (existing.getAddress() != null && updated.getAddress() != null) {
            existing.getAddress().setHouseNo(updated.getAddress().getHouseNo());
            existing.getAddress().setStreet(updated.getAddress().getStreet());
            existing.getAddress().setArea(updated.getAddress().getArea());
            existing.getAddress().setCity(updated.getAddress().getCity());
            existing.getAddress().setState(updated.getAddress().getState());
            existing.getAddress().setPincode(updated.getAddress().getPincode());
        }

        return repo.save(existing);
    }

    // ==============================================
    // STATUS CHANGES
    // ==============================================
    @Override
    public Property approveProperty(int propertyId) {
        Property p = repo.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        p.setStatus(Property.Status.approved);
        return repo.save(p);
    }

    @Override
    public Property rejectProperty(int propertyId) {
        Property p = repo.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        p.setStatus(Property.Status.rejected);
        return repo.save(p);
    }

    @Override
    public Property markAsSold(int propertyId) {
        Property p = repo.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        p.setStatus(Property.Status.sold);
        return repo.save(p);
    }

    // ==============================================
    // DELETE
    // ==============================================
    @Override
    public boolean deleteProperty(int propertyId) {
        if (!repo.existsById(propertyId)) return false;
        repo.deleteById(propertyId);
        return true;
    }

    // ==============================================
    // GETTERS
    // ==============================================
    @Override
    public Property getById(int propertyId) {
        return repo.findById(propertyId).orElse(null);
    }

    @Override
    public List<Property> getAllByStatus(Property.Status status) {
        return repo.findByStatus(status);
    }

    @Override
    public Page<Property> getAllByStatus(Property.Status status, Pageable pageable) {
        return repo.findByStatus(status, pageable);
    }

    @Override
    public Page<Property> getAllProperties(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Page<Property> getAllByListingType(String listingType, Pageable pageable) {
        return repo.findByListingType(listingType, pageable);
    }

    // ==============================================
    // SEARCH PROPERTIES (Controller-Compatible)
    // ==============================================
    @Override
    public Page<Property> searchProperties(String city, String type,
                                           Double minPrice, Double maxPrice,
                                           Integer bedrooms, Integer bathrooms,
                                           Pageable pageable) {

        return repo.searchProperties(city, type, minPrice, maxPrice,
                bedrooms, bathrooms, pageable);
    }
}
