package com.jsp.sap.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.sap.entity.RefreshToken;
import com.jsp.sap.entity.User;
import java.util.List;


public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByToken(String token);
	
	List<RefreshToken> findByIsBlocked(boolean blocked);

}
