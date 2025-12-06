package com.realestate.realestate.repository;

import com.realestate.realestate.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Integer> {

    List<Property> findByStatus(Property.Status status);

    Page<Property> findByListingType(String listingType, Pageable pageable);

    Page<Property> findByStatus(Property.Status status, Pageable pageable);

    @Query("""
        SELECT p FROM Property p
        WHERE (:city IS NULL OR LOWER(p.address.city) LIKE LOWER(CONCAT('%', :city, '%')))
          AND (:type IS NULL OR p.listingType = :type)
          AND (:minPrice IS NULL OR 
                (p.listingType = 'sale' AND p.price >= :minPrice) OR
                (p.listingType = 'rent' AND p.monthlyRent >= :minPrice))
          AND (:maxPrice IS NULL OR 
                (p.listingType = 'sale' AND p.price <= :maxPrice) OR
                (p.listingType = 'rent' AND p.monthlyRent <= :maxPrice))
          AND (:bedrooms IS NULL OR p.bedrooms = :bedrooms)
          AND (:bathrooms IS NULL OR p.bathrooms = :bathrooms)
    """)
    Page<Property> searchProperties(
            @Param("city") String city,
            @Param("type") String type,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("bedrooms") Integer bedrooms,
            @Param("bathrooms") Integer bathrooms,
            Pageable pageable
    );
}
