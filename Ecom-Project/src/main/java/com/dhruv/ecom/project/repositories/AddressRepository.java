package com.dhruv.ecom.project.repositories;


import com.dhruv.ecom.project.Model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
