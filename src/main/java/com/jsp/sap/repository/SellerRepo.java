package com.jsp.sap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jsp.sap.entity.Seller;
@Repository
public interface SellerRepo extends JpaRepository<Seller, Integer> {

}
