package com.jsp.sap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jsp.sap.entity.User;

public interface UserRepo extends JpaRepository<User, Integer>{
	boolean existsByEmail(String email);

	Optional<User> findByUserName(String userName);
}
