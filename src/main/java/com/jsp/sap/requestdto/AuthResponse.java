package com.jsp.sap.requestdto;

import java.time.LocalDateTime;

import com.jsp.sap.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthResponse {
	int userId;
	String username;
	String role;
	boolean isAuthenticated;
	LocalDateTime accessExpiration;
	LocalDateTime refreshExpiration;
	
	

}
