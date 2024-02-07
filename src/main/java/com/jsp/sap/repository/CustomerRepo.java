package com.jsp.sap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.sap.entity.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {

}
