package com.realestate.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.realestate.realestate.model.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

}
