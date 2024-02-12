package com.jsp.sap.security;

import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {
	@Value("${myapp.secret}")
	private String secret;
	@Value("${myapp.access.expiry}")
	private Long accesExpirationInSeconds;
	@Value("${myapp.refresh.expiry}")
	private Long refreshExpirationInSeconds;
	
	
	public String generateAccesToken(String username) {
	return 	generateJwt(new HashMap<String,Object>(), username,accesExpirationInSeconds*1000l);
	}
	public String generateRefreshToken(String username) {
		return 	generateJwt(new HashMap<String,Object>(), username,refreshExpirationInSeconds*1000l);
		}
	private String generateJwt(Map<String, Object> claims,String username,Long expiry) {
		
		return  Jwts.builder()
				.setClaims(claims)
				.setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+ expiry))
				.signWith(getSignature(), SignatureAlgorithm.HS512)//signing Jwt with Key
				.compact();
	}
	
	private Key getSignature() {
		byte[] secretBytes = Decoders.BASE64.decode(secret);
		
		return Keys.hmacShaKeyFor(secretBytes);
		
	}

}
