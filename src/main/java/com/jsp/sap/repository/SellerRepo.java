package com.jsp.sap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.sap.entity.Seller;

public interface SellerRepo extends JpaRepository<Seller, Integer> {

}
