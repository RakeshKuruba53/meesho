package com.jsp.sap.serviceimpl;

import org.springframework.web.bind.annotation.RequestMethod;

import com.jsp.sap.entity.User;
import com.jsp.sap.requestdto.UserRequest;
import com.jsp.sap.responsedto.UserResponse;
import com.jsp.sap.service.AuthService;

import lombok.Builder;

public class AuthServiceImpl implements AuthService{
	
	
	private User mapToUserResponse(UserRequest request) {
		return User.builder()
		.email(request.getEmail())
		.password(request.getPassword())
		.userRole(request.getUserRole())
		.build();
		
		
	}

	@Override
	public UserResponse registerUser(UserRequest request) {
		
		User user=mapToUserResponse(request);
		
		
		
		return ;
		
		
		
	}

	

}
