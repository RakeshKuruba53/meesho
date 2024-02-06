package com.jsp.sap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jsp.sap.entity.User;

public interface AuthRepo extends JpaRepository<User, Integer>{

}
