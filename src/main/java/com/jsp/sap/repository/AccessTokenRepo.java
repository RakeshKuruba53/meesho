package com.jsp.sap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.sap.entity.AccessToken;


public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {
	
	Optional<AccessToken> findByToken(String token);
	List<AccessToken> findByIsBlocked(boolean blocked);
	
}
