package com.jsp.sap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.sap.entity.AccessToken;
import com.jsp.sap.entity.User;

import java.time.LocalDateTime;



public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {
	
	Optional<AccessToken> findByToken(String token);
	
	List<AccessToken> findByIsBlocked(boolean blocked);
	
	Optional<AccessToken> findByTokenAndIsBlocked(String at, boolean b);
	
   List<AccessToken> findByExpirationBefore(LocalDateTime expiration);

List<AccessToken> findAllByUserAndIsBlocked(User user, boolean b);

List<AccessToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String accessToken);
	
}
