package com.jsp.sap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jsp.sap.entity.Customer;
@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer> {

}
