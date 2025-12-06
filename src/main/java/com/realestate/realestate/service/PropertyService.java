package com.realestate.realestate.service;

import com.realestate.realestate.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PropertyService {

    Property addProperty(Property property);

    Property updateProperty(int propertyId, Property property);

    Property approveProperty(int propertyId);

    Property rejectProperty(int propertyId);

    Property markAsSold(int propertyId);

    boolean deleteProperty(int propertyId);

    Property getById(int propertyId);

    List<Property> getAllByStatus(Property.Status status);

    Page<Property> getAllByStatus(Property.Status status, Pageable pageable);

    Page<Property> getAllProperties(Pageable pageable);

    Page<Property> getAllByListingType(String listingType, Pageable pageable);

    Page<Property> searchProperties(
            String city,
            String type,
            Double minPrice,
            Double maxPrice,
            Integer bedrooms,
            Integer bathrooms,
            Pageable pageable
    );
}
