package com.realestate.realestate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.realestate.model.PropertyImage;


public interface PropertyImageRepository extends JpaRepository<PropertyImage, Integer> {
	int countByPropertyPropertyId(int propertyId);
	List<PropertyImage> findByPropertyPropertyIdOrderBySortOrder(int propertyId);

}
